(ns dfd.story-discovery.web.controllers.story-discovery
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [ring.util.http-response :as http-response]
    [dfd.story-discovery.web.routes.utils :as utils]))

(defn list-all-triggers
  [request]
  (log/info "listing all triggers")
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)]
    (try
      (->> (query-fn :list-all-triggers {:select (snip-fn :select-triggers-snip {})})
           (group-by :audience)
           (http-response/ok)))))

(defn list-default-triggers
  [request]
  (log/info "listing default triggers")
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)]
    (try
      (->> (query-fn :list-default-triggers {:select (snip-fn :select-triggers-snip {})})
           (group-by :audience)
           (http-response/ok)))))

(defn search*
  [query-fn snip-fn query]
  (try
    (or
      (->> (query-fn :plain-search
                     {:select (snip-fn :select-triggers-snip {})
                      :query  query})
           (not-empty))
      (let [words (->> (query-fn :word-search {:words (string/split query #"\s+")})
                       (map :word))]
        (query-fn :tsquery-search
                  {:select (snip-fn :select-triggers-snip {})
                   :query  (string/join " | " words)})))
    (catch Exception e
      (log/error "failed to perform search" e))))

(defn search
  [{{:keys [query]} :params :as request}]
  (log/info "searching for" query)
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)
        query   (string/replace query #"want|need|have|require" "")
        results (search* query-fn snip-fn query)]
    (try
      (query-fn :log-search {:query query :results results})
      (catch Exception e
        (log/error "failed to log search" e)))
    (->> results
         (group-by :audience)
         (http-response/ok))))
;; TODO: typos in synonyms

(defn log-click-through
  [{{:keys [query trigger]} :body-params :as request}]
  (log/info "logging click through")
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (query-fn :log-click-through {:query query :trigger trigger})
      (http-response/ok)
      (catch Exception e
        (log/error "failed to log click through" e)))))