-- Copyright (C) 2022-2023 Magenta Health Inc. and Carmen La

-- This file is part of the DFD Story Discovery Tool.

-- The DFD Story Discovery Tool is free software: you can redistribute it 
-- and/or modify it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.

-- The DFD Story Discovery Tool is distributed in the hope that it will be 
-- useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.

-- You should have received a copy of the GNU Affero General Public License
-- along with the DFD Story Discovery Tool.
-- If not, see <https://www.gnu.org/licenses/>.

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
