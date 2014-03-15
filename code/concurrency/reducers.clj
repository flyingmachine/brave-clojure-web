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

(def orc-names (vec (random-string-list 80000 200)))

(defn expected-progeny-for-orc
  [name]
  (apply + (map int (take 20 name))))

(defn filtered-expected-progeny
  [orc-names regex-filter]
  (->> orc-names
       (filter #(re-find regex-filter %))
       (map expected-progeny-for-orc)))

(defn time-reduce
  []
  (time (reduce + (filtered-expected-progeny orc-names #"^A"))))

(defn time-fm
  []
  (time (dorun (filtered-expected-progeny orc-names #"^A"))))

;;;
(defn rfiltered-expected-progeny
  [orc-names regex-filter]
  (->> orc-names
       (r/filter #(re-find regex-filter %))
       (r/map expected-progeny-for-orc)))

(defn time-fold
  []
  (time (r/fold + (rfiltered-expected-progeny orc-names #"^A"))))

(defn time-rmf
  []
  (time (dorun (into [] (rfiltered-expected-progeny orc-names #"^A")))))

;;;
(defn pfiltered-expected-progeny
  [orc-names regex-filter]
  (->> orc-names
       (filter #(re-find regex-filter %))
       (pmap expected-progeny-for-orc)))

(defn time-pmap
  []
  (time (reduce + (pfiltered-expected-progeny orc-names #"^A"))))


(defn bad+
  [x y]
  (+ x y))


(defn divisible-by?
  [x]
  (fn [y] (zero? (mod y x))))
