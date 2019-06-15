(ns cardtable.table
  (:require [cardtable.cards :as c]
            [cardtable.games :as g]
            [quil.core :as q]
            [quil.middleware :as m]))

(def table-width  900)
(def table-height 600)

(defn setup-table [] {
  :color [62 249 149]
  :cards (g/start-game table-width table-height)})

(defn update-table [table]
  (assoc table :cards (g/update-game (:cards table))))

(defn draw-table [t]
  (apply q/background (:color t))
  (doseq [card (:cards t)] (c/draw card)))
