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

(defprotocol CARD
  (set-angle [c a])
  (set-size  [c w h])
  (draw      [c])
  (move      [c x y])
  (place     [c x y])
  (rotate    [c a])
  (fliph     [c])
  (flipv     [c])
  (cards-eq  [c1 c2]))

(defn- draw-simple-card-face [c]
  (q/fill 255 255 255)
  (q/rect-mode :center)
  (q/text-font (q/create-font "Times-Bold" (/ (:width c) 1.3) true))
  (q/text-align :center :center)
  (let [x (+ (:xpos c) (/ (:width  c) 2))
        y (+ (:ypos c) (/ (:height c) 2))]
    (q/with-translation [x y]
      (q/with-rotation [(q/radians (:angle c))]
        (q/rect 0 0 (:width c) (:height c) 10)
        (apply q/fill (:color c))
        (q/text (str (:text c)) 0 0)))))

(defn- draw-simple-card-back [c]
  (q/fill 0 128 255)
  (q/rect-mode :center)
  (q/text-font (q/create-font "Chalkboard-Bold" (/ (:width c) 4) true))
  (q/text-align :center :center)
  (let [x (+ (:xpos c) (/ (:width  c) 2))
        y (+ (:ypos c) (/ (:height c) 2))]
    (q/with-translation [x y]
      (q/with-rotation [(q/radians (:angle c))]
        (q/rect 0 0 (:width c) (:height c) 10)
        (q/fill 255 180 0)
        (q/text "Clojure" 0 0)))))

(defn- draw-simple-card [c]
  (if (:is-face-down c) (draw-simple-card-back c)
                        (draw-simple-card-face c)))

(defrecord simple-card [])
(defn make-simple-card [t c]
  (simple-card. nil (assoc default-card-settings :text t :color c)))
(extend-type simple-card
  CARD
  (set-angle [c a]   (assoc c :angle a))
  (set-size  [c w h] (assoc c :width  w
                              :height h))
  (draw      [c]     (draw-simple-card c))
  (move      [c x y] (assoc c :xpos (+ (:xpos c) x)
                              :ypos (+ (:ypos c) y)))
  (place     [c x y] (assoc c :xpos x
                              :ypos y))
  (rotate    [c a]   (let [angle (+ (:angle c) a)]
                       (if (>= angle 0)
                         (mod angle 360)
                         (- 360 (mod (Math/abs angle) 360)))))
  (fliph     [c]     (assoc c :is-face-down (not (:is-face-down c))))
  (flipv     [c]     (rotate (fliph c) 180))
  (cards-eq  [c1 c2] (and (= (:text c1) (:text c2)) (= (:color c1) (:color c2)))))

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

(defn make-simple-card-deck []
  (mapv #(apply make-simple-card %)
    (for [[_ s] card-symbols [_ c] card-colors] [s c])))

