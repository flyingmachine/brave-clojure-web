(ns ifacepalm)

(defn lifeline-and-years-lived
  "Given a human subject, return vector of lifeline ratio and years
  person lived"
  [{:keys [palm-stats life-history] :as subject}]
  [(:lifeline-ratio palm-stats) (:years-lived life-history)])

(lifeline-and-years-lived {:palm-stats   {:lifeline-ratio 0.5}
                           :life-history {:years-lived 75}})

(defn reveal-connections
  [subjects]
  (->> subjects
       (map lifeline-and-years-lived)
       (reduce (fn [x palm]
                 (update-in x palm (fnil inc 0)))
               {})))
