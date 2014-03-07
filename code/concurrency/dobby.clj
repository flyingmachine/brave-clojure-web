(def ^:dynamic *notification-address* "dobby@elf.org")
(binding [*notification-address* "test@elf.org"]
  *notification-address*)

(binding [*notification-address* "tester-1@elf.org"]
  (println *notification-address*)
  (binding [*notification-address* "tester-2@elf.org"]
    (println *notification-address*))
  (println *notification-address*))

(binding [*notification-address* "test@elf.org"]
  (future (wait 100 (println *notification-address*))))
(println *notification-address*)


(defn notify
  [message]
  (str "TO: " *notification-address* "\n"
       "MESSAGE: " message))

(binding [*notification-address* "test@elf.org"]
  (notify "test!"))

(defn repl-printing-thread
  [thread-fn]
  (let [out *out*]
    (Thread. #(binding [*out* out]
                (thread-fn)))))

(binding [*notification-address* "test@elf.org"]
  ;; This manually creates a Java thread
  (doto (repl-printing-thread #(println "I see the original value of *notification-address*:" *notification-address*))
    .start
    .join)
  (println "I see the bound value:" *notification-address*))



(binding [*out* (clojure.java.io/writer "print-output")]
  (println "A man who carries a cat by the tail learns 
something he can learn in no other way.
-- Mark Twain"))


(println ["Print" "all" "the" "items!"])
(binding [*print-length* 1]
  (println ["Print" "just" "one!"]))

(def ^:dynamic *troll-thought* nil)
(defn troll-riddle
  [your-answer]
  (let [number "man meat"]
    (when (thread-bound? #'*troll-thought*)
      (set! *troll-thought* number))
    (if (= number your-answer)
      "TROLL: You can cross the bridge!"
      "TROLL: Time to eat you, succulent human!")))

(binding [*troll-thought* nil]
  (println (troll-riddle 2))
  (println "SUCCULENT HUMAN: Oooooh! The answer was" *troll-thought*))


;; prints output to repl:
(.write *out* "prints to repl")
; => prints to repl

;; doesn't print output to repl because *out* is not bound to repl printer:
(.start (Thread. #(.write *out* "prints to standard out")))


(let [out *out*]
  (.start
   (Thread. #(binding [*out* out]
               (.write *out* "prints to repl from thread")))))
