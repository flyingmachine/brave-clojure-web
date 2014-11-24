(def addition-list (list + 1 2))
(eval addition-list)
(eval (list 'def 'lucky-number (concat addition-list [10])))

(read-string "(+ 1 2)")
(conj (read-string "(+ 1 2)") :zagglewag)

(eval (read-string "(+ 1 2)"))

(read-string ("+"))
(type (read-string "+"))
(eval (list (read-string "+") 1 2))

(+ 1 (+ 2 3))

(defn my-eval
  [data]
  (if (function-call? data) (apply (head data) (map my-eval (rest data)))))

(defmacro infix
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))
