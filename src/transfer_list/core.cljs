(ns transfer-list.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [transfer-list.config :as config]
   [transfer-list.items :rename {items initial-data}]))

(defonce items 
  (r/atom (into {} 
                (map (fn [val] 
                       {val {:value   val
                             :checked false
                             :parent  :left}}))
                initial-data)))

(defn uncheck-all []
  (swap! items update-vals #(assoc % :checked false)))

(defn item [{:keys [value checked parent]}]
  [:label
   [:input {:type "checkbox"
            :on-change #(swap! items assoc value {:value   value
                                                  :checked (not checked)
                                                  :parent  parent})
            :checked checked}]
   value])

(defn list-items [items]
  [:div.list.flex
   (for [i items]
     ^{:key (:value i)}
     [item i])])

(defn action-buttons []
  (let [switch-parent #(if (= :left %) :right :left)
        switch-parent (fn [{:keys [value]}]
                        (swap! items update-in [value] update :parent switch-parent))
        transfer #(let [checked-items (filter :checked (vals @items))]
                    (doseq [item checked-items]
                      (switch-parent item))
                    (uncheck-all))]
    [:div.buttons.flex
     [:button {:on-click transfer} 
      ">"]
     [:button {:on-click transfer} 
      "<"]])) 

(defn app []
  (fn []
    (let [items (vals @items)
          left-items  (filter #(= :left (:parent %)) items)
          right-items (filter #(= :right (:parent %)) items)]
      [:div.app.flex
       [list-items left-items]
       [action-buttons]
       [list-items right-items]])))
   
(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [app] root-el)))

(defn init []
  (dev-setup)
  (mount-root))
