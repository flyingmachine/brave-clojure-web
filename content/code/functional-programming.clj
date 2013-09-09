(defn clean-chars
  [text]
  (reduce #(string/replace %1 (first %2) (second %2))
          text
          [[#"[‘`’¿]" "'"]
           [#"[“]", "\""]
           [#"[—]", "&mdash;"]
           [#"•", "&bull;"]]))

(defn clean-description
  [description]
  (if-not (empty? description)
    (-> description
        clean-chars
        tidy
        html->md)))

(defn dirty-html->clean-md
  [dirty-html]
  (html->md (tidy (clean-chars dirty-html))))

(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 1000)
  x)

(def memo-sleep-identity (memoize sleepy-identity))