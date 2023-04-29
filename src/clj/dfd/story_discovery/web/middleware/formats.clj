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

(ns dfd.story-discovery.web.middleware.formats
  (:require
    [luminus-transit.time :as time]
    [muuntaja.core :as m]))

(def instance
  (m/create
    (-> m/default-options
        (update-in
          [:formats "application/transit+json" :decoder-opts]
          (partial merge time/time-deserialization-handlers))
        (update-in
          [:formats "application/transit+json" :encoder-opts]
          (partial merge time/time-serialization-handlers)))))
