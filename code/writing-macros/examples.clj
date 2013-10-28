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