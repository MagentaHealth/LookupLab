-- :snip select-triggers-snip
select trigger.prefix,
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
where trigger.is_default = true;


-- :name plain-search :? :*
select t.*,
       story.audience,
       story.description as story,
       story.destination
from (select distinct on (trigger.description)
          trigger.prefix,
          trigger.description,
          trigger.message,
          trigger.story_id,
          greatest(ts_rank(trigger.search_vector, plainto_tsquery('dfd', :query)), ts_rank(alias.search_vector, plainto_tsquery('dfd', :query))) as rank
      from trigger
               left join alias on trigger.id = alias.trigger_id
      where trigger.search_vector @@ plainto_tsquery('dfd', :query)
         or alias.search_vector @@ plainto_tsquery('dfd', :query)
      order by trigger.description, rank desc) t
join story on t.story_id = story.id
where (story.audience = :audience or story.audience = 'All')
order by t.rank desc;


-- :name tsquery-search :? :*
select t.*,
       story.audience,
       story.description as story,
       story.destination
from (select distinct on (trigger.description)
          trigger.prefix,
          trigger.description,
          trigger.message,
          trigger.story_id,
          greatest(ts_rank(trigger.search_vector, to_tsquery('dfd', :query)), ts_rank(alias.search_vector, to_tsquery('dfd', :query))) as rank
      from trigger
               left join alias on trigger.id = alias.trigger_id
      where trigger.search_vector @@ to_tsquery('dfd', :query)
         or alias.search_vector @@ to_tsquery('dfd', :query)
      order by trigger.description, rank desc) t
         join story on t.story_id = story.id
where (story.audience = :audience or story.audience = 'All')
order by t.rank desc;


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