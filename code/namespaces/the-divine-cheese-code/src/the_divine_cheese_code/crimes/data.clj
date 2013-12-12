(ns the-divine-cheese-code.crimes.data)

(def crimes
  {:cheese-stolen ""
   :location {:lat :lng}})

(defn locations
  [crimes]
  (map :location crimes))