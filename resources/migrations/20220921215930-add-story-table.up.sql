create table story (
id serial primary key,
audience text,
description text,
destination text
);
--;;
copy story(audience, description, destination)
from '${data.stories}'
delimiter ','
csv header;