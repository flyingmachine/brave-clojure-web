(set-env!
 :dependencies '[[org.clojure/clojure   "1.7.0"]])


(defn scan
  [[x y & xs]]
  (if y
    (cons x (scan (cons (+ x y) xs)))
    (list x)))
