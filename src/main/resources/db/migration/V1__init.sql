CREATE TABLE IF NOT EXISTS public.Users
(
    login VARCHAR(15) NOT NULL PRIMARY KEY ,
    email VARCHAR(100) NOT NULL UNIQUE ,
    password VARCHAR(255) NOT NULL,
    hightScore INTEGER DEFAULT 0
);