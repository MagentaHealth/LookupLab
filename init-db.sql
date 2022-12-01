-- Run this script as a superuser

CREATE ROLE dfd LOGIN PASSWORD 'dfd';

GRANT pg_read_server_files TO dfd;
GRANT pg_write_server_files TO dfd;

CREATE DATABASE dfd
WITH OWNER = dfd
  TEMPLATE template0
  ENCODING = 'UTF8'
  TABLESPACE = pg_default
  LC_COLLATE = 'en_US.UTF-8'
  LC_CTYPE = 'en_US.UTF-8'
  CONNECTION LIMIT = -1;
