(ns cardtable.games
  (:require [cardtable.cards :as cbase]
            [cardtable.match-cards :as c]
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
  (->> (range (count deck))
    (filter #(not (nil?         (get deck %))))
    (filter #(point-in-rect x y (get deck %)))
    last))

; Finds card that was clicked, if any, and turns it face up.
; Also records and uses the click time for debouncing purposes.
(defn- handle-mouse-click [deck]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) :left))
    (let [card-idx (find-selected-card deck (q/mouse-x) (q/mouse-y))
          card     (get deck card-idx)]
      (cond
        (nil? card)
          deck
        (not (last-click-recent card))
          (assoc deck card-idx (assoc card :is-face-down false
                                           :last-click   (q/millis)))
        :else
          deck))
    deck))

; Remove cards from a deck by indices
; Removal replaces card with nil (doesn't actually remove entry from deck vector)
(defn- remove-cards [deck card-idxs]
  (loop [old-deck deck idxs card-idxs]
    (if (= 0 (count idxs))
      old-deck
      (recur (assoc old-deck (first idxs) nil) (rest idxs)))))

; Hide (turn face-down) cards in a deck by indices
(defn- hide-cards [deck card-idxs]
  (loop [old-deck deck idxs card-idxs]
    (if (= 0 (count idxs))
      old-deck
      (let [flipped-card (assoc (get old-deck (first idxs)) :is-face-down true)]
        (recur
          (assoc old-deck (first idxs) flipped-card)
          (rest idxs))))))

(defn- update-game-impl [game]
  ; The "handle-mouse-click" function may select a new card and thus advance the game
  (let [deck          (if (> 2 (:num-sel-cards game)) ; prevent selecting more than 2 cards
                          (handle-mouse-click (:cards game)) (:cards game))
        cards-eq      #(and (= (:text %1) (:text %2)) (= (:color %1) (:color %2)))
        sel-cards     (filter #(= false (:is-face-down (get deck %))) (range (count deck)))
        num-sel-cards (count sel-cards)]
    (cond
      ; Fewer than two selected cards - nothing to do
      (> 2 num-sel-cards)
        (assoc game :cards deck
                    :num-sel-cards num-sel-cards)
      ; More than two selected cards should never happen!
      (< 2 num-sel-cards)
        (throw (AssertionError.
                 "Internal error: match game allowed more than two selected cards"))
      ; If a second card was just selected, pause game using the timer so that the
      ; player has time to view and remember the second card.
      (= 1 (:num-sel-cards game))
        (assoc game :cards deck
                    :timer 40
                    :num-sel-cards 2)
      ; If the cards are equal, remove them.
      (cards-eq (get deck (first sel-cards)) (get deck (second sel-cards)))
        (assoc game :cards (remove-cards deck sel-cards)
                    :num-sel-cards 0)
      ; Otherwise, hide the cards and increment the score.
      :else
        (let [new-score (+ 1 (:score game))]
          (assoc game :cards (hide-cards deck sel-cards)
                      :score new-score
                      :message (str "Score: " new-score)
                      :num-sel-cards 0)))))

; Places cards on the table in a grid pattern
(defn- deal-cards-for-match-game [deck twidth theight]
  (let [cwidth  (:width  cbase/default-card-settings)
        cheight (:height cbase/default-card-settings)
        hspace (-> cwidth  (* 6) (- twidth)  (Math/abs) (/ 7))
        vspace (-> cheight (* 4) (- theight) (Math/abs) (/ 5))
        new-x  (fn [card-idx] (let [m (mod  card-idx 6)]
                 (+ (* cwidth  m) (* hspace (+ m 1)))))
        new-y  (fn [card-idx] (let [q (quot card-idx 6)]
                 (+ (* cheight q) (* vspace (+ q 1)))))]
    (mapv #(assoc (get deck %) :xpos (new-x %) :ypos (new-y %)) (range (count deck)))))

; Combine two match card decks into one deck, shuffle, and then "deal" the deck
; (set card positions and turn them face down)
(defn start-game [table-width table-height]
  (let [deck (into (c/make-match-card-deck) (c/make-match-card-deck))]
    {:cards (deal-cards-for-match-game (shuffle deck) table-width table-height)
     :score 0
     :message "Score: 0"
     :num-sel-cards 0
     :timer 0}))

; Entry point for updates. Blocks updates as long as timer is > 0
(defn update-game [game]
  (if (< 0 (:timer game))
    (assoc game :timer (- (:timer game) 1))
    (update-game-impl game)))

