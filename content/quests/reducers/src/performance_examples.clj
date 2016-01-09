(ns performance-examples
  (:require [clojure.core.reducers :as r]))

;;----- Just for printing out times for easy comparison

;; Number of times to run a code snippet
(def runs 4)

(defn runs-and-pauses
  "Return a bunch of functions that time execution when called"
  [expr]
  (->> (repeat runs expr)
       (interleave (repeat '(Thread/sleep 50)))
       (map (fn [form]
              `(fn []
                 (let [start# (. System (nanoTime))]
                   ~form
                   (- (. System (nanoTime)) start#)))))))

(defn pretty-time
  "Give the times macro a form that executes the given expression
  _runs_ times, pausing 100ms between each run"
  [expr]
  (let [exprs (runs-and-pauses expr)]
    `(let [times#    (->> (list ~@exprs)
                          (drop 1)
                          (map (fn [f#] (f#)))
                          (take-nth 2))
           avg-time# (/ (double (reduce + times#))
                        ~runs)]
       (prn (format "%8.2f %s"
                    (/ avg-time# 1000000.0)
                    (quote ~expr))))))

(defmacro times
  "Prints a pretty time for each expression, waiting 100 ms between exprs"
  [& exprs]
  `(do ~@(butlast (interleave (map pretty-time exprs)
                              (repeat '(Thread/sleep 100))))))


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
         (->> x (map inc)   (reduce +))))

(defn t3 [x]
  (times (->> x (r/map inc) (r/filter even?) (r/fold +))
         (->> x (r/map inc) (r/filter even?) (r/reduce +))
         (->> x (map inc)   (filter even?)   (reduce +))))


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
         (->> x (map add-primes)   (reduce +))))

(defn p2 [x]
  (times (->> x (r/filter even?) (r/map add-primes) (r/fold +))
         (->> x (r/filter even?) (r/map add-primes) (r/reduce +))
         (->> x (filter even?)   (map add-primes)   (reduce +))))


