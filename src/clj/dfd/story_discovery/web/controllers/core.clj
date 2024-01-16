;; Copyright (C) 2022-2023 Magenta Health Inc. 
;; Authored by Carmen La <https://carmen.la/>.

;; This file is part of LookupLab.

;; LookupLab is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as
;; published by the Free Software Foundation, either version 3 of the
;; License, or (at your option) any later version.

;; LookupLab is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with LookupLab.  If not, see <https://www.gnu.org/licenses/>.

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
  [query-fn snip-fn query audiences]
  (try
    (or
      (->> (query-fn :plain-search
                     {:query    query
                      :audiences audiences})
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
                    {:query    (string/join " | " words)
                     :audiences audiences}))))
    (catch Exception e
      (log/error "failed to perform search" e))))

(defn search
  [{{:keys [query audiences]} :body-params :as request}]
  (log/info "searching for" audiences query)
  (let [{:keys [query-fn snip-fn]} (utils/route-data request)
        stripped-query (string/replace query #"want|need|have|require|i'm" "")
        results        (search* query-fn snip-fn stripped-query audiences)]
    (try
      (query-fn :log-search {:query query :audiences audiences :results results})
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