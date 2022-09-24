(ns dfd.story-discovery.web.controllers.story-discovery
  (:require
    [clojure.tools.logging :as log]
    [ring.util.http-response :as http-response]
    [dfd.story-discovery.web.routes.utils :as utils]))


(defn basic-search
  [{{:keys [query]} :body-params :as request}]
  (log/debug request)
  (log/info "searching for" query)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (http-response/ok
        (group-by :audience(query-fn :search {:query query}))))))
