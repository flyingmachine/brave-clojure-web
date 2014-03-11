(def alphabet-length 26)
;; seq of chars, A-Z
(def letters (mapv (comp str char (partial + 65)) (range alphabet-length)))
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

(def orc-names (random-string-list 3000 7000))

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel
  overhead worthwhile"
  [grain-size f & colls]
  (apply concat
   (apply pmap
          (fn [& pgroups]
            (doall (apply map f pgroups)))
          (map (partial partition-all grain-size) colls))))

(def numbers [1 2 3 4 5 6 7 8 9 10])

(pmap (fn [number-group] (doall (map inc number-group)))
      (partition-all 3 numbers))

(apply concat
       (pmap (fn [number-group] (doall (map inc number-group)))
             (partition-all 3 numbers)))


(def orc-name-abbrevs (random-string-list 20000 300))
(time (dorun (map clojure.string/lower-case orc-name-abbrevs)))
(time (dorun (pmap clojure.string/lower-case orc-name-abbrevs)))
(time
 (dorun
  (apply concat
         (pmap (fn [name] (doall (map clojure.string/lower-case name)))
               (partition-all 1000 orc-name-abbrevs)))))
