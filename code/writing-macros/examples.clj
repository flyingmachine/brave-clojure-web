(doseq [phone-number (db/all :phone-numbers)]
  (when (call-number phone-number)
    (log-successful-call! phone-number)
    (put-call-on-speaker! phone-number)))

(macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))

(if boolean-expression
  (do expression-1
      expression-2
      expression-3))

(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))
(macroexpand '(postfix-notation (1 1 +)))


(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

;; syntax quoted
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))

(defmacro my-when
  [arguments]
  body)

(defmacro mutiple-arity
  ([single-arg]
     "Don't do this")
  ([arg1 arg2]
     "Seriously, don't do it :(")
  ([arg1 arg2 arg 3]
     "Nah, just kidding. Do whatever you want! Self-actualize!"))

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
  [{:keys [good bad]}]
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

(def shipping-details-validation
  {:name
   ["Please enter a name" not-empty]

   :address
   ["Please enter an address" not-empty]

   :city
   ["Please enter a city" not-empty]

   :postal-code
   ["Please enter a postal code" not-empty
    
    "Please enter a postal code that looks like a postal code"
    #(or (empty? %)
         (not (re-seq #"[^0-9-]" %)))]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    (or #(empty? %)
        #(re-seq #"@" %))]})

(def shipping-details
  {:name "Mitchard Blimmons"
   :address "134 Wonderment Ln"
   :city ""
   :state "FL"
   :postal-code "32501"
   :email "mitchard.blimmonsgmail.com"})

(validate shipping-details)
; =>
{:email ["Your email address doesn't look like an email address."]
 :city ["Please enter a city"]}


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
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(validate shipping-details shipping-details-validation)

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

(error-messages-for "SHINE ON"
                    ["Please enter a postal code" not-empty
                     "Please enter a postal code that looks like a US postal code"
                     #(or (empty? %)
                          (not (re-seq #"[^0-9-]" %)))])

(let [errors (validate shipping-details shipping-details-validation)]
  (if (empty? errors)
    (render :success)
    (render :failure errors)))

(let [errors (validate shipping-details shipping-details-validation)]
  (if (empty? errors)
    (do (save-shipping-details shipping-details)
        (redirect-to (url-for :order-confirmation)))
    (render "shipping-details" {:errors errors})))

(if-valid shipping-details shipping-details-validation errors
 (render :success)
 (render :failure errors))

(if-valid shipping-details shipping-details-validation errors
 (do (save-shipping-details shipping-details)
     (redirect-to (url-for :order-confirmation)))
 (render "shipping-details" {:errors errors}))