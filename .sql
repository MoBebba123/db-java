INSERT INTO studentpruefungenanmelden (student_id, pruefung_id, seit)
SELECT student_id, prüfungsid, '2024-04-19 00:00:00'
FROM studentabsolviertpruefungen
WHERE note IS NULL AND status IS NULL;




UPDATE studentabsolviertpruefungen
SET note = NULL, status = NULL
WHERE (student_id = 3 AND prüfungsid = 2)
   OR (student_id = 4 AND prüfungsid = 3)
   OR (student_id = 3 AND prüfungsid = 3);


-- alternative 
AND p.datum < DATEADD(WEEK, -6, GETDATE());
