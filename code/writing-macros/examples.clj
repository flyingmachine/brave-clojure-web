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

(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list
   'do
   (list 'println "Great squid of Madrid, this is bad code:" (list 'quote bad))
   (list 'println "Sweet gorilla of Manila, this is good code:" (list 'quote good))))

(code-critic {:good (+ 1 1) :bad (1 + 1)})

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