(ns app.routes
  (:require [compojure.core :as c]
	    [app.pages.products :as products-page]))

(c/defroutes routes
  (c/GET "/" request (products-page/index request)))