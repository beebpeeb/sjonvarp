(ns tv.components
  (:require [citrus.core :as citrus]
            [rum.core :as rum :refer [defc]]
            [tv.moment :as moment]))

(def flavors #{:danger :dark :info :light :link :primary :success :warning})

(defc progress-bar < rum/static [& [flavor]]
  [:progress.progress.is-small
   (when (some? (flavors flavor))
     {:class (str "is-" (name flavor))})])

(defc status-badge < rum/static [status]
  [:div.status-badge
   (case status
     :live [:span.tag.is-danger "Live"]
     :repeat [:span.tag.is-success "Repeat"]
     nil)])

(defc hero < rum/static [{:keys [error schedule]}]
  [:header.hero.is-info.is-bold
   [:div.hero-body
    [:div.container
     [:h1.hero-title.has-text-weight-bold.is-size-1
      "Dagskrá RÚV"]
     (when (some? (seq schedule))
       [:h2.subtitle (str (count schedule) " shows")])
     (when (some? error)
       [:h2.subtitle (str "Something went wrong! " error)])]]])

(defc tv-show < rum/static
  [{:tv.show/keys [description start-time status subtitle title]}]
  [:div.columns
   [:div.column.is-2
    [:h3.has-text-grey-light.has-text-weight-bold.is-size-4
     (moment/time-string start-time)]
    (status-badge status)]
   [:div.column
    [:h3.has-text-weight-bold.has-text-info.is-size-4
     title]
    (when (some? subtitle)
      [:h5.has-text-grey-light.is-italic.is-size-7 subtitle])
    (when (some? description)
      [:p.has-text-weight-light description])]])

(defc tv-schedule < rum/static [{:keys [loading? schedule]}]
  (if (some? (seq schedule))
    [:section#schedule.container
     (let [render #(-> (tv-show %)
                       (rum/with-key (:tv.show/react-key %)))]
       (mapv render schedule))]
    (progress-bar :danger)))

(defc container < rum/reactive [reconciler]
  (let [subscription (rum/react (citrus/subscription reconciler [:schedule]))]
    (conj [:div#components]
          (hero subscription)
          (tv-schedule subscription))))
