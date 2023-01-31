(ns dfd.story-discovery.web.controllers.core
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [ring.util.http-response :as http-response]
    [dfd.story-discovery.web.routes.utils :as utils])
  (:import
    [java.util Date]))

(defn healthcheck!
  [req]
  (http-response/ok
    {:time     (str (Date. (System/currentTimeMillis)))
     :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
     :app      {:status  "up"
                :message ""}}))

(defn list-all-triggers
  [request]
  (log/info "listing all triggers")
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)]
    (try
      (->> (query-fn :list-all-triggers {:select (snip-fn :select-triggers-snip {})})
           (group-by :audience)
           (http-response/ok))
      (catch Exception e
        (log/error "failed to list triggers" e)))))

(defn list-default-triggers
  [request]
  (log/info "listing default triggers")
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)]
    (try
      (->> (query-fn :list-default-triggers {:select (snip-fn :select-triggers-snip {})})
           (group-by :audience)
           (http-response/ok))
      (catch Exception e
        (log/error "failed to list default triggers" e)))))

(defn search*
  [query-fn snip-fn query audience]
  (try
    (or
      (->> (query-fn :plain-search
                     {:select   (snip-fn :select-triggers-snip {})
                      :query    query
                      :audience audience})
           (not-empty))
      (do
        (log/info (str "no results found for query " query ", performing trigram search"))
        (let [words (->> (query-fn :remove-stop-words {:query query})
                         :words
                         (map #(query-fn :word-search {:word % :threshold (if (>= (count %) 5) 0.4 0.25)}))
                         (mapcat #(if (= 1.0 (:similarity (first %)))
                                    [(first %)]
                                    %))
                         (map :word)
                         (remove nil?))]
          (log/info (str "\tsearching for " (string/join " | " words)))
          (query-fn :tsquery-search
                    {:select   (snip-fn :select-triggers-snip {})
                     :query    (string/join " | " words)
                     :audience audience}))))
    (catch Exception e
      (log/error "failed to perform search" e))))

(defn search
  [{{:keys [query audience]} :params :as request}]
  (log/info "searching for" query)
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)
        stripped-query (string/replace query #"want|need|have|require" "")
        results        (search* query-fn snip-fn stripped-query audience)]
    (try
      (query-fn :log-search {:query query :audience audience :results results})
      (catch Exception e
        (log/error "failed to log search" e)))
    (->> results
         (group-by :audience)
         (http-response/ok))))

(defn log-click-through
  [{{:keys [query audience trigger]} :body-params :as request}]
  (log/debug "logging click through")
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (query-fn :log-click-through {:query query :audience audience :trigger trigger})
      (http-response/ok)
      (catch Exception e
        (log/error "failed to log click through" e)))))


(comment
  (def query-fn user/query-fn)
  (def snip-fn user/snip-fn)


  (let [query "renew my health card"]
    (query-fn :remove-stop-words {:query query}))

  (let [query "dcotor"]
    (let [words (->> (query-fn :remove-stop-words {:query query})
                     :words
                     (map #(query-fn :word-search {:word % :threshold (if (>= (count %) 5) 0.4 0.25)}))
                     #_(mapcat #(if (= 1.0 (:similarity (first %)))
                                [(first %)]
                                %))
                     #_#_(map :word)
                     (remove nil?))]
      words
      #_#_(log/info (str "searching for " (string/join " | " words)))
      (query-fn :tsquery-search
                {:select (snip-fn :select-triggers-snip {})
                 :query  (string/join " | " words)}))))