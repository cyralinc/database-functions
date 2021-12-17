SELECT SPLIT_PART(d.description, '\n', 1) as "Function Name",
       SPLIT_PART(d.description, '\n', 2) as "Version",
       SPLIT_PART(d.description, '\n', 3) as "Created at",
       SPLIT_PART(d.description, '\n', 4) as "Source Code"
FROM pg_proc p
    LEFT JOIN pg_description d
    ON d.objoid = p.oid
WHERE p.proname = lower('${FUNC_NAME}');
