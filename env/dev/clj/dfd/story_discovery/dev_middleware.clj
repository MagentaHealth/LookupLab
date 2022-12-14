(ns dfd.story-discovery.dev-middleware)

(defn log-req [handler]
  (fn [request]
    (clojure.tools.logging/info "in middleware")
    (clojure.tools.logging/info request)
    (handler request)))

(defn wrap-dev [handler _opts]
  (-> handler
      #_log-req))
