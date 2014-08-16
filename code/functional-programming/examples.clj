(defn no-mutation
  [x]
  ;; = is a boolean operation
  (= x 3)
  (println x)

  (let [x "Kafka Man"]
    (println x))

  (println x))

(defn sum
  ([vals]
     (sum vals 0))
  ([vals acc]
     (if (empty? vals)
       acc
       (sum (rest vals) (+ (first vals) acc)))))

(defn sum
  ([vals]
     (sum vals 0))
  ([vals acc]
     (loop [vals vals
            acc acc]
       (if (empty? vals)
         acc
         (recur (rest vals) (+ (first vals) acc))))))


(-> "My boa constrictor is so sassy lol!  "
    clojure.string/trim
    (str "!!!"))

(defn abs
  "Absolute value of a number"
  [x]
  (if (< x 0)
    (* x -1)
    x))

(comp inc *)

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})

(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))


(defn spell-slots
  "Calculates number of spell slots based on intelligence"
  [char]
  (int (inc  (/ (c-int char) 2))))

(def spell-slots-comp (comp int inc #(/ % 2) c-int))

(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 1000)
  x)

(def memo-sleep-identity (memoize sleepy-identity))
