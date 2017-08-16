-- DDL
-- object: blogggr | type: SCHEMA --
-- DROP SCHEMA IF EXISTS blogggr CASCADE;
CREATE SCHEMA blogggr;
-- ddl-end --
ALTER SCHEMA blogggr OWNER TO postgres;
-- ddl-end --

SET search_path TO pg_catalog,public,blogggr;
-- ddl-end --

-- object: blogggr.user_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS blogggr.user_id_seq CASCADE;
CREATE SEQUENCE blogggr.user_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 999999999999999999
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE blogggr.user_id_seq OWNER TO postgres;
-- ddl-end --

-- object: blogggr.post_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS blogggr.post_id_seq CASCADE;
CREATE SEQUENCE blogggr.post_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 999999999999999999
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE blogggr.post_id_seq OWNER TO postgres;
-- ddl-end --

-- object: blogggr.comment_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS blogggr.comment_id_seq CASCADE;
CREATE SEQUENCE blogggr.comment_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 999999999999999999
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE blogggr.comment_id_seq OWNER TO postgres;
-- ddl-end --

-- object: blogggr.friends | type: TABLE --
-- DROP TABLE IF EXISTS blogggr.friends CASCADE;
CREATE TABLE blogggr.friends(
	user_one_id bigint,
	user_two_id bigint,
	status integer NOT NULL,
	last_action_user_id bigint,
	last_action_timestamp timestamp NOT NULL,
	version bigint NOT NULL,

);
-- ddl-end --
ALTER TABLE blogggr.friends OWNER TO postgres;
-- ddl-end --

-- object: blogggr.user_image_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS blogggr.user_image_id_seq CASCADE;
CREATE SEQUENCE blogggr.user_image_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 999999999999999999
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE blogggr.user_image_id_seq OWNER TO postgres;
-- ddl-end --

-- object: blogggr.post_image_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS blogggr.post_image_id_seq CASCADE;
CREATE SEQUENCE blogggr.post_image_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 999999999999999999
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE blogggr.post_image_id_seq OWNER TO postgres;
-- ddl-end --

-- object: blogggr.comments | type: TABLE --
-- DROP TABLE IF EXISTS blogggr.comments CASCADE;
CREATE TABLE blogggr.comments(
	comment_id bigint NOT NULL DEFAULT nextval('blogggr.comment_id_seq'::regclass),
	post_id bigint,
	user_id bigint,
	text varchar(500) NOT NULL,
	"timestamp" timestamp NOT NULL,
	version bigint NOT NULL,
	CONSTRAINT comments_commentid_pk PRIMARY KEY (comment_id)

);
-- ddl-end --
ALTER TABLE blogggr.comments OWNER TO postgres;
-- ddl-end --

-- object: blogggr.posts | type: TABLE --
-- DROP TABLE IF EXISTS blogggr.posts CASCADE;
CREATE TABLE blogggr.posts(
	post_id bigint NOT NULL DEFAULT nextval('blogggr.post_id_seq'::regclass),
	user_id bigint,
	title varchar(500) NOT NULL,
	short_title varchar(100) NOT NULL,
	text_body text NOT NULL,
	"timestamp" timestamp NOT NULL,
	is_global boolean NOT NULL,
	version bigint NOT NULL,
	CONSTRAINT postid_pk PRIMARY KEY (post_id),
	CONSTRAINT shorttitle_unique UNIQUE (short_title)

);
-- ddl-end --
ALTER TABLE blogggr.posts OWNER TO postgres;
-- ddl-end --

-- object: blogggr.users | type: TABLE --
-- DROP TABLE IF EXISTS blogggr.users CASCADE;
CREATE TABLE blogggr.users(
	user_id bigint NOT NULL DEFAULT nextval('blogggr.user_id_seq'::regclass),
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	password_hash character(64) NOT NULL,
	salt character(12) NOT NULL,
	challenge character(64) NOT NULL,
	status smallint NOT NULL,
	last_change timestamp NOT NULL,
	sex smallint NOT NULL,
	lang character(2) NOT NULL,
	version bigint NOT NULL,
	CONSTRAINT userid_pk PRIMARY KEY (user_id),
	CONSTRAINT email_unique UNIQUE (email)

);
-- ddl-end --
ALTER TABLE blogggr.users OWNER TO postgres;
-- ddl-end --

-- object: blogggr.post_images | type: TABLE --
-- DROP TABLE IF EXISTS blogggr.post_images CASCADE;
CREATE TABLE blogggr.post_images(
	post_image_id bigint NOT NULL DEFAULT nextval('blogggr.post_image_id_seq'::regclass),
	post_id bigint,
	name varchar(64) NOT NULL,
	width integer NOT NULL,
	height integer NOT NULL,
	version bigint NOT NULL,
	CONSTRAINT post_image_id_pk PRIMARY KEY (post_image_id)

);
-- ddl-end --
ALTER TABLE blogggr.post_images OWNER TO postgres;
-- ddl-end --

-- object: blogggr.user_images | type: TABLE --
-- DROP TABLE IF EXISTS blogggr.user_images CASCADE;
CREATE TABLE blogggr.user_images(
	user_image_id bigint NOT NULL DEFAULT nextval('blogggr.user_image_id_seq'::regclass),
	user_id bigint,
	name varchar(64) NOT NULL,
	is_current boolean NOT NULL,
	width integer NOT NULL,
	height integer NOT NULL,
	version bigint NOT NULL,
	CONSTRAINT user_image_image_id_pk PRIMARY KEY (user_image_id)

);
-- ddl-end --
ALTER TABLE blogggr.user_images OWNER TO postgres;
-- ddl-end --

-- object: friends_useroneid_pk | type: CONSTRAINT --
-- ALTER TABLE blogggr.friends DROP CONSTRAINT IF EXISTS friends_useroneid_pk CASCADE;
ALTER TABLE blogggr.friends ADD CONSTRAINT friends_useroneid_pk FOREIGN KEY (user_one_id)
REFERENCES blogggr.users (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: friends_usertwoid_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.friends DROP CONSTRAINT IF EXISTS friends_usertwoid_fk CASCADE;
ALTER TABLE blogggr.friends ADD CONSTRAINT friends_usertwoid_fk FOREIGN KEY (user_two_id)
REFERENCES blogggr.users (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: last_action_user_id_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.friends DROP CONSTRAINT IF EXISTS last_action_user_id_fk CASCADE;
ALTER TABLE blogggr.friends ADD CONSTRAINT last_action_user_id_fk FOREIGN KEY (last_action_user_id)
REFERENCES blogggr.users (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: user_images_user_id_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.user_images DROP CONSTRAINT IF EXISTS user_images_user_id_fk CASCADE;
ALTER TABLE blogggr.user_images ADD CONSTRAINT user_images_user_id_fk FOREIGN KEY (user_id)
REFERENCES blogggr.users (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: comments_postid_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.comments DROP CONSTRAINT IF EXISTS comments_postid_fk CASCADE;
ALTER TABLE blogggr.comments ADD CONSTRAINT comments_postid_fk FOREIGN KEY (post_id)
REFERENCES blogggr.posts (post_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: comments_userid_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.comments DROP CONSTRAINT IF EXISTS comments_userid_fk CASCADE;
ALTER TABLE blogggr.comments ADD CONSTRAINT comments_userid_fk FOREIGN KEY (user_id)
REFERENCES blogggr.users (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: posts_userid_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.posts DROP CONSTRAINT IF EXISTS posts_userid_fk CASCADE;
ALTER TABLE blogggr.posts ADD CONSTRAINT posts_userid_fk FOREIGN KEY (user_id)
REFERENCES blogggr.users (user_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: post_image_post_id_fk | type: CONSTRAINT --
-- ALTER TABLE blogggr.post_images DROP CONSTRAINT IF EXISTS post_image_post_id_fk CASCADE;
ALTER TABLE blogggr.post_images ADD CONSTRAINT post_image_post_id_fk FOREIGN KEY (post_id)
REFERENCES blogggr.posts (post_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


