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

(defn numeric-value
  [str]
  (map int str))

(defn ->letters
  [col]
  (apply str (map char col)))

(defn generate-orc-password
  [orc-name]
  )

(defn int->letters
  [i]
  (apply str (map (comp char alpha-offset int) (str i))))

(def orc-names (vec (random-string-list 1000 6000)))

(defn time-fold
  []
  (time (r/fold + (r/map numeric-value orc-names))))
