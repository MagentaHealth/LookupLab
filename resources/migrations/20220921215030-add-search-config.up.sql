create extension if not exists pg_trgm;
--;;


create text search dictionary dfd_ths (
    template = thesaurus,
    dictfile = dfd,
    dictionary = english_stem
);
--;;
create text search dictionary dfd_syn (
    template = synonym,
    synonyms = dfd
    );
--;;
CREATE TEXT SEARCH DICTIONARY public.dfd_simple (
    TEMPLATE = pg_catalog.simple,
    STOPWORDS = english
    );
--;;


create text search configuration public.dfd (copy = pg_catalog.english);
--;;
alter text search configuration dfd
    alter mapping for asciiword, asciihword, hword_asciipart, word, hword, hword_part , uint
        with dfd_syn, dfd_ths, english_stem;
--;;
alter text search configuration dfd
    drop mapping for email, url, url_path, hword_asciipart, hword_part;
--;;


create text search configuration public.dfd_syn (copy = pg_catalog.english);
--;;
alter text search configuration dfd_syn
    alter mapping for asciiword, asciihword, hword_asciipart, word, hword, hword_part , uint
        with dfd_syn, english_stem;
--;;
alter text search configuration dfd_syn
    drop mapping for email, url, url_path;
--;;


create text search configuration public.dfd_simple (copy = pg_catalog.simple);
--;;
alter text search configuration dfd_simple
    alter mapping for asciiword, asciihword, word, hword, uint
        with dfd_simple;
--;;
alter text search configuration dfd_simple
    drop mapping for hword_asciipart, hword_part;