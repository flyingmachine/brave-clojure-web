(defn clean-chars
  [text]
  (reduce #(string/replace %1 (first %2) (second %2))
          text
          [[#"[‘`’¿]" "'"]
           [#"[“]", "\""]
           [#"[—]", "&mdash;"]
           [#"•", "&bull;"]]))

(defn clean-description
  [description]
  (if-not (empty? description)
    (-> description
        clean-chars
        tidy
        html->md)))

(defn dirty-html->clean-md
  [dirty-html]
  (html->md (tidy (clean-chars dirty-html))))

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

(defn analyzed-patients
  [patients]
  (loop [remaining-patients patients
         analyzed []]
    (let [current-patient (first remaining-patients)]
      (cond (empty? remaining-patients)
            analyzed
            
            (analyzed? current-patient)
            (recur (rest remaining-patients)
                   (conj analyzed current-patient))

            :else
            (recur (rest remaining-patients)
                   analyzed)))))

(-> "My boa constrictor is so sassy lol!  "
    clojure.string/trim
    (str "!!!"))

(spit "read_and_feel_giddy.txt"
      (str 
       (clojure.string/trim "My boa constrictor is so sassy lol!  ")
       "!!!"))