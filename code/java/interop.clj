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


(let [file (java.io.File. "/")]
  (println (.exists file))
  (println (.canWrite file))
  (println (.getPath file)))


(spit "/tmp/hercules-todo-list"
"- kill dat lion brov
- chop up what nasty snakey heady thing")

(let [s (java.io.StringWriter.)]
  (spit s "- capture cerynian hind like for real")
  (.toString s))

(let [s (java.io.StringReader. "- get erymanthian pig what with the tusks")]
  (slurp s))

(with-open [todo-list-rdr (clojure.java.io/reader "/tmp/hercules-todo-list")]
  (line-seq todo-list-rdr))
