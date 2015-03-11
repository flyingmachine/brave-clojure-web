(defmacro infix
  "Use this macro when you're too indie for prefix notation"
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))

(macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))

(if boolean-expression
  (do expression-1
      expression-2
      expression-3))

(defmacro infix-2
  [[operand1 op operand2]]
  (list op operand1 operand2))

(defmacro my-print-whoopsie
  [expression]
  (list let [result expression]
        (list println result)
        result))

(defmacro my-print
  [expression]
  (list 'let ['result expression]
        (list 'println 'result)
        'result))

(let [result expression]
  (println result)
  result)

(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(code-critic (1 + 1) (+ 1 1))

;; syntax quoted
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))

`(blarg# blarg#)

(macroexpand '(when (the-cows-come :home)
                (call me :pappy)
                (slap me :silly)))
(if (the-cows-come :home)
  (do (call me :pappy)
      (slap me :silly)))

(defmacro unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(macroexpand '(unless (done-been slapped? me)
                      (slap me :silly)
                      (say "I reckon that'll learn me")))


;; simple syntax quote
(let [x 1 y 2]
  `(+ ~x ~y))
(clojure.core/+ 1 2)

(defmacro code-praiser
  [code]
  (list 'println
        "Sweet gorilla of Manila, this is good code:"
        (list 'quote code)))

;;
(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manila, this is good code:"
    (quote ~code)))

(defmacro code-makeover
  [code]
  `(println "Before: " (quote ~code))
  `(println "After: " (quote ~(reverse code))))


;; first step
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic
  [bad good]
  `(do ~(criticize-code "Great squid of Madrid, this is bad code:" bad)
       ~(criticize-code "Sweet gorilla of Manila, this is good code:" good)))

;; second step

(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(map #(apply criticize-code %)
             [["Great squid of Madrid, this is bad code:" bad]
              ["Sweet gorilla of Manila, this is good code:" good]])))

(do
 ((clojure.core/println "criticism" '(1 + 1))
  (clojure.core/println "criticism" '(+ 1 1))))

(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~@(map #(apply criticize-code %)
              [["Sweet lion of Zion, this is bad code:" bad]
               ["Great cow of Moscow, this is good code:" good]])))

;; Final
(def criticisms {:good "Sweet manatee of Galilee, this is good code:"
                 :bad "Sweet giant anteater of Santa Anita, this is bad code:"})

(defn criticize-code
  [[criticism-key code]]
  `(println (~criticism-key criticisms) (quote ~code)))

(defmacro code-critic
  [code-evaluations]
  `(do ~@(map criticize-code code-evaluations)))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

;; Variable capture
(def message "Good job!")
(defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh, big deal!"])
          stuff-to-do))
(with-mischief
  (println "Here's how I feel about that thing you did: " message))

(defmacro with-mischief
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
     ~@stuff-to-do))

(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))

(defmacro without-mischief
  [& stuff-to-do]
  `(let [macro-message# "Oh, big deal!"]
     ~@stuff-to-do
     (println "I still need to say: " macro-message#)))

(defmacro gensym-example
  []
  `(let [name# "Larry Potter"] name#))

;; double eval
(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "was not successful:" ~to-try)))

(if (do (Thread/sleep 1000) (+ 1 1))
  (println '(do (Thread/sleep 1000) (+ 1 1))
           "was successful:"
           (do (Thread/sleep 1000) (+ 1 1)))
  
  (println '(do (Thread/sleep 1000) (+ 1 1))
           "was not successful:"
           (do (Thread/sleep 1000) (+ 1 1))))

(report (Thread/sleep 1000))

(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println (quote ~to-try) "was successful:" result#)
       (println (quote ~to-try) "was not successful:" result#))))

(doseq [code ['(= 1 1) '(= 1 2)]]
  (report code))

(defmacro doseq-macro
  [macroname & args]
  `(do
     ~@(map (fn [arg] (list macroname arg)) args)))

;; validation

(def order-details-validations
  {:name
   ["Please enter a name" not-empty]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    #(or (empty? %) (re-seq #"@" %))]})

(def order-details
  {:name "Mitchard Blimmons"
   :email "mitchard.blimmonsgmail.com"})

(defn error-messages-for
  "return a seq of error messages
   validation-check-groups is a seq of alternating messages and
   validation checks"
  [value validation-check-groups]
  (map first (filter #(not ((second %) value))
                     (partition 2 validation-check-groups))))

(defn validate
  "returns a map with a vec of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname message-validator-pairs] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value message-validator-pairs)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(validate order-details order-details-validations)

(let [errors (validate order-details order-details-validations)]
  (if (empty? errors)
    (println :success)
    (println :failure errors)))

(defn if-valid
  [record validation success-code failure-code]
  (let [errors (validate record validation)]
    (if (empty? errors)
      success-code
      failure-code)))

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

(error-messages-for "" ["Please enter a name" not-empty])
;=> ("Please enter a name")

(let [errors (validate order-details order-details-validations)]
  (if (empty? errors)
    (println :success)
    (println :failure errors)))

(let [errors (validate order-details order-details-validations)]
  (if (empty? errors)
    (do (save-order-details order-details)
        (redirect-to (url-for :order-confirmation)))
    (render "order-details" {:errors errors})))

(if-valid order-details order-details-validations errors
 (println :success)
 (println :failure errors))

(macroexpand
 '(if-valid order-details order-details-validations my-error-name
            (println :success)
            (println :failure my-error-name)))

(if-valid order-details order-details-validations errors
 (do (save-order-details order-details)
     (redirect-to (url-for :order-confirmation)))
 (render "order-details" {:errors errors}))


(if-let [errors (valid? order-details order-details-validations)])
