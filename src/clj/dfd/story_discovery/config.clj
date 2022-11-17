(ns dfd.story-discovery.config
  (:require
    [aero.core :as aero]
    [kit.config :as config]))

(def ^:const system-filename "system.edn")

(defn system-config
  [options]
  (config/read-config system-filename (merge {:resolver aero/root-resolver} options)))
