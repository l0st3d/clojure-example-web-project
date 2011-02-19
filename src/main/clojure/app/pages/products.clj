(ns app.pages.products
  (:require [net.cgrand.enlive-html :as html]))

(def templates {:index "web/templates/products/index.html"})

(html/defsnippet product-table-row
  (:index templates) [:table.product_list :> :tbody :> :tr]
  [product]
  [:.product_name] (html/content "name")
  [:.product_desc] (html/content "desc")
  [:.product_price] (html/content "10.99"))

(html/deftemplate index (:index templates)
  [request]
  [:table.product_list :> :tbody] (html/content (map product-table-row [1 2 3])))
