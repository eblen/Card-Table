(ns cardtable.games
  (:require [cardtable.cards :as c]
            [quil.core :as q]
            [quil.middleware :as m]))

; Debounce mouse clicks on cards.
; Store the last click time for each card and ignore mouse-press events that
; are too close together.
(defn- last-click-recent [card]
  ; Ignore clicks that are less than 1/2 second apart
  (if-let [lctime (:last-click card)]
    (> 500 (- (q/millis) lctime))
    false))
  
(defn- point-in-rect [x y r]
  (and (>= x (:xpos r)) (<= x (+ (:xpos r) (:width  r))) 
       (>= y (:ypos r)) (<= y (+ (:ypos r) (:height r)))))

(defn- find-selected-card [deck x y]
  (last (filter #(point-in-rect x y (get deck %)) (range (count deck)))))

(defn- deal-cards-for-match-game [deck twidth theight]
  (let [cwidth  (:width  c/default-card-settings)
        cheight (:height c/default-card-settings)
        hspace (-> cwidth  (* 6) (- twidth)  (Math/abs) (/ 7))
        vspace (-> cheight (* 4) (- theight) (Math/abs) (/ 5))
        new-x  (fn [card-idx] (let [m (mod  card-idx 6)]
                 (+ (* cwidth  m) (* hspace (+ m 1)))))
        new-y  (fn [card-idx] (let [q (quot card-idx 6)]
                 (+ (* cheight q) (* vspace (+ q 1)))))]
    (mapv #(assoc (get deck %) :xpos (new-x %) :ypos (new-y %)) (range (count deck)))))
    
(defn start-game [table-width table-height]
  (let [deck (into (c/make-simple-card-deck) (c/make-simple-card-deck))]
    (deal-cards-for-match-game deck table-width table-height)))

(defn update-game [deck]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) :left))
    (let [card-idx (find-selected-card deck (q/mouse-x) (q/mouse-y))
          card     (get deck card-idx)]
      (if (and card-idx (not (last-click-recent card)))
        (assoc deck card-idx (assoc card :is-face-down (not (:is-face-down card))
                                         :last-click (q/millis)))
        deck))
    deck))

