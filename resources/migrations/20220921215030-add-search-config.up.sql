create extension if not exists pg_trgm;
--;;
create text search configuration public.dfd (copy = pg_catalog.english);
--;;
create text search dictionary dfd_ths (
    template = thesaurus,
    dictfile = dfd,
    dictionary = english_stem
);
--;;
alter text search configuration dfd
    alter mapping for asciiword, asciihword, hword_asciipart, word, hword, hword_part
        with dfd_ths, english_stem;
--;;
alter text search configuration dfd
    drop mapping for email, url, url_path;
--;;