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
(defmacro 
  [])

;; double eval
(defmacro
  [])