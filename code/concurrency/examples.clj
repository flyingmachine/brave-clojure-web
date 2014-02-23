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
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(-> (future (wait 2000 (println 1)))
    (queue (wait 1000 (println 2)))
    (queue (wait 500 (println 3)))
    (queue (wait 250 (println 4))))

;; Yak butter

(def yak-butter-international
  {:store "Yak Butter International"
    :price 90
    :smoothness 90})
(def butter-than-nothing
  {:store "Butter than Nothing"
   :price 150
   :smoothness 83})
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

(let [butter-promise (promise)]
  (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
    (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
              (deliver butter-promise satisfactory-butter))))
  (println "And the winner is:" @butter-promise))


(time (some (comp satisfactory? mock-api-call)
            [yak-butter-international butter-than-nothing baby-got-yak]))


(let [ferengi-wisdom-promise (promise)]
  (future (println "Here's some Ferengi wisdom:" @ferengi-wisdom-promise))
  (Thread/sleep 100)
  (deliver ferengi-wisdom-promise "Whisper your way to success."))


(def jackson-5-delay
  (delay (let [message "Just call my name and I'll be there"]
           (println "First deref:" message)
           message)))

(defn append-to-file
  [filename s]
  (spit filename s :append true))

(defn format-quote
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn snag-quotes
  [n filename]
  (dotimes [_ n]
    (->> (slurp "http://www.iheartquotes.com/api/v1/random")
         format-quote
         (append-to-file filename)
         (future))))

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(defmacro queue
  [q concurrent-promise-name & work]
  (let [concurrent (butlast work)
        serialized (last work)]
    `(let [~concurrent-promise-name (promise)]
       (future (deliver ~concurrent-promise-name (do ~@concurrent)))
       (deref ~q)
       ~serialized
       ~concurrent-promise-name)))


(time @(-> (future (wait 200 (println "'Ello, gov'na!")))
           (queue line (wait 400 "Pip pip!") (println @line))
           (queue line (wait 100 "Cheerio!") (println @line))))


(defn queue-quote
  [filename])

(defn random-quote
  []
  (format-quote (slurp "http://www.iheartquotes.com/api/v1/random")))

(defmacro snag-quotes-queued
  [n filename]
  (let [quote-gensym (gensym)
        queue-line `(queue ~quote-gensym
                           (random-quote)
                           (append-to-file ~filename @~quote-gensym))]
    `(-> (future)
         ~@(take n (repeat queue-line)))))
