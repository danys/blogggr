
CREATE SEQUENCE Blogggr.users_userid_seq;

CREATE TABLE Blogggr.Users (
                userID BIGINT NOT NULL DEFAULT nextval('Blogggr.users_userid_seq'),
                firstName VARCHAR(255) NOT NULL,
                lastName VARCHAR(255) NOT NULL,
                Email VARCHAR(255) NOT NULL,
                PasswordHash CHAR(64) NOT NULL,
                Salt CHAR(12) NOT NULL,
                Challenge CHAR(64) NOT NULL,
                Status INTEGER NOT NULL,
                LastChange TIMESTAMP NOT NULL,
                Version BIGINT DEFAULT 0 NOT NULL,
                CONSTRAINT user_pk PRIMARY KEY (userID)
);


ALTER SEQUENCE Blogggr.users_userid_seq OWNED BY Blogggr.Users.userID;

CREATE UNIQUE INDEX users_idx
 ON Blogggr.Users
 ( Email ASC );

CREATE SEQUENCE Blogggr.sessions_sessionid_seq;

CREATE TABLE Blogggr.Sessions (
                sessionID BIGINT NOT NULL DEFAULT nextval('Blogggr.sessions_sessionid_seq'),
                userID BIGINT NOT NULL,
                sessionHash CHAR(64) NOT NULL,
                lastActionTime TIMESTAMP NOT NULL,
                validTill TIMESTAMP NOT NULL,
                CONSTRAINT session_pk PRIMARY KEY (sessionID)
);


ALTER SEQUENCE Blogggr.sessions_sessionid_seq OWNED BY Blogggr.Sessions.sessionID;

CREATE SEQUENCE Blogggr.posts_postid_seq;

CREATE TABLE Blogggr.Posts (
                postID BIGINT NOT NULL DEFAULT nextval('Blogggr.posts_postid_seq'),
                userID BIGINT NOT NULL,
                title VARCHAR(500) NOT NULL,
                shortTitle VARCHAR(100) NOT NULL,
                textBody TEXT NOT NULL,
                timeStamp TIMESTAMP NOT NULL,
                Version BIGINT DEFAULT 0 NOT NULL,
                CONSTRAINT post_pk PRIMARY KEY (postID)
);


ALTER SEQUENCE Blogggr.posts_postid_seq OWNED BY Blogggr.Posts.postID;

CREATE SEQUENCE Blogggr.comments_commentid_seq;

CREATE TABLE Blogggr.Comments (
                commentID BIGINT NOT NULL DEFAULT nextval('Blogggr.comments_commentid_seq'),
                postID BIGINT NOT NULL,
                text VARCHAR(500) NOT NULL,
                Timestamp TIMESTAMP NOT NULL,
                userID BIGINT NOT NULL,
                Version BIGINT DEFAULT 0 NOT NULL,
                CONSTRAINT comment_pk PRIMARY KEY (commentID)
);


ALTER SEQUENCE Blogggr.comments_commentid_seq OWNED BY Blogggr.Comments.commentID;

CREATE TABLE Blogggr.Friends (
                userOneID BIGINT NOT NULL,
                userTwoID BIGINT NOT NULL,
                Status INTEGER NOT NULL,
                actionUserID BIGINT NOT NULL,
                Version BIGINT DEFAULT 0 NOT NULL,
                CONSTRAINT friend_pk PRIMARY KEY (userOneID, userTwoID)
);


ALTER TABLE Blogggr.Friends ADD CONSTRAINT users_friends_1_fk
FOREIGN KEY (userOneID)
REFERENCES Blogggr.Users (userID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE Blogggr.Friends ADD CONSTRAINT users_friends_2_fk
FOREIGN KEY (userTwoID)
REFERENCES Blogggr.Users (userID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE Blogggr.Friends ADD CONSTRAINT users_friends_3_fk
FOREIGN KEY (actionUserID)
REFERENCES Blogggr.Users (userID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE Blogggr.Posts ADD CONSTRAINT users_posts_fk
FOREIGN KEY (userID)
REFERENCES Blogggr.Users (userID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE Blogggr.Comments ADD CONSTRAINT users_comments_fk
FOREIGN KEY (userID)
REFERENCES Blogggr.Users (userID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE Blogggr.Sessions ADD CONSTRAINT users_sessions_fk
FOREIGN KEY (userID)
REFERENCES Blogggr.Users (userID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE Blogggr.Comments ADD CONSTRAINT posts_comments_fk
FOREIGN KEY (postID)
REFERENCES Blogggr.Posts (postID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
