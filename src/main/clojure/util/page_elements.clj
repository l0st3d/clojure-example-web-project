(ns util.page-elements)

(defn format-price [price]
  (String/format "%.2f" (to-array [(double (/ price 100))])))

(defn link [href text]
  {:tag :a :attrs {:href href} :content text})