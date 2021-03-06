(ns tv.components
  (:require [citrus.core :as citrus]
            [rum.core :as rum :refer [defc]]
            [tv.moment :as moment]))

(def flavors #{:danger :dark :info :light :link :primary :success :warning})

(defc ProgressBar < rum/static [& [flavor]]
  [:progress.progress.is-small
   (when (some? (flavors flavor))
     {:class (str "is-" (name flavor))})])

(defc StatusBadge < rum/static [status]
  [:div.status-badge
   (case status
     :live [:span.tag.is-danger "Live"]
     :repeat [:span.tag.is-success "Repeat"]
     nil)])

(defc Hero < rum/static [{:keys [error schedule]}]
  [:header.hero.is-info.is-bold
   [:div.hero-body
    [:div.container
     [:h1.hero-title.has-text-weight-bold.is-size-1
      "Dagskrá RÚV"]
     (when (some? (seq schedule))
       [:h2.subtitle (str (count schedule) " shows")])
     (when (some? error)
       [:h2.subtitle (str "Something went wrong! " error)])]]])

(defc Show < rum/static
  [{:tv.show/keys [description start-time status subtitle title]}]
  [:div.columns
   [:div.column.is-2
    [:h3.has-text-grey-light.has-text-weight-bold.is-size-4
     (moment/time-string start-time)]
    (StatusBadge status)]
   [:div.column
    [:h3.has-text-weight-bold.has-text-info.is-size-4
     title]
    (when (some? subtitle)
      [:h5.has-text-grey-light.is-italic.is-size-7 subtitle])
    (when (some? description)
      [:p.has-text-weight-light description])]])

(defc Schedule < rum/static [{:keys [schedule]}]
  (if (some? (seq schedule))
    [:section#schedule.container
     (let [render #(-> (Show %)
                       (rum/with-key (:tv.show/react-key %)))]
       (mapv render schedule))]
    (ProgressBar :danger)))

(defc Container < rum/reactive [reconciler]
  (let [subscription (rum/react (citrus/subscription reconciler [:schedule]))]
    (conj [:div#components]
          (Hero subscription)
          (Schedule subscription))))
