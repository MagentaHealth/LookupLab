#!/bin/bash

set -e

clj -T:build ths
clj -T:build syn
cp search_data/dfd.ths "$(pg_config --sharedir)/tsearch_data/"
cp search_data/dfd.syn "$(pg_config --sharedir)/tsearch_data/"
psql -f init-db.sql
