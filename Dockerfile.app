# Copyright (C) 2022-2023 Magenta Health Inc. 
# Authored by Carmen La <https://carmen.la/>.

# This file is part of LookupLab.

# LookupLab is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.

# LookupLab is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.

# You should have received a copy of the GNU Affero General Public License
# along with LookupLab.  If not, see <https://www.gnu.org/licenses/>.

# syntax = docker/dockerfile:1.2
FROM clojure:openjdk-17 AS build
WORKDIR /
COPY . /
RUN clj -Sforce -T:build app


FROM azul/zulu-openjdk-alpine:17
COPY --from=build target/story-discovery-standalone.jar /app/story-discovery-standalone.jar
COPY .db-connection.edn /app/.db-connection.edn
EXPOSE 3000
ENTRYPOINT exec java $JAVA_OPTS -jar /app/story-discovery-standalone.jar
