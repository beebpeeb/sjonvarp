(ns tv.core
  (:require [goog.dom :as dom]
            [clojure.walk :refer [keywordize-keys]]
            [citrus.core :as citrus]
            [rum.core :as rum]
            [tv.components :as components]
            [tv.show :refer [tv-show]]
            [tv.specs :refer [explain-schedule valid-schedule?]]))

(enable-console-print!)

;;; Helpers

(defn response->schedule [{:keys [results]}]
  (let [schedule (map tv-show results)]
    schedule))

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
      (.then #(keywordize-keys %))
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
