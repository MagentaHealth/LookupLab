-- Copyright (C) 2022-2023 Magenta Health Inc. and Carmen La

-- This file is part of the DFD Story Discovery Tool.

-- The DFD Story Discovery Tool is free software: you can redistribute it 
-- and/or modify it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.

-- The DFD Story Discovery Tool is distributed in the hope that it will be 
-- useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.

-- You should have received a copy of the GNU Affero General Public License
-- along with the DFD Story Discovery Tool.
-- If not, see <https://www.gnu.org/licenses/>.

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