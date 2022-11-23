-- :snip select-triggers-snip
select trigger.prefix,
       trigger.description,
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
:snip:select,
      ts_rank(search_vector, plainto_tsquery('dfd', :query))
from trigger join story on trigger.story_id = story.id
where search_vector @@ plainto_tsquery('dfd', :query);


-- :name tsquery-search :? :*
:snip:select,
      ts_rank(search_vector, plainto_tsquery('dfd', :query))
from trigger join story on trigger.story_id = story.id
where search_vector @@ to_tsquery('dfd', :query);


-- :name word-search :? :*
/* :require [clojure.string :as string] */
select word
from search_word
where
/*~
(string/join " or "
 (for [word (:words params)]
   (str "similarity(word,'" word "') >= 0.25")))
~*/


-- :name log-search :! :n
insert into search_log (query, results)
values (:query, :results);

-- :name log-click-through :! :n
insert into click_through_log (query, trigger)
values (:query, :trigger)