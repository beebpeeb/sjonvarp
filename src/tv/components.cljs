(ns tv.components
  (:require [citrus.core :as citrus]
            [rum.core :as rum]
            [tv.show :as show]))

;;; Status

(rum/defc status < rum/static [show]
  [:div.status-badge
   (case (show/status show)
     :live [:span.tag.is-danger "Live"]
     :repeat [:span.tag.is-info "Repeat"]
     nil)])

;;; Hero

(rum/defc hero < rum/static [{:keys [schedule]}]
  [:header.hero.is-primary.is-bold
   [:div.hero-body
    [:div.container
     [:h1.hero-title.has-text-weight-bold.is-size-1 "Dagskrá RÚV"]
     [:h2.subtitle
      (if (seq schedule)
        (str (count schedule) " shows")
        "Loading...")]]]])

;;; TV Schedule

(rum/defc tv-show < rum/static [show]
  [:div.columns
   [:div.column.is-2
    [:h3.has-text-grey-light.has-text-weight-bold.is-size-5
     (show/start-time show)]
    (status show)]
   [:div.column
    [:h3.has-text-weight-bold.has-text-primary.is-size-4
     (show/title show)]
    (when-some [subtitle (show/subtitle show)]
      [:h5.has-text-grey-light.is-italic.is-size-7 subtitle])
    (when-some [description (show/description show)]
     [:p.has-text-weight-light description])]])

(rum/defc tv-schedule < rum/static [{:keys [schedule]}]
  (when (seq schedule)
    (let [render #(rum/with-key (tv-show %) (show/react-key %))]
      [:section.container
       (mapv render schedule)])))

;;; Container

(rum/defc container < rum/reactive [reconciler]
  (let [subscription (rum/react (citrus/subscription reconciler [:schedule]))]
    (conj [:div#components]
          (hero subscription)
          (tv-schedule subscription))))
