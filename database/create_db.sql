create table blogggr.users(
 id bigserial primary key,
 firstname varchar(50),
 lastname varchar(50),
 email varchar(200),
 password varchar(50),
 salt char(12),
 challenge char(64),
 status smallint,
 modifiedtimestamp timestamp with time zone
);
