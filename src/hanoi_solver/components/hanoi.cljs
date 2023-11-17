(ns hanoi-solver.components.hanoi
  (:require [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn clog [x] (js/console.log x)) ;; for debugging

(defn draw-canvas-content [ canvas ] 
  (let [ctx (.getContext canvas "2d")]
    (.beginPath ctx)
    (.moveTo ctx 0 0)
    (.lineTo ctx 200 100)
    (.stroke ctx)))

(def canvas-style
  {:width 360
   :height 250
   :style {:border "1px solid black"}})

(defn tower-of-hanoi []
  (let [hanoi-canvas (reagent/atom nil) discs (reagent/atom 3)]
    (reagent/create-class
     {
    ;;   :component-did-update
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