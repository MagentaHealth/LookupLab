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
