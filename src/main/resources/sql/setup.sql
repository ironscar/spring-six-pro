-- ------------------------- CREATE/DELETE USERS -------------------------------

-- create user with proper privileges (for now all)
create user springstudent with password 'springstudent';

-- grant all privileges
grant all on schema public to springstudent;

-- drop user
reassign owned BY springstudent TO postgres;
drop user if exists springstudent;

-- ------------------------- CREATE DATABASES -------------------------------

-- create db
CREATE DATABASE student_tracker;

-- ------------------------- CREATE TABLES -------------------------------

-- create first table: student
create table student (
	id serial primary key,
	first_name varchar(45) default null,
	last_name varchar(45) default null,
	email varchar(45) default null
);

-- create second table: greeting
CREATE TABLE greeting (
	id serial PRIMARY KEY,
    message varchar(45) NOT NULL,
    student_id int NOT NULL,
    FOREIGN KEY(student_id) REFERENCES student(id)
);

-- create users table for spring security
CREATE TABLE users (
	username varchar(50) not null primary key,
    password varchar(100) not null,
    enabled int not null
);

CREATE TABLE authorities (
	username varchar(50) not null,
    authority varchar(50) not null,
    unique(username, authority),
    foreign key (username) references users(username)
);

CREATE TABLE custom_users (
	userid varchar(50) not null primary key,
    pwd varchar(100) not null,
    age int not null,
    enabled char(1) not null
);

CREATE TABLE custom_authorities (
	userid varchar(50) not null references custom_users(userid),
    role varchar(50) not null,
    unique(userid, role)
);

-- ------------------------- INSERT DATA -------------------------------

-- insert into student table
insert into student (first_name, last_name, email) values ('Iron', 'Scar', 'ironscar@gmail.com');

-- insert into greeting table
insert into greeting (message, student_id) values ('Hello World!', 1);

-- insert into users & authorities security table
insert into users (username, password, enabled) values ('john', '{bcrypt}$2a$12$hrEaU.DlOHFFz./tvhSKqutvEYz1E0aJRfQ71DSQMpW2unEDoegZi', 1);
insert into users (username, password, enabled) values ('amy', '{bcrypt}$2a$12$/I3plg0ELFDOAqCoo.NFX.ZTtyGUTQS.tBZk0IywYu6WzBQCcYt6C', 1);
insert into users (username, password, enabled) values ('prince', '{bcrypt}$2a$12$QJszp0OHuMlE2fNjREC6fOCvhtnrd6tuJLPUBLG68qE0oNXwQKT1y', 1);

insert into authorities (username, authority) values ('john', 'ROLE_STUDENT');
insert into authorities (username, authority) values ('amy', 'ROLE_TEACHER');
insert into authorities (username, authority) values ('prince', 'ROLE_TEACHER');
insert into authorities (username, authority) values ('prince', 'ROLE_ADMIN');

-- insert into custom users & authorities security table
insert into custom_users (userid, pwd, age, enabled) values ('john', '{bcrypt}$2a$12$hrEaU.DlOHFFz./tvhSKqutvEYz1E0aJRfQ71DSQMpW2unEDoegZi', 24, 'Y');
insert into custom_users (userid, pwd, age, enabled) values ('amy', '{bcrypt}$2a$12$/I3plg0ELFDOAqCoo.NFX.ZTtyGUTQS.tBZk0IywYu6WzBQCcYt6C', 25, 'Y');
insert into custom_users (userid, pWd, age, enabled) values ('prince', '{bcrypt}$2a$12$QJszp0OHuMlE2fNjREC6fOCvhtnrd6tuJLPUBLG68qE0oNXwQKT1y', 31, 'Y');

insert into custom_authorities (userid, role) values ('john', 'STUDENT');
insert into custom_authorities (userid, role) values ('amy', 'TEACHER');
insert into custom_authorities (userid, role) values ('prince', 'TEACHER');
insert into custom_authorities (userid, role) values ('prince', 'ADMIN');

-- ------------------------ CLEANUP ------------------------

-- drop regular tables
drop table student;
drop table greeting;

-- to delete records if required from security tables
delete from authorities where username in ('john', 'amy', 'prince');
delete from users where username in ('john', 'amy', 'prince');
drop table authorities;
drop table users;
drop table custom_authorities;
drop table custom_users;

-- ------------------------- QUERY DATA -------------------------------

-- select from student table
select* from student;

-- select from security tables
select u.*, a.authority from users u join authorities a on u.username = a.username;

-- select from custom security tables
select u.userid, u.pwd, CASE WHEN u.enabled = 'Y' THEN 1 ELSE 0 END enabled, concat('ROLE_', a.role) role from custom_users u join custom_authorities a on u.userid = a.userid;

-- ------------------------- JOIN QUERY DATA -------------------------------

-- join select
select 
	s.id st_id,
    s.first_name first_name,
    s.last_name last_name,
    s.email email,
    g.id g_id,
    g.message g_msg
from student s
left join greeting g
on s.id = g.student_id;

-- multi-greeting insert
insert into greeting (
	message,
    student_id
) 
select* from (
	select 'Hello Again 1' message, 4 student_id
	union
	select 'Hello Again 2' message, 4 student_id
) new_greets
where student_id in (select id from student where id = 4);
