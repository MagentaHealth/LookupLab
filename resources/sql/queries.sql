-- :name list-triggers :*
select trigger.prefix,
       trigger.description,
       trigger.story_id,
       story.audience,
       story.description as story,
       story.destination
from trigger join story on trigger.story_id = story.id


-- :name plain-search :? :*
select trigger.prefix,
       trigger.description,
       trigger.story_id,
       story.audience,
       story.description as story,
       story.destination,
       ts_rank(search_vector, plainto_tsquery('magenta', :query))
from trigger join story on trigger.story_id = story.id
where search_vector @@ plainto_tsquery('magenta', :query);


-- :name tsquery-search :? :*
select trigger.prefix,
       trigger.description,
       trigger.story_id,
       story.audience,
       story.description as story,
       story.destination,
       ts_rank(search_vector, to_tsquery('magenta', :query))
from trigger join story on trigger.story_id = story.id
where search_vector @@ to_tsquery('magenta', :query);



-- :name word-search :? :*
/* :require [clojure.string :as string] */
select word
from search_word
where
/*~
(string/join " or "
 (for [word (:words params)]
   (str "similarity(word,'" word "') > 0.3")))
~*/