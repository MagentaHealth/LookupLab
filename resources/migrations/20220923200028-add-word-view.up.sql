create extension if not exists pg_trgm;
--;;
-- use 'simple' as the language here so that stop words are not ignored, and words are not stemmed
create materialized view search_word as
select word from ts_stat('select to_tsvector(''simple'', synonym) from synonym')
union
select word from ts_stat('select to_tsvector(''simple'', description) from trigger');
--;;

create index word_index on search_word using gin(word gin_trgm_ops);