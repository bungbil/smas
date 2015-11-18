
/****************** GroupRole ********************/
DROP ROLE IF EXISTS smasgroup;
CREATE ROLE smasgroup
  ENCRYPTED PASSWORD 'sumbermas'
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;
  
/****************** User ********************/
DROP USER IF EXISTS smas;
CREATE ROLE smas LOGIN
  ENCRYPTED PASSWORD 'sumbermas'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

GRANT smasgroup TO smas;

/****************** Create Database********************/
CREATE DATABASE smas
  WITH OWNER = smas
       ENCODING = 'UNICODE';

