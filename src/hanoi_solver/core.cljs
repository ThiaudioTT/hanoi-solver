(ns hanoi-solver.core
    (:require
     [reagent.core :as r]
     [reagent.dom :as d]
     [hanoi-solver.components.hanoi :as hanoi]))

;; -------------------------
;; Views

(defn header []
  [:header [:h1 "Tower of Hanoi"]]
)

(defn home-page []
  [:div [header] [hanoi/tower-of-hanoi]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
