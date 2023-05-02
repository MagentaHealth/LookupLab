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

(ns dfd.story-discovery.web.handler
  (:require
    [dfd.story-discovery.web.middleware.core :as middleware]
    [integrant.core :as ig]
    [reitit.ring :as ring]
    [reitit.swagger-ui :as swagger-ui]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.util.http-response :as http-response]))

(defmethod ig/init-key :handler/ring
  [_ {:keys [router api-path] :as opts}]
  (ring/ring-handler
    router
    (ring/routes
      ;; Handle trailing slash in routes - add it + redirect to it
      ;; https://github.com/metosin/reitit/blob/master/doc/ring/slash_handler.md
      (ring/redirect-trailing-slash-handler)
      (ring/create-resource-handler {:path "/"})
      (when (some? api-path)
        (swagger-ui/create-swagger-ui-handler {:path api-path
                                               :url  (str api-path "/swagger.json")}))
      (ring/create-default-handler
        {:not-found
         (constantly (-> {:status 404, :body "Page not found"}
                         (http-response/content-type "text/html")))
         :method-not-allowed
         (constantly (-> {:status 405, :body "Not allowed"}
                         (http-response/content-type "text/html")))
         :not-acceptable
         (constantly (-> {:status 406, :body "Not acceptable"}
                         (http-response/content-type "text/html")))}))
    {:middleware [(middleware/wrap-base opts)]}))

(defmethod ig/init-key :router/routes
  [_ {:keys [routes]}]
  (apply conj [] routes))


(defmethod ig/init-key :router/core
  [_ {:keys [routes cors-origins cors-methods]
      :or   {cors-origins [#".*"]
             cors-methods [:get :put :post :patch :delete]}}]
  (ring/router
    routes
    {:data {:middleware [[wrap-cors
                          :access-control-allow-origin cors-origins
                          :access-control-allow-methods cors-methods
                          :access-control-allow-credentials ["true"]]]}}))
