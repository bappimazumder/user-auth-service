# User Authentication Management Service

This repo will contain user authentication management APIs.


**Prerequisite: **
*  openjdk 17
*  Postgresql 10 or higher
*  git client


**Prepare Database: **

    psql -U postgres -d postgres

    create user pms_user with encrypted password '123';

    create database user_auth_db owner pms_user;

 
 