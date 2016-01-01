(ns reducer-examples
  (:require [clojure.core.reducers :as r]))

(def nums (range 10000000))

(time (r/fold + nums))
(time (r/reduce + nums))
(time (reduce + nums))

(time (r/fold + (r/map inc nums)))
(time (r/reduce + (r/map inc nums)))
(time (reduce + (map inc nums)))

(time (r/fold + (r/map inc (r/filter even? nums))))
(time (r/reduce + (r/map inc (r/filter even? nums))))
(time (reduce + (map inc (filter even? nums))))
