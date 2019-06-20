(ns cardtable.core
  (:require [cardtable.table :as t]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn setup-table []
  (q/frame-rate 30)
  (t/setup-table))

(defn draw-table [table]
  (t/draw-table table))

(q/defsketch cardtable
  :title "Card Table"
  :size [t/total-width t/total-height]
  :setup setup-table
  :update t/update-table
  :draw t/draw-table
  :middleware [m/fun-mode])

(defn -main [& args])
