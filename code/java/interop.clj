(let [stack (java.util.Stack.)]
  (.push stack "Latest episode of Game of Fancy Chairs, ho!")
  stack)

(let [stack (java.util.Stack.)]
  (.push stack "Latest episode of Game of Fancy Chairs, ho!")
  (first stack))

(macroexpand-1
 '(doto (java.util.Stack.)
    (.push "Latest episode of Game of Fancy Chairs, ho!")
    (.push "Whoops, I meant 'Land, ho!'")))
