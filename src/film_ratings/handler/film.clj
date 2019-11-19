(ns film-ratings.handler.film
  (:require [ataraxy.response :as response]
            [film-ratings.boundary.film :as boundary.film]
            [film-ratings.views.film :as views.film]
            [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]))

(s/def ::name string?)
(s/def ::description string?)
(s/def ::rating pos-int?)
(s/def ::film-form (s/keys :req-un [::name ::description ::rating]))

(defmethod ig/init-key :film-ratings.handler.film/show-create [_ _]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (views.film/create-film-view)]))

(defmethod ig/init-key :film-ratings.handler.film/create [_ {:keys [db]}]
  (fn [{[_ film-form] :ataraxy/result :as request}]
    (let [uncoerced (reduce-kv
                      (fn [m k v] (assoc m (keyword k) v))
                      {}
                      (dissoc film-form "__anti-forgery-token"))
          film (st/coerce ::film-form uncoerced st/string-transformer)
          result (boundary.film/create-film db film)
          alerts (if (:id result)
                   {:messages ["Film added"]}
                   result)]
      [::response/ok (views.film/film-view film alerts)])))

(defmethod ig/init-key :film-ratings.handler.film/list [_ {:keys [db]}]
  (fn [_]
    (let [films-list (boundary.film/list-films db)]
      (if (seq films-list)
        [::response/ok (views.film/list-films-view films-list {})]
        [::response/ok (views.film/list-films-view [] {:messages ["No films found"]})]))))