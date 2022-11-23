#!/bin/bash

set -e

clj -T:build ths
cp search_data/dfd.ths "$(pg_config --sharedir)/tsearch_data/"
psql -f init-db.sql
