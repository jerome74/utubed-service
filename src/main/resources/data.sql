drop table IF EXISTS Location;
drop table IF EXISTS Userprofile;
drop table IF EXISTS Users;

create TABLE Users(id INT AUTO_INCREMENT  PRIMARY KEY
                    , username VARCHAR(250)  NOT NULL
                    , password VARCHAR(250)  NOT NULL
                    , active BOOLEAN NOT NULL);

create TABLE Userprofile(id INT AUTO_INCREMENT  PRIMARY KEY
                    , nickname VARCHAR(250)  NOT NULL
                    , email VARCHAR(250)  NOT NULL
                    , avatarname VARCHAR(250)  NOT NULL
                    , avatarcolor VARCHAR(250));

     insert into Users (username, password, active ) values ('walter.longo74@gmail.com','admin123', TRUE);
     insert into Users (username, password, active ) values ('guest','guest', TRUE);

     insert into Userprofile (nickname, email, avatarname ,avatarcolor) values ('walter','walter.longo74@gmail.com','users','[0.5, 0.5, 0.5, 1]');
     insert into Userprofile (nickname, email, avatarname ,avatarcolor) values ('guest','guest','users','[0.5, 0.5, 0.5, 1]');

commit;