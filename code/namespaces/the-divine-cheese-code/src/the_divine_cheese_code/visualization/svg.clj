(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [min max]))

;; ~~~2~~~
(defn comparator-over-maps
  [comparison-fn keys]
  (fn [maps]
    ;; ~~~2.3~~~
    (reduce (fn [result current-map]
              ;; ~~~2.2~~~
              (reduce merge
                      ;; ~~~2.1~~~
                      (map (fn [key]
                             {key (comparison-fn (key result) (key current-map))})
                           keys)))
            maps)))

;; ~~~3~~~
(def min (comparator-over-maps clojure.core/min [:lat :lng]))
(def max (comparator-over-maps clojure.core/max [:lat :lng]))

;; ~~~4~~~
(defn translate-to-00
  [locations]
  (let [mincoords (min locations)]
    (map #(merge-with - % mincoords) locations)))

;; ~~~5~~~
(defn scale
  [width height locations]
  (let [maxcoords (max locations)
        ratio {:lat (/ height (:lat maxcoords))
               :lng (/ width (:lng maxcoords))}]
    (map #(merge-with * % ratio) locations)))

(defn latlng->point
  "Convert lat/lng map to comma-separated string" 
  [latlng]
  (str (:lng latlng) "," (:lat latlng)))

(defn points
  "Given a seq of lat/lng maps, return string of points joined by space"
  [locations]
  (s/join " " (map latlng->point locations)))

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