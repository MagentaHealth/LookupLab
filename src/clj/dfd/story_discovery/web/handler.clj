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
