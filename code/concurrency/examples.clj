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

@(-> (future (println 1))
     (queue (future (Thread/sleep 1000) (println 2)))
     (queue (future (Thread/sleep 100) (println 3))))



