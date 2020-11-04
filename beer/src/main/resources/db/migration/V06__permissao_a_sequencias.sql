SELECT 'GRANT USAGE, SELECT ON ' || quote_ident (schemaname) || '.' || quote_ident (relname) || ' to tgfcode1_postgresjava19734650;'
FROM pg_statio_all_sequences WHERE schemaname = 'public';