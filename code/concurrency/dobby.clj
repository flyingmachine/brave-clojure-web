(def ^:dynamic *notification-address* "dobby@elf.org")
(binding [*notification-address* "test@elf.org"]
  *notification-address*)

(binding [*notification-address* "test@elf.org"]
  (future (wait 100 (println *notification-address*))))
(println *notification-address*)


(defn send-email
  [message]
  (str "TO: " *notification-address* "\n"
       "MESSAGE: " message))

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

