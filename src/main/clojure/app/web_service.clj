(ns app.web-service
  (:require [compojure.core :as compojure]
	    [clojure.contrib.logging :as log]
	    [clojure.pprint :as p]
	    [ring.adapter.jetty :as jetty]
	    [ring.middleware.session :as session]
	    [ring.middleware.cookies :as cookies]
	    [ring.middleware.keyword-params :as keyword-params]
	    [ring.middleware.params :as params]
	    [ring.middleware.file :as file]
	    [ring.middleware.file-info :as file-info]
	    [ring.middleware.multipart-params :as multipart-params]
	    [app.routes :as routes]))

(def webserver-instance (ref nil))

(defn wrap-stacktrace-and-log [f]
  (fn [req]
    (try
      (f req)
      (catch Throwable e
	(log/error (with-out-str
		     (println "Error from request : " (.getMessage e))
		     (try (p/pprint req)
			  (catch Exception e
			    (println "UNPRINTABLE REQUEST")))
		     (println "Stacktrace :")) e)
	{:status 500
	 :body "There was a problem with processing your request."}))))

(defn wrap-response-headers [handler]
  (fn [request]
    (let [response (handler request)
	  headers (:headers response)]
      (if (-> response :headers (get "Content-Type"))
	response
	(assoc-in response [:headers "Content-Type"] "text/html; charset=UTF-8")))))

(def webapp (-> (var routes/routes)
		wrap-response-headers
		session/wrap-session
		keyword-params/wrap-keyword-params
		params/wrap-params
		multipart-params/wrap-multipart-params
		cookies/wrap-cookies
		(file/wrap-file "src/main/public")
		(file/wrap-file "target/resources/web/public")
		file-info/wrap-file-info
		wrap-stacktrace-and-log))

(defn- assign-webserver [server]
  (dosync (ref-set webserver-instance server)))

(defn start-webservice []
  (let [thread-name (str (ns-name *ns*) " Jetty Server")]
    (.start (Thread. #(jetty/run-jetty webapp { :port 8888 :configurator assign-webserver}) thread-name))))

(defn stop-webservice []
  (.stop @webserver-instance))