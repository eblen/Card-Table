(ns cardtable.table
  (:require [cardtable.cards :as c]
            [quil.core :as q]
            [quil.middleware :as m]))

(def table-width  800)
(def table-height 600)

(defn throw-card [card]
  (assoc card :angle (rand-int 360) :is-face-down (== 0 (rand-int 2))
              :xpos (rand-int table-width) :ypos (rand-int table-height)))

(defn arrange-cards [deck]
  (mapv throw-card deck))

(defn setup-table [] {
  :color [62 249 149]
  ; For now, just toss a deck of cards all over the table
  :cards (arrange-cards (c/make-simple-card-deck))})

(defn update-table [table] table)

(defn draw-table [t]
  (apply q/background (:color t))
  (doseq [card (:cards t)] (c/draw card)))
