(ns app.domain.access
  (:require [clojureql.core :as cql]
	    [infrastructure.db :as db]))

(defn extract-map [data prefix]
  (reduce #(let [k (name (key %2))]
	     (if (.matches k (str prefix ".*"))
	       (assoc %1 (keyword (.replaceAll k (str "^" prefix) ""))
		      (val %2))
	       %1)) {} data))

(defn- collect [prefix extract-element]
  (fn [coll data]
    (let [prev-element (or (first coll) {})]
      (if (= (:id prev-element) (:id data))
	(conj (rest coll) (extract-element data prev-element))
	(conj coll (extract-element data (extract-map data prefix)))))))

(def products (-> (cql/table db/spec :products)
		  (cql/project [[:id :as :product_id]
				[:name :as :product_name]
				[:description :as :product_description]])))

;; (def customers (-> ))

(defn find-page-of-products [page-size offset]
  (let [product-data @(-> products
			  (cql/drop offset)
			  (cql/take page-size))]
    (reduce (collect "product_" (extract-map)) [] product-data)))
