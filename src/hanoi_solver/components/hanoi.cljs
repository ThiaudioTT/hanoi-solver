(ns hanoi-solver.components.hanoi
  (:require [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn clog [x] (js/console.log x)) ;; for debugging


;; global vars 

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

(defn tower-of-hanoi []
  (let [hanoi-canvas (reagent/atom nil) discs (reagent/atom 3)]
    (reagent/create-class
     {;;   :component-did-update
    ;;   (fn [this]
    ;;     (clog "component-did-update")
    ;;     (draw-canvas-content (.-firstChild @hanoi-canvas)))

      :component-did-mount
      (fn [this]
        (clog (dom/dom-node this))
        (reset! hanoi-canvas (.-firstChild (dom/dom-node this)))
        (draw-canvas-content @hanoi-canvas))

      :reagent-render
      (fn []
        [:div.tower-of-hanoi
         [:canvas canvas-style]])})))