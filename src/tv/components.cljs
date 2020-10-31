(ns tv.components
  (:require [citrus.core :as citrus]
            [rum.core :as rum]
            [tv.moment :as moment]))

(rum/defc status-badge < rum/static [status]
  [:div.status-badge
   (case status
     :live [:span.tag.is-danger "Live"]
     :repeat [:span.tag.is-info "Repeat"]
     nil)])

(rum/defc hero < rum/static [{:keys [error schedule]}]
  [:header.hero.is-primary.is-bold
   [:div.hero-body
    [:div.container
     [:h1.hero-title.has-text-weight-bold.is-size-1
      "Dagskrá RÚV"]
     [:h2.subtitle
      (when (some? (seq schedule))
        (str (count schedule) " shows"))
      (when (some? error)
        (str "Something went wrong! " error))]]]])

(rum/defc tv-show < rum/static
  [{:tv.show/keys [description start-time status subtitle title]}]
  [:div.columns
   [:div.column.is-2
    [:h3.has-text-grey-light.has-text-weight-bold.is-size-4
     (moment/time-string start-time)]
    (status-badge status)]
   [:div.column
    [:h3.has-text-weight-bold.has-text-primary.is-size-4
     title]
    (when (some? subtitle)
      [:h5.has-text-grey-light.is-italic.is-size-7 subtitle])
    (when (some? description)
      [:p.has-text-weight-light description])]])

(rum/defc tv-schedule < rum/static [{:keys [schedule]}]
  [:section#schedule.container
   (if (some? (seq schedule))
     (let [render #(rum/with-key (tv-show %) (:tv.show/react-key %))]
       (mapv render schedule))
     [:h3.has-text-grey-light.is-italic.is-size-5
      "Loading..."])])

(rum/defc container < rum/reactive [reconciler]
  (let [subscription (rum/react (citrus/subscription reconciler [:schedule]))]
    (conj [:div#components]
          (hero subscription)
          (tv-schedule subscription))))
