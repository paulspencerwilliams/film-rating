(ns film-ratings.main
  (:gen-class)
  (:require [duct.core :as duct]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys (or (duct/parse-keys args) [:duct/migrator :duct/daemon])
        profiles [:duct.profile/prod]]
    (prn keys)
    (-> (duct/resource "film_ratings/config.edn")
        (duct/read-config)
        (duct/exec-config profiles keys))))
