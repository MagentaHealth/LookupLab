CREATE TABLE search_log (
timestamp timestamp DEFAULT now(),
query text,
results jsonb
);