(ns util.convert)

(defn string-to-int [string]
  (try
    (Long/valueOf string)
    (catch NumberFormatException e
      nil)))
