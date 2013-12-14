(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :refer [join]])
  (:refer-clojure :exclude [min max]))


(defn comparison-over-maps
  [comparison keys]
  (fn [maps]
    (reduce (fn [result current-map]
              (reduce merge
                      (map (fn [key]
                             {key (comparison (key result) (key current-map))})
                           keys)))
            maps)))

(def min (comparison-over-maps clojure.core/min [:lat :lng]))
(def max (comparison-over-maps clojure.core/max [:lat :lng]))

(defn translate-to-00
  [locations]
  (let [mincoords (min locations)]
    (map #(merge-with - % mincoords) locations)))

(defn scale
  [width height locations]
  (let [maxcoords (max locations)
        ratio {:lat (/ height (:lat maxcoords))
               :lng (/ width (:lng maxcoords))}]
    (map #(merge-with * % ratio) locations)))

(defn latlng->point
  [latlng]
  (str (:lng latlng) "," (:lat latlng)))

(defn points
  [locations]
  (join " " (map latlng->point locations)))

(defn line
  [points]
  (str "<polyline points=\"" points "\" />"))

(defn transform
  [width height locations]
  (->> locations
       translate-to-00
       (scale width height)))

(defn xml
  [width height locations]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       "<g transform=\"translate(0," height ")\">"
       "<g transform=\"scale(1,-1)\">"
       (-> (transform width height locations)
           points
           line)
       "</g></g>"
       "</svg>"))