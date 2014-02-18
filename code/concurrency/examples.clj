(let [result (future (println "this prints once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  (println "@: " @result))

(let [result (future (Thread/sleep 3000)
                     (+ 1 1))]
  (println "The result is: " @result)
  (println "It will be at least 3 seconds before I print"))


(deref (future (Thread/sleep 1000) 0)
       10
       5)
; => 5

(defmacro queue
  [queue & task]
  `(future @~queue ~@task))

(defmacro wait
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(-> (future (wait 2000 (println 1)))
    (queue (wait 1000 (println 2)))
    (queue (wait 500 (println 3)))
    (queue (wait 250 (println 4))))

;; Yak butter

(def sites ["http://yak-butter-international.com"
            "http://butter-than-nothing.com"
            "http://yak-attack.com"
            "http://baby-got-yak.com"])

(defn generate-yak-butter
  "Yak butter with random smoothness and price for site"
  [id site]
  {:id id
   :smoothness (inc (int (rand 100)))
   :price (inc (int (rand 1000)))
   :url (str site "/products/" id)})

(defn yak-butter-generator
  "Create lazy list of random yak butters for site"
  [site]
  (iterate (fn [x] (generate-yak-butter (inc (:id x)) site))
           (generate-yak-butter 0 site)))

(defn yak-butters
  "Return a lazy list of yak butters for this site. Sleep 1 seconds to
  simulate network connection"
  [site n]
  (Thread/sleep 1000)
  (take n (yak-butter-generator site)))

(defn satisfactory?
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

(let [butter-promise (promise)]
  (doseq [site sites]
    (future (if-let [satisfactory-butter (some satisfactory? (yak-butters site 250000))]
              (deliver butter-promise satisfactory-butter))))
  (println "And the winner is:" @butter-promise))

