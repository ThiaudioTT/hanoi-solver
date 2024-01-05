(ns hanoi-solver.core
    (:require
     [reagent.core :as r]
     [reagent.dom :as d]
     [hanoi-solver.components.hanoi :as hanoi]))

;; -------------------------
;; Views

(defn made-with-clojure []
  [:div.logo-container
   [:a.made-with-clojure {:href "https://clojure.org/" :target "_blank"}
    [:span "Made with Clojure!"]
    [:img {:src "https://clojure.org/images/clojure-logo-120b.png"}]]])

(defn header []
  [:header [:h1 "Tower of Hanoi"]]
)

(defn footer []
  [:footer [:a {:href "https://github.com/ThiaudioTT/tower-of-hanoi" :target "_blank" :style {:text-decoration "none" :color "gray"}} "Github"]]
)

(defn home-page []
  [:div [made-with-clojure] [header] [hanoi/tower-of-hanoi] [footer]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
