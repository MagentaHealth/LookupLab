#!/bin/bash

echo "Generating DB password"
DB_PASSWORD=$(openssl rand -hex 10)

echo "POSTGRES_DB=dfd" >> db.env
echo "POSTGRES_USER=dfd" >> db.env
echo "POSTGRES_PASSWORD=$DB_PASSWORD" >> db.env

echo "{:jdbc-url \"jdbc:postgresql://db/dfd?user=dfd&password=$DB_PASSWORD\"}" >> .db-connection.edn