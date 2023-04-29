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

create table alias (
id serial primary key,
trigger_id int,
description text,
keywords text,
search_vector tsvector generated always as (setweight(setweight(to_tsvector('dfd_syn',description), 'B'), 'A', tsvector_to_array(to_tsvector('dfd_syn', coalesce(keywords, ''))))) stored,
-- use dfd_syn here so that certain keywords do not get stemmed
CONSTRAINT FK_trigger_id FOREIGN KEY(trigger_id)
   REFERENCES trigger(id)
    on delete set null
);
--;;
-- create index alias_search_index on alias using gin (search_vector);;