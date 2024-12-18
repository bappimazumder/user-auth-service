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


Build the war file using this command

    ./gradlew clean bootWar

The war will found on \build\libs\user-auth-service.war

Finally, For Deployment copy the generated war to /path/to/tomcat10/webapps/     directory as user-auth-service.war


For test please import the postman collection from this location

    resources/postman_collection/User Auth Service.postman_collection.json_

Example API Request and Response Format

1. POST Request Example: Register New User

   HTTP Method: POST

   Endpoint: /api/v1/auth/user/addUser

   Description: Creates a new user with the provided details.

   Request:

   URL: POST /api/v1/auth/user/addUser

   Headers:

        Content-Type: application/json
        Authorization: No Auth

   Body:

        {
           "userName":"bappi",
           "fullName":"Bappi Mazumder",
           "password":"1234567",
           "email":"bappi@gmail.com",
           "roles":"ROLE_PATIENT"
        }


Response:

    HTTP Status Code: 200

        {
        "status": "success",
        "message": "User created successfully",
        "data": null,
        "metadata": null
        }

2. PUT Request Example: Get Token

   HTTP Method: POST

   Endpoint: /api/v1/auth/user/getToken

   Description: Get Token API.

   Request:
   URL: POST /api/v1/auth/user/getToken

   Headers:

        Content-Type: application/json
        Authorization: No Auth
   Body:

        {
           "userName":"bappi",
           "password":"1234567"
        }

   Response:

       {
         "status": "success",
         "message": "Token generated successfully",
         "data": {
            "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYXBwaTIiLCJpYXQiOjE3MzQ0NDczOTcsImV4cCI6MTczNDQ0ODU5N30.n7JwiKe8MaQGA-1bc3CDPKvcg2tkYcQtQK7m37wAgtc"
         },
         "metadata": null
       }

   HTTP Status Code: 200 OK 

   
3. GET Request Example: Validate Token

   HTTP Method: GET

   Endpoint: /api/v1/auth/user/validate?token=eyJhbGciOiJIUzI1NiJ9

   Description: Validate Token.

   Request:
   URL:  GET  /api/v1/auth/user/validate

   Headers:

        Content-Type: none
        Authorization: No Auth
   Request Param:

             "token":"eyJhbGciOiJIUzI1NiJ9",       

   Response:

        {
            "status": "success",
            "message": "Token is valid",
            "data": null,
            "metadata": null
        }
   HTTP Status Code: 200 OK 


## Steps to Dockerized an Application 

Ensure that the database properties in your application.yml file match those in the docker-compose.yml file

Run the Application with Docker Compose

    docker-compose up --build

Stop the Containers

    docker-compose down