(ns tv.core
  (:require [goog.dom :as dom]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [citrus.core :as citrus]
            [rum.core :as rum]
            [tv.components :as components]
            [tv.show :as show]
            [tv.specs :as specs]))

;;; Helpers

(defn response->schedule [response]
  (some->> (:results response)
           (map show/map->Show)
           (specs/validate-schedule)))

;;; Controller

(defmulti control-schedule (fn [event] event))

(defmethod control-schedule :init []
  {:effect/http {:on-failed :fetch-failed
                 :on-ok :fetch-ok
                 :url "https://apis.is/tv/ruv"}
   :state {:error nil :schedule nil}})

(defmethod control-schedule :fetch-ok [_ [response] state]
  {:state (assoc state :schedule (response->schedule response))})

(defmethod control-schedule :fetch-failed [_ [error] state]
  {:state (assoc state :error (.-message error))})

;;; Effects

(defn fetch! [reconciler controller {:keys [on-failed on-ok url]}]
  (-> (js/fetch url)
      (.then #(.json %))
      (.then #(js->clj %))
      (.then #(transform-keys ->kebab-case-keyword %))
      (.then #(citrus/dispatch! reconciler controller on-ok %))
      (.catch #(citrus/dispatch! reconciler controller on-failed %))))

;;; Reconciler

(defonce reconciler
  (citrus/reconciler
    {:controllers {:schedule control-schedule}
     :effect-handlers {:effect/http fetch!}
     :state (atom nil)}))

;;; Initialize

(defonce init-ctrl (citrus/broadcast-sync! reconciler :init))

(rum/mount (components/container reconciler) (dom/getElement "main"))
