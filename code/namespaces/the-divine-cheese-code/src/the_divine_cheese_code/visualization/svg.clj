(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :refer [join]]))

(defn latlng->point
  [latlng]
  (str (* 5 (:lat latlng)) "," (* 5 (:lng latlng))))

(defn points
  [locations]
  (join " " (map latlng->point locations)))

(defn line
  [points]
  (str "<polyline points=\"" points "\" />"))

(defn draw
  [width height line]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       line
       "</svg>"))