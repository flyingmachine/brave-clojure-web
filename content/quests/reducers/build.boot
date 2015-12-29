(set-env!
 :dependencies '[[org.clojure/clojure   "1.7.0"]])


(defn scan
  [f [x y & xs]]
  (if y
    (cons x (scan f (cons (f x y) xs)))
    (list x)))
