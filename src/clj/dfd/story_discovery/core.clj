;; Copyright (C) 2022-2023 Magenta Health Inc. and Carmen La

;; This file is part of the DFD Story Discovery Tool.

;; The DFD Story Discovery Tool is free software: you can redistribute it 
;; and/or modify it under the terms of the GNU Affero General Public License as
;; published by the Free Software Foundation, either version 3 of the
;; License, or (at your option) any later version.

;; The DFD Story Discovery Tool is distributed in the hope that it will be 
;; useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with the DFD Story Discovery Tool.
;; If not, see <https://www.gnu.org/licenses/>.

(ns dfd.story-discovery.core
  (:require
    [dfd.story-discovery.db.core]
    [dfd.story-discovery.config :as config]
    [dfd.story-discovery.env :refer [defaults]]

    [integrant.core :as ig]
    [clojure.tools.logging :as log]
    [conman.core :as conman]

    ;; Edges
    [kit.edge.db.sql.conman]
    [kit.edge.db.sql.migratus]
    [kit.edge.utils.nrepl]
    [kit.edge.server.undertow]
    [dfd.story-discovery.web.handler]

    ;; Routes
    [dfd.story-discovery.web.routes.api])
  (:gen-class))


(defmethod ig/init-key :db.sql/snip-fn
  [_ {:keys [conn options filename]
      :or   {options {}}}]
  (let [queries (conman/bind-connection-map conn options filename)]
    (fn [query params]
      (conman/snip queries query params))))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))
  (shutdown-agents))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _]
  (start-app))
