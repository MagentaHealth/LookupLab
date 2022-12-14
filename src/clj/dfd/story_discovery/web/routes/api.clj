(ns dfd.story-discovery.web.routes.api
  (:require
    [dfd.story-discovery.web.controllers.core :as controllers]
    [dfd.story-discovery.web.middleware.exception :as exception]
    [dfd.story-discovery.web.middleware.formats :as formats]
    [integrant.core :as ig]
    [reitit.coercion.malli :as malli]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.swagger :as swagger]))

;; Routes
(defn api-routes [_opts]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "dfd.story-discovery API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/health"
    {:get controllers/healthcheck!}]
   ["/search"
    {:get {:handler    controllers/search
           :parameters {:query {:query    string?
                                :audience string?}}}}]
   ["/all-triggers"
    {:get {:handler controllers/list-all-triggers}}]
   ["/default-triggers"
    {:get {:handler controllers/list-default-triggers}}]
   ["/log-click-through"
    {:post {:handler    controllers/log-click-through
            :parameters {:body {:query    string?
                                :audience string?
                                :trigger  map?}}}}]])

(defn route-data
  [opts]
  (merge
    opts
    {:coercion   malli/coercion
     :muuntaja   formats/instance
     :swagger    {:id ::api}
     :middleware [;; query-params & form-params
                  parameters/parameters-middleware
                  ;; content-negotiation
                  muuntaja/format-negotiate-middleware
                  ;; encoding response body
                  muuntaja/format-response-middleware
                  ;; exception handling
                  coercion/coerce-exceptions-middleware
                  ;; decoding request body
                  muuntaja/format-request-middleware
                  ;; coercing response bodys
                  coercion/coerce-response-middleware
                  ;; coercing request parameters
                  coercion/coerce-request-middleware
                  ;; exception handling
                  exception/wrap-exception]}))

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (route-data opts) (api-routes opts)])
