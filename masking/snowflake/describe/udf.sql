SELECT SPLIT_PART(f.comment, '\n', 1) AS "Function Name",
       SPLIT_PART(f.comment, '\n', 2) AS "Version",
       SPLIT_PART(f.comment, '\n', 3) AS "Created at",
       SPLIT_PART(f.comment, '\n', 4) AS "Source Code"
FROM ${DB}.information_schema.functions AS f
WHERE f.function_schema = '${SCHEMA}' AND f.function_name = '${FUNC_NAME}';
