create table trigger (
id serial primary key,
story_id int,
description text, -- unique?
search_vector tsvector generated always as (to_tsvector('english', description)) stored
-- use english bc we don't want words to get replaced when generating vectors
);
--;;
copy trigger(story_id, description)
from '${data.triggers}'
delimiter ','
csv header;
--;;
create index search_index on trigger using gin (search_vector);