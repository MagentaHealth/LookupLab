create extension if not exists pg_trgm;
--;;
create text search configuration public.magenta (copy = pg_catalog.english);
--;;
create text search dictionary magenta_ths (
    template = thesaurus,
    dictfile = magenta,
    dictionary = english_stem
);
--;;
create text search dictionary magenta_syn (
    template = synonym,
    synonyms = magenta
);
--;;
alter text search configuration magenta
    alter mapping for asciiword, asciihword, hword_asciipart, word, hword, hword_part
        with magenta_ths, english_stem;
--;;
alter text search configuration magenta
    drop mapping for email, url, url_path;
--;;