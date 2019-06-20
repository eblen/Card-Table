(ns cardtable.table
  (:require [cardtable.cards :as c]
            [cardtable.games :as g]
            [quil.core :as q]
            [quil.middleware :as m]))

(def table-width    900)
(def table-height   600)
(def display-width  600)
(def display-height  50)
(def display-top-margin    5)
(def display-bot-margin   20)
(def total-width table-width)
(def total-height (+ table-height   display-top-margin
                     display-height display-bot-margin))

(defn setup-table [] {
  :color [62 249 149]
  :game  (g/start-game table-width table-height)})

(defn update-table [table]
  (assoc table :game (g/update-game (:game table))))

(defn draw-table [t]
  ; Green felt table top
  (apply q/background (:color t))

  ; Draw cards
  (doseq [card (:cards (:game t))] (if card (c/draw card)))

  (let [display-center (+ table-height display-top-margin (/ display-height 2))]
    ; Draw display area below table
    (q/fill 0 0 0)
    (q/rect-mode :center)
    (q/rect (/ total-width 2) display-center display-width display-height 10)

    ; Print message from game
    (q/text-font (q/create-font "Times-Bold" 30 true))
    (q/text-align :center :center)
    (q/fill 255 0 0)
    (q/text (:message (:game t)) (/ table-width 2) display-center)))
