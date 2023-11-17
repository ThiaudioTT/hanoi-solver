(ns hanoi-solver.components.hanoi
  (:require [reagent.core :as reagent]))

(def canvas-style
  {:width 360
   :height 250
   :style {:border "1px solid black"}})

(defn tower-of-hanoi []
  (let [dom-node (reagent/atom nil)
        discs (reagent/atom 3)]
    (reagent/create-class
     {:reagent-render
      (fn []
        [:div.tower-of-hanoi
         [:canvas canvas-style]])})))