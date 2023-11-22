(ns hanoi-solver.components.hanoi
  (:require [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn clog [x] (js/console.log x)) ;; for debugging


;; global vars 
(def max-disk 8)

(def width 360)
(def height 250)
(def tower-width 15)
(def tower-height (/ height 2))
(def tower-edge-spacing 40)
(def tower-color "black")
(def disk-height 10)
(def disk-width 100)
(def disk-colors ["red" "blue" "green" "yellow"])

(def canvas-style
  {:width width
   :height height
   :style {:border "1px solid black"}})

(def discs (reagent/atom discs [{:tower 0 :pos 0} {:tower 0 :pos 1} {:tower 0 :pos 2}])) ;; disc 0 is the smallest
(comment 
  "An example of discs:
    1
    0   2
   --- --- ---
    0 is the biggest disc (the first) and 2 is the smallest (the last)
   ")

;; -------------------------
;; main program

;; TOWERS
(defn draw-tower [ctx x y]
  (set! (.-fillStyle ctx) tower-color)
  (.fillRect ctx x y tower-width tower-height))

(defn draw-towers [ctx]
  (draw-tower ctx tower-edge-spacing tower-height)
  (draw-tower ctx (/ width 2) tower-height)
  (draw-tower ctx (- width tower-edge-spacing tower-width) tower-height))

;; DISKS
(defn draw-disk [ctx x y]
  (set! (.-fillStyle ctx) (first disk-colors)) ;; todo: implement colors
  (.fillRect ctx x y tower-width tower-height))

;; todo: implement N later
(defn draw-disks [ctx n]
  (draw-disk ctx tower-edge-spacing (- height disk-height))
  )

(defn draw-canvas-content [canvas]
  (let [ctx (.getContext canvas "2d")]
    (draw-towers ctx)
    (draw-disks ctx 3)))

;; MECHANICS

;; mechanicis helper functions

;; disk is a integer, return a map with the disk position
(defn get-disk-X [disk]
  ;; todo: I know it is ugly, I will fix it later nyaa <3
  (if (= (:tower disk) 1)
    (/ width 2)
    (if (= (:tower disk) 2)
      (- width tower-edge-spacing tower-width)
      tower-edge-spacing)))

;; (defn get-disk-Y [disk]
;;   (+ (- height tower-width) (* (:posY disk) disk-height)))
(defn get-disk-Y [disk] ;; todo: verify for inputs types
  (+ (- height tower-height) (- tower-height (* disk-height (inc (:pos @discs disk))))))

;;** Probably we can use a function to access the disk :posY key.

(defn get-disk-pos [disk]
  {:x (get-disk-X disk)
   :y (get-disk-Y disk)})

;; todo: refactor, theres a way using math to know where disk is placed
(defn is-mouse-inside-disk? [mouseX mouseY diskX diskY]
  (println mouseX mouseY diskX diskY)
  (and (>= mouseX diskX)
       (<= mouseX (+ diskX disk-width)) ;; !! todo: disk-width is dinamic
       (>= mouseY diskY)
       (<= mouseY (+ diskY disk-height))))

;; handle mouse event
(defn handle-mousedown [event]
  (clog event)
  ;; (clog (.-target event))
  (let [mouseX (- (.-clientX event) (.-left (.getBoundingClientRect (.-target event))))
        mouseY (- (.-clientY event) (.-top (.getBoundingClientRect (.-target event))))
        diskX (get-disk-X 0)
        diskY (get-disk-Y 0)]
    (clog (get-disk-Y 0))
    (if (is-mouse-inside-disk? mouseX mouseY diskX diskY)
      (clog "mouse is inside disk")
      (clog "mouse is OUTSIDE disk"))))

(defn tower-of-hanoi []
  (let [hanoi-canvas (reagent/atom nil)]
    (reagent/create-class
     {;;   :component-did-update
    ;;   (fn [this]
    ;;     (clog "component-did-update")
    ;;     (draw-canvas-content (.-firstChild @hanoi-canvas)))

      :component-did-mount
      (fn [this]
        (clog (dom/dom-node this))
        (reset! hanoi-canvas (.-firstChild (dom/dom-node this)))
        (draw-canvas-content @hanoi-canvas)
        (set! (.-onmousedown @hanoi-canvas) handle-mousedown))

      :reagent-render
      (fn []
        [:div.tower-of-hanoi
         [:canvas canvas-style]])})))