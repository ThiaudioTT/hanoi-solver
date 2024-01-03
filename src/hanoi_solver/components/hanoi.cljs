(ns hanoi-solver.components.hanoi
  (:require [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn clog [x] (js/console.log x)) ;; for debugging


;; global vars 
(def max-disk 8)

;; this needs to be correct:
;; x = tower-edge-spacing
;; z = tower-width
;; y = width / 2
;; 2y = 2x + 2z ;; Needs to be true
;; width = 2y

(def width 460)
(def height 250)
(def tower-width 20)
(def tower-height (/ height 2))
(def tower-edge-spacing 100)
(def tower-color "black")
(def disk-height 10)
(def disk-width 100) ;; base disk size
(def disk-colors ["red" "blue" "green" "yellow"])

(def canvas-style
  {:width width
   :height height
   :style {:border "1px solid black"}})

;; todo: make a fucn to populate discs
(def discs (reagent/atom [{:tower 0 :pos 0 :width 100 :color "yellow"} {:tower 0 :pos 1 :width 50 :color "orange"} {:tower 0 :pos 2 :width 25 :color "red"}])) ;; pos 0 is the base of the tower, first disk is the greatest
;; we can also have: is-dragging key
(comment
  "An example of discs:
    1
    0   2
   --- --- ---
    0 is the biggest disc (the first) and 2 is the smallest (the last)
   ")

(def hanoi-canvas (reagent/atom nil))

;; todo: I coded using disk and dics.

;; -------------------------
;; main program


(defn get-tower-X 
  "Tower is a number between 0 and 2" ;; todo: verify for inputs types
  [^number tower]
  (+ (* (inc tower) tower-edge-spacing) (* tower tower-width))
)

(defn get-disk-tower 
  "Returns the number of the tower where the disk is"
  [^int disk]
  (:tower (get @discs disk))
  )

(defn get-disk-X
  "Get the X position of a disk that is in a tower"
  [^int discIndex]
  (- (get-tower-X (get-disk-tower discIndex)) (/ (- (:width (get @discs discIndex)) tower-width) 2)))

(defn get-disk-Y [^int disk] ;; todo: verify for inputs types
  (- height (* disk-height (inc (:pos (get @discs disk))))))

;;** Probably we can use a function to access the disk :posY key.

;; (defn get-disk-pos [disk]
;;   {:x (get-disk-X disk)
;;    :y (get-disk-Y disk)})

;; TOWERS

(defn draw-tower [ctx x y]
  (set! (.-fillStyle ctx) tower-color)
  (.fillRect ctx x y tower-width tower-height))


(defn draw-towers [ctx]
  (doseq [index (range 3)]
    ;; (draw-tower ctx (+ tower-edge-spacing (* index tower-width) (* index tower-edge-spacing)) tower-height)
    (draw-tower ctx (+ tower-edge-spacing (* index (+ tower-width tower-edge-spacing))) tower-height)
))

;; DISKS
(defn draw-disk [ctx width height x y color]
  (set! (.-fillStyle ctx) color)
  (.fillRect ctx x y width height))

(defn draw-disk-in-tower 
  "Draw a especific disk in a especific tower"
  [ctx x y discSize]
  (set! (.-fillStyle ctx) (rand-nth disk-colors))  ;; todo: implement always different colors and colors for each disk
  (.fillRect ctx x y discSize disk-height))


(defn draw-all-disks
  "Draw all disks that are not being dragged in their respective towers"
  [ctx]
  (doseq [index (range (count @discs)) disk @discs]
    ;; (println index)
    (if (or (not (:is-dragging disk)) (not (nil? (:is-dragging disk))))
      (draw-disk-in-tower ctx (get-disk-X index) (get-disk-Y index) (:width (get @discs index))) nil)))


(defn draw-canvas-content
  "Draw towers and disks, basically set the canvas content"
  [canvas]
  (let [ctx (.getContext canvas "2d")]
    (draw-towers ctx)
    (draw-all-disks ctx))) ;;todo: implement N disks later


;; clear previous disk and draw a new one
(defn move-disk-to
  "Move a disk to a new position"
  [ctx discIndex x y]

  (let
   [discWidth (:width (get @discs discIndex))
    discHeight disk-height
    ;; discX (get-disk-X discIndex)
    ;; discY (get-disk-Y discIndex)
    discColor (:color (get @discs discIndex))
    ]

    (.clearRect ctx 0 0 width height)
    (draw-canvas-content @hanoi-canvas)
    (draw-disk ctx discWidth discHeight x y discColor))
  )


;; todo: refactor, theres a way using math to know where disk is placed
(defn is-mouse-inside-disk? [mouseX mouseY diskX diskY]
  ;; (println mouseX mouseY diskX diskY)
  (and (>= mouseX diskX)
       (<= mouseX (+ diskX disk-width)) ;; !! todo: disk-width is dinamic
       (>= mouseY diskY)
       (<= mouseY (+ diskY disk-height))))

;; handle mouse event
;; todo: this function is just a test for one disc
;; (defn handle-mousedown 
;;   "Handle the click event on the canvas"
;;   [event]
;;   (let [mouseX (- (.-clientX event) (.-left (.getBoundingClientRect (.-target event))))
;;         mouseY (- (.-clientY event) (.-top (.getBoundingClientRect (.-target event))))
;;         diskX (get-disk-X 0)
;;         diskY (get-disk-Y 0)]
;;     (if (is-mouse-inside-disk? mouseX mouseY diskX diskY)
;;       ;; todo: fix for other discs
;;       (swap! discs #(assoc-in % [0 :is-dragging] true))
;;       (clog "no"))))

;; (defn is-mouse-inside-any-disc? 
;;   "Return the index of the disc that the mouse is inside, or nil if not inside any disc"
;;   [mouseX mouseY]
;;   (doseq [index (range (count @discs))]
;;     (let [discX (get-disk-X index)
;;           discY (get-disk-Y index)]
;;       (if (is-mouse-inside-disk? mouseX mouseY discX discY) index nil)
      
;;     )
;;   ))

(defn is-mouse-inside-any-disc?
  "Return the index of the disc that the mouse is inside, or nil if not inside any disc"
  [mouseX mouseY]
  (loop [index 0]
    (if (< index (count @discs))
      (let [discX (get-disk-X index)
            discY (get-disk-Y index)]
        (if (is-mouse-inside-disk? mouseX mouseY discX discY)
          index
          (recur (inc index))))
      nil)))

(defn handle-mousedown 
  "Handle the click event on the canvas"
  [event]
  (let [mouseX (- (.-clientX event) (.-left (.getBoundingClientRect (.-target event))))
        mouseY (- (.-clientY event) (.-top (.getBoundingClientRect (.-target event))))
        discIndex (is-mouse-inside-any-disc? mouseX mouseY)]
    (if (not (nil? discIndex))
      (swap! discs #(assoc-in % [discIndex :is-dragging] true))
      ;; (println "ACHOUU" discIndex)
      nil)))


(defn is-dragging-any-disc
  "Return the index of the disc that is being dragged, or nil if not dragging any disc"
  []
  (loop [index 0]
    (if (< index (count @discs))
      (let [disc (get @discs index)]
        (if (:is-dragging disc)
          index
          (recur (inc index))))
      nil)))

;; (defn handle-mousemove [event]
;;   (let [mouseX (- (.-clientX event) (.-left (.getBoundingClientRect (.-target event))))
;;         mouseY (- (.-clientY event) (.-top (.getBoundingClientRect (.-target event))))
;;         ctx (.getContext @hanoi-canvas "2d")]

;;     (if (= (:is-dragging (get @discs 0)) true)
;;       (move-disk-to ctx mouseX mouseY) ;; todo: fix for other discs
;;       nil) ;; todo: fix for other discs
;;     ))

(defn handle-mousemove [event]
  (let [mouseX (- (.-clientX event) (.-left (.getBoundingClientRect (.-target event))))
        mouseY (- (.-clientY event) (.-top (.getBoundingClientRect (.-target event))))
        ctx (.getContext @hanoi-canvas "2d")
        discIndex (is-dragging-any-disc)]

    (if (not (nil? discIndex))
      (move-disk-to ctx discIndex mouseX mouseY)
      nil)
    ))

(defn handle-mouseup [event] ;; todo: fix for other discs
  (swap! discs #(assoc-in % [0 :is-dragging] false)))

(defn tower-of-hanoi []
  (reagent/create-class
   {:component-did-update (fn []
                            (println "component-did-update"))

    :component-did-mount
    (fn [this]
      (clog (dom/dom-node this))
      (reset! hanoi-canvas (.-firstChild (dom/dom-node this)))
      (draw-canvas-content @hanoi-canvas)
      (set! (.-onmousedown @hanoi-canvas) handle-mousedown)
      (set! (.-onmousemove @hanoi-canvas) handle-mousemove)
      (set! (.-onmouseup @hanoi-canvas) handle-mouseup))

    :reagent-render
    (fn []
      [:div.tower-of-hanoi
       [:canvas canvas-style]])}))

