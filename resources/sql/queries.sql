-- :name search :? :*
select trigger.description as trigger,
       trigger.story_id,
       story.audience,
       story.description as story,
       story.destination,
       ts_rank(search_vector, plainto_tsquery('magenta', :query))
from trigger join story on trigger.story_id = story.id
where search_vector @@ plainto_tsquery('magenta', :query);