(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))

(swap! fred (fn [state] (merge-with + state {:cuddle-hunger-level 1})))

(defn increase-cuddle-hunger-level
  [zombie-state increase-by]
  (merge-with + zombie-state {:cuddle-hunger-level increase-by}))

(swap! fred increase-cuddle-hunger-level 10)

(let [zombie-state @fred]
  (if (>= (:percent-deteriorated zombie-state) 50)
    (future (log (:percent-deteriorated zombie-state)))))
