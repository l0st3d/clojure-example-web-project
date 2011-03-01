(ns app.pages.customers
  (:require [net.cgrand.enlive-html :as html]
	    [app.domain.access :as db]
	    [util.page-elements :as el]))

(defn logout-page [request])

(defn render-login-page [request])

(defn login-page [request]
  (if (-> request :session :logged-in-user)
    (logout-page request)
    (render-login-page request)))

(defn login [request])