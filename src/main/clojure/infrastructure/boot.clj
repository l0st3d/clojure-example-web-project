(ns infrastructure.boot
  (:require
   [app.web-service :as app]
   swank.swank
   [clojure.contrib.logging :as log]
   [clojure.contrib.find-namespaces :as find-ns]
   [mycroft.main :as mycroft])
  (:gen-class))

(defn start-mycroft []
  (let [port 8008]
    (log/info (str "loading mycroft inspector on port " port))
    (mycroft/run port)))

(defn start-repl []
  (log/info "starting REPL")
  (swank.swank/start-repl))

(defn start-web-service []
  (log/info "starting web service ...")
  (app/start-webservice))

(defn -main [ & args]
  (start-mycroft)
  (start-repl)
  (start-web-service))
