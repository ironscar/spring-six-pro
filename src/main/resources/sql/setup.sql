-- ------------------------- CREATE USERS -------------------------------

-- drop user first if they exist
DROP USER if exists 'springstudent'@'localhost' ;

-- create user with proper privileges (for now all)
CREATE USER 'springstudent'@'localhost' IDENTIFIED BY 'springstudent';

-- grant all privileges
GRANT ALL PRIVILEGES ON * . * TO 'springstudent'@'localhost';

-- ------------------------- CREATE DATABASES -------------------------------

-- create db is not exists
CREATE DATABASE  IF NOT EXISTS `student_tracker`;
USE `student_tracker`;

-- ------------------------- CREATE TABLES -------------------------------

-- create first table: student
CREATE TABLE `student` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name`varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- ------------------------- INSERT DATA -------------------------------

-- insert into student table
insert into student (first_name, last_name, email) values ('Iron', 'Scar', 'ironscar@gmail.com');

-- ------------------------- QUERY DATA -------------------------------

-- select from student table
select* from student;




