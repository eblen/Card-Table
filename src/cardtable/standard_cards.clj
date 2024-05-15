(ns cardtable.standard-cards
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.string  :as str]
            [cardtable.cards :as c]))

(defrecord standard-card []
  c/Card
  (draw-face [c]
    (q/image (:image c) 0 0 (:width c) (:height c)))

  (draw-back [c]
    (q/fill 0 128 255)
    (q/text-font (q/create-font "Chalkboard-Bold" (/ (:width c) 4) true))
    (q/rect 0 0 (:width c) (:height c) 10)
    (q/fill 255 180 0)
    (q/text "Clojure" 0 0)))

(defn make-standard-card-deck []
  (for [r (range 2 15) s "cdhs" path '("data/standard_cards/")]
    (c/init-card (standard-card. nil {:rank r :suit s
                                      :image (q/load-image (str/join [path (str r) s ".png"]))}))))

