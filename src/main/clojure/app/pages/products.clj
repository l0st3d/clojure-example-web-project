(ns app.pages.products
  (:require [net.cgrand.enlive-html :as html]
	    [app.domain.access :as db]
	    [util.page-elements :as el]
	    [util.convert :as convert]))

(def templates {:show "web/templates/products/show.html"
		:index "web/templates/products/index.html"})

(def page-size 10)

(html/defsnippet product-table-row
  (:index templates) [:table.product_list :> :tbody :> :tr]
  [product]
  [:.product_name] (html/content (el/link (str "/product/" (:id product)) (:name product)))
  [:.product_desc] (html/content (:description product))
  [:.product_price] (html/content (-> product :price el/format-price)))

(defn total-count []
  "SELECT FOUND_ROWS() AS rows")

(html/deftemplate index (:index templates)
  [request]
  [:table.product_list :> :tbody] (html/content (map product-table-row (db/find-page-of-products page-size (-> request :params :page convert/string-to-int (or 0) (* page-size)))))
  [:ul.pagination] (html/substitute nil))

(html/defsnippet purchase-form
  (:show templates) [:form.purchase_form]
  [product]
  [:form] (html/set-attr :action (str "/product/" (:id product) "/buy")))

(html/deftemplate render-show-page (:show templates)
  [product]
  [:h1] (html/content (str "Product " (:id product) " - " (:name product)))
  [:.product_name] (html/content (:name product))
  [:.product_description] (html/content (:description product))
  [:.product_price] (html/content (-> product :price el/format-price))
  [:form.purchase_form] (html/substitute (purchase-form product)))

(defn show [request]
  (let [product (db/find-product (-> request :params (get "id")))]
    (render-show-page product)))

(defn buy [request]
  (let [product (db/find-product (-> request :params (get "id")))]
    (render-show-page product)))