(ns dfd.story-discovery.web.controllers.story-discovery
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [ring.util.http-response :as http-response]
    [dfd.story-discovery.web.routes.utils :as utils]))

(defn list-triggers
  [request]
  (log/info "listing triggers")
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (http-response/ok
        (group-by :audience (query-fn :list-triggers {}))))))

(defn search
  [{{:keys [query]} :params :as request}]
  (log/info "searching for" query)
  (let [{:keys [query-fn]} (utils/route-data request)
        query (string/replace query #"want|need|have|require" "")]
    (try
      (if-let [results (not-empty (query-fn :plain-search {:query query}))]
        (http-response/ok
          (-> (group-by :audience results)))
        (let [words (->> (query-fn :word-search {:words (string/split query #"\s+")})
                         (map :word))]
          (->> (query-fn :tsquery-search {:query (string/join " | " words)})
               (group-by :audience)
               (http-response/ok)))))))
