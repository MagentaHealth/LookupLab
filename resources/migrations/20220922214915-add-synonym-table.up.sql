create table synonym(
keyword text,
synonym text,
PRIMARY KEY (keyword, synonym)
);
--;;
copy synonym(keyword, synonym)
from '${data.synonyms}'
delimiter ','
csv header;