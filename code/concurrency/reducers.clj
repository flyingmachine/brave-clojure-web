(require '[clojure.core.reducers :as r])

(def alphabet-length 26)
(def alpha-offset (partial + 65))
;; seq of chars, A-Z
(def letters (mapv (comp str char alpha-offset) (range alphabet-length)))
(defn random-letter
  "returns a random upper-case letter"
  []
  (get letters (int (rand alphabet-length))))
(defn random-string
  "returns a random string of specified length"
  [length]
  (apply str (take length (repeatedly random-letter))))

(defn random-string-list
  "returns a fully realized list of random strings"
  [list-length string-length]
  (doall (take list-length (repeatedly (partial random-string string-length)))))

(def orc-names (vec (random-string-list 1000 6000)))

(defn str->ints
  [str]
  (map int str))

(defn ints->str
  [col]
  (apply str (map char col)))

(defn generate-orc-passwords
  [orc-names]
  (->> orc-names
       (map str->ints)
       (map ints->str)
       (map str->ints)
       (map (partial reduce +))))

(defn time-reduce
  []
  (time (reduce + (generate-orc-passwords orc-names))))



(defn rstr->ints
  [str]
  (r/map int str))

(defn rints->str
  [col]
  (apply str (r/map char col)))

(defn rgenerate-orc-passwords
  [orc-names]
  (->> orc-names
       (r/map str->ints)
       (r/map ints->str)
       (r/map str->ints)
       (r/map (partial r/fold +))))

(defn time-fold
  []
  (time (r/fold + (rgenerate-orc-passwords orc-names))))
