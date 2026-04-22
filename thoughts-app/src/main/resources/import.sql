-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
INSERT INTO Thought (id, content, author, authorBio, displayStatus) VALUES (1, 'Sometimes you climb out of bed in the morning and you think, I’m not going to make it, but you laugh inside—remembering all the times you’ve felt that way.', 'Charles Bukowski', 'poet and novelist', 'ENABLED');
INSERT INTO Thought (id, content, author, authorBio, displayStatus) VALUES (2, 'The free soul is rare, but you know it when you see it - basically because you feel good, very good, when you are near or with them.', 'Charles Bukowski', 'poet and novelist', 'ENABLED');
ALTER SEQUENCE Thought_SEQ RESTART WITH 3;