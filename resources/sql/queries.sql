-- Copyright (C) 2022-2023 Magenta Health Inc. 
-- Authored by Carmen La <https://carmen.la/>.

-- This file is part of LookupLab.

-- LookupLab is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.

-- LookupLab is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.

-- You should have received a copy of the GNU Affero General Public License
-- along with LookupLab.  If not, see <https://www.gnu.org/licenses/>.

-- :snip select-triggers-snip
select trigger.id as trigger_id,
       trigger.prefix,
       trigger.description,
       trigger.message,
       trigger.story_id,
       story.audience,
       story.description as story,
       story.destination


-- :name list-all-triggers :*
:snip:select
from trigger join story on trigger.story_id = story.id;


-- :name list-default-triggers :*
:snip:select
from trigger join story on trigger.story_id = story.id
where trigger.is_default = true
order by trigger_id;


-- :name plain-search :? :*
select *
from (select distinct on (story.audience, trigger.description)
          story.audience,
          story.description as story,
          story.destination,
          trigger.prefix,
          trigger.description,
          trigger.message,
          trigger.story_id,
          trigger.priority,
          greatest(ts_rank(trigger.search_vector, plainto_tsquery('dfd', :query)), ts_rank(alias.search_vector, plainto_tsquery('dfd', :query))) as rank
      from trigger
               left join alias on trigger.id = alias.trigger_id
               join story on trigger.story_id = story.id
      where trigger.search_vector @@ plainto_tsquery('dfd', :query)
         or alias.search_vector @@ plainto_tsquery('dfd', :query)) t
where audience in (:v*:audiences)
order by (101 - coalesce(priority, 100)) * rank desc;


-- :name tsquery-search :? :*
select *
from (select distinct on (story.audience, trigger.description)
          story.audience,
          story.description as story,
          story.destination,
          trigger.prefix,
          trigger.description,
          trigger.message,
          trigger.story_id,
          trigger.priority,
          greatest(ts_rank(trigger.search_vector, to_tsquery('dfd', :query)), ts_rank(alias.search_vector, to_tsquery('dfd', :query))) as rank
      from trigger
               left join alias on trigger.id = alias.trigger_id
               join story on trigger.story_id = story.id
      where trigger.search_vector @@ to_tsquery('dfd', :query)
         or alias.search_vector @@ to_tsquery('dfd', :query)) t
where audience in (:v*:audiences)
order by (101 - coalesce(priority, 100)) * rank desc;


-- :name remove-stop-words :? :1
SELECT tsvector_to_array(to_tsvector('dfd_simple', :query)) as words;


-- :name word-search :? :*
select word, similarity(word, :word)
from search_word
where similarity(word, :word) >= :threshold
order by similarity(word, :word) desc;


-- :name log-search :! :n
insert into search_log (query, results)
values (:query, :results);

-- :name log-click-through :! :n
insert into click_through_log (query, trigger)
values (:query, :trigger)