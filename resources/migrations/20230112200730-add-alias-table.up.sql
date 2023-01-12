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