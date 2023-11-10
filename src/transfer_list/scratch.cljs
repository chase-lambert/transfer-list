(ns transfer-list.scratch)

(def items [1 2 3 4])

(def map-items
  (map (fn [val] 
         {:value val
          :checked false
          :parent :left})
       items))

(zipmap items map-items)
