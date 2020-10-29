(ns tv.components
  (:require [citrus.core :as citrus]
            [rum.core :as rum :refer [defc]]
            [tv.moment :as moment]
            [tv.show :as show]))

;;; Status

(defc status-badge < rum/static [status]
  [:div.status-badge
   (case status
     :live [:span.tag.is-danger "Live"]
     :repeat [:span.tag.is-info "Repeat"]
     nil)])

;;; Hero

(defc hero < rum/static [{:keys [error schedule]}]
  [:header.hero.is-primary.is-bold
   [:div.hero-body
    [:div.container
     [:h1.hero-title.has-text-weight-bold.is-size-1
      "Dagskrá RÚV"]
     (when (seq schedule)
       [:h2.subtitle
        (str (count schedule) " shows")])
     (when (some? error)
       [:h2.subtitle
        (str "Something went wrong! " error)])]]])

;;; TV Schedule

(defc tv-show < rum/static [{:tv.show/keys [description start-time status subtitle title]}]
  [:div.columns
   [:div.column.is-2
    [:h3.has-text-grey-light.has-text-weight-bold.is-size-4
     (moment/time-string start-time)]
    (status-badge status)]
   [:div.column
    [:h3.has-text-weight-bold.has-text-primary.is-size-4
     title]
    (when subtitle
      [:h5.has-text-grey-light.is-italic.is-size-7 subtitle])
    (when description
     [:p.has-text-weight-light description])]])

(defc tv-schedule < rum/static [{:keys [schedule]}]
  [:section#schedule.container
   (if (seq schedule)
     (let [render #(rum/with-key (tv-show %) (:tv.show/react-key %))]
       (mapv render schedule))
     [:h3.has-text-grey-light.is-italic.is-size-5
      "Loading..."])])

;;; Container

(defc container < rum/reactive [reconciler]
  (let [subscription (rum/react (citrus/subscription reconciler [:schedule]))]
    (conj [:div#components]
          (hero subscription)
          (tv-schedule subscription))))
