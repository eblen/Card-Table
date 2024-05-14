(ns cardtable.match-cards
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [cardtable.cards :as c]))

(defrecord match-card []
  c/Card
  (draw-face [c]
    (q/fill 255 255 255)
    (q/text-font (q/create-font "Times-Bold" (/ (:width c) 1.3) true))
    (q/rect 0 0 (:width c) (:height c) 10)
    (apply q/fill (:color c))
    (q/text (str (:text c)) 0 0))

  (draw-back [c]
    (q/fill 0 128 255)
    (q/text-font (q/create-font "Chalkboard-Bold" (/ (:width c) 4) true))
    (q/rect 0 0 (:width c) (:height c) 10)
    (q/fill 255 180 0)
    (q/text "Clojure" 0 0)))

(def card-symbols {
  :star    \u2605
  :moon    \u263E
  :heart   \u2665
})

(def card-colors {
  :red    [255   0   0]
  :green  [  0 255   0]
  :blue   [  0   0 255]
  :yellow [255 230   0]
})

(defn make-match-card-deck []
  (mapv #(c/init-card (match-card. nil {:text (get % 0) :color (get % 1)}))
    (for [[_ s] card-symbols [_ c] card-colors] [s c])))

