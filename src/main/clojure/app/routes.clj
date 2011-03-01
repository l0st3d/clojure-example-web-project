(ns app.routes
  (:require [compojure.core :as c]
	    [app.pages.products :as products-page]
	    [app.pages.customers :as customers-page]))

(c/defroutes routes
  (c/GET "/" request (products-page/index request))
  (c/GET "/product/:id" request (products-page/show request))
  (c/POST "/product/:id/buy" request (products-page/buy request))
  (c/GET "/login" request (customers-page/login-page request)))