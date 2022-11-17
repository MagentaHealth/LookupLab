create extension if not exists pg_trgm;
--;;
create materialized view search_word as
select synonym as word from synonym
union
-- use 'simple' as the language here so that stop words are not ignored, and words are not stemmed
select word from ts_stat('select to_tsvector(''simple'', description) from trigger');
--;;

create index word_index on search_word using gin(word gin_trgm_ops);