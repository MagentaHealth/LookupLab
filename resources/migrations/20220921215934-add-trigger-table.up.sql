create table trigger (
id serial primary key,
story_id int,
prefix text,
description text, -- unique?
is_default boolean,
search_vector tsvector generated always as (to_tsvector('dfd_syn', description)) stored
-- use dfd_syn here so that certain keywords do not get stemmed
);
--;;
copy trigger(story_id, prefix, description, is_default)
from '${data.triggers}'
delimiter ','
csv header;
--;;
create index search_index on trigger using gin (search_vector);