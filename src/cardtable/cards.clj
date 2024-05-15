(ns cardtable.cards
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def default-card-settings {
  :width   62
  :height 100
  :xpos     0
  :ypos     0
  :angle    0
  :is-face-down true})

(defprotocol Card
  (draw-face [c])
  (draw-back [c]))

(defn init-card [c] (merge c default-card-settings))
(defn draw [c]
  (q/rect-mode  :center)
  (q/image-mode :center)
  (q/text-align :center :center)
  (let [x (+ (:xpos c) (/ (:width  c) 2))
        y (+ (:ypos c) (/ (:height c) 2))]
    (q/with-translation [x y]
      (q/with-rotation [(q/radians (:angle c))]
        (if (:is-face-down c) (draw-back c) (draw-face c))))))

