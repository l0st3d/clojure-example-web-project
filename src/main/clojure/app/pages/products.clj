(ns app.pages.products
  (:require [net.cgrand.enlive-html :as html]
	    [app.domain.access :as db]
	    [util.convert :as convert]))

(def templates {:index "web/templates/products/index.html"})

(def page-size 10)

(html/defsnippet product-table-row
  (:index templates) [:table.product_list :> :tbody :> :tr]
  [product]
  [:.product_name] (html/content (str product))
  [:.product_desc] (html/content (str product))
  [:.product_price] (html/content (str product)))

(html/deftemplate index (:index templates)
  [request]
  [:table.product_list :> :tbody] (html/content (map product-table-row (db/find-page-of-products page-size (-> request :params :page convert/string-to-int (or 0) (* page-size))))))
