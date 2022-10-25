create table click_through_log (
timestamp timestamp default now(),
query text,
trigger jsonb);