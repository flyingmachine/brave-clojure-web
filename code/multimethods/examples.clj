(ns were-creatures)
(defmulti full-moon-behavior (fn [were-creature] (:were-type were-creature)))
(defmethod full-moon-behavior :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder"))
(defmethod full-moon-behavior :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people and sweat to the oldies"))
(full-moon-behavior {:were-type :wolf
                     :name "Rachel from next door"})
(full-moon-behavior {:were-type :simmons
                     :name "Andy the baker"})


(ns random-namespace)
(defmethod were-creatures/full-moon-behavior :murray
  [were-creature]
  (str (:name were-creature) " will show up and dance at random parties"))

(were-creatures/full-moon-behavior {:name "Laura the intern" :were-type :murray})

(ns user)
(defmulti object-type? identity)
(defmethod java.lang.String
  [])
