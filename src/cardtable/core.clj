(ns cardtable.core
  (:require [cardtable.table :as t]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn setup-table []
  (q/frame-rate 30)
  (t/setup-table))

(defn update-table [table]
  (t/update-table table))

(defn draw-table [table]
  (t/draw-table table))

(q/defsketch cardtable
  :title "Card Table"
  :size [t/table-width t/table-height]
  :setup setup-table
  :update update-table
  :draw draw-table
  :middleware [m/fun-mode])

(defn -main [& args])
