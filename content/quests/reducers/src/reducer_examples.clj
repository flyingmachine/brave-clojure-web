(ns reducer-examples
  (:require [clojure.core.reducers :as r]))

;;----- Just for printing out times for easy comparison
(defn pretty-time
  [expr]
  `(let [start# (. System (nanoTime))
         runs#  4]
     (dotimes [n# runs#] ~expr)
     (prn (format "%8.2f %s"
                  (/ (double (- (. System (nanoTime)) start#)) 1000000.0 runs#)
                  (quote ~expr)))))

(defmacro times
  [& exprs]
  `(do ~@(map pretty-time exprs)))

;;----- Simple operations
(def snums  (range 10000000))
(def snumsv (vec snums))

(defn t1 [x]
  (times (r/fold + x)
         (r/reduce + x)
         (reduce + x)))

(defn t2 [x]
  (times (->> x (r/map inc) (r/fold +))
         (->> x (r/map inc) (r/reduce +))
         (->> x (pmap inc)  (reduce +))
         (->> x (map inc)   (reduce +))))

(defn t3 [x]
  (times (->> x (r/filter even?) (r/map inc) (r/fold +))
         (->> x (r/filter even?) (r/map inc) (r/reduce +))
         (->> x (filter even?)   (pmap inc)  (reduce +))
         (->> x (filter even?)   (map inc)   (reduce +))))


;;----- More computationally intensive ops
(def pnums  (range 10000))
(def pnumsv (vec pnums))

(defn primes [n]
  (if (zero? n)
    #{}
    (loop [n n
           factor 2
           factors #{}]
      (if (= n 1)
        factors
        (if (zero? (mod n factor))
          (recur (/ n factor) factor (conj factors factor))
          (recur n (+ factor 1) factors))))))

(defn add-primes [n]
  (reduce + (primes n)))

(defn p1 [x]
  (times (->> x (r/map add-primes) (r/fold +))
         (->> x (r/map add-primes) (r/reduce +))
         (->> x (pmap add-primes)  (reduce +))
         (->> x (map add-primes)   (reduce +))))

(defn p2 [x]
  (times (->> x (r/filter even?) (r/map add-primes) (r/fold +))
         (->> x (r/filter even?) (r/map add-primes) (r/reduce +))
         (->> x (filter even?)   (pmap add-primes)  (reduce +))
         (->> x (filter even?)   (map add-primes)   (reduce +))))
;;-----
