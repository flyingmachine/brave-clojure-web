(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))

(swap! fred (fn [state] (merge-with + state {:cuddle-hunger-level 1})))

(defn increase-cuddle-hunger-level
  [current-state increase-by]
  (merge-with + current-state {:cuddle-hunger-level increase-by}))
