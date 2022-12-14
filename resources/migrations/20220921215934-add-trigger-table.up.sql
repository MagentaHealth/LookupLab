create table trigger (
id serial primary key,
story_id int,
prefix text,
description text, -- unique?
message text,
keywords text,
is_default boolean,
search_vector tsvector generated always as (setweight(setweight(to_tsvector('dfd_syn',description), 'B'), 'A', tsvector_to_array(to_tsvector('dfd_syn', keywords)))) stored
-- use dfd_syn here so that certain keywords do not get stemmed
);
--;;
copy trigger(story_id, prefix, description, is_default)
from '${data.triggers}'
delimiter ','
csv header;
--;;
create index search_index on trigger using gin (search_vector);