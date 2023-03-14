# Calculator challenge REST API

This project exposes REST endpoints to:
- Allow Login into the application using JWT authentication
- Provide user CRUD capabilities to authenticated users 
- Provide operations CRUD capabilities to authenticated users 
- Provide limited records CRUD capabilities to authenticated users 
- Allow performing a specific operation (arithmetic ones are created by default)

It was built in response to TrueNorth challenge: Arithmetic Calculator REST API

## Technologies involved
- Java 17
- PostgreSQL
- Testcontainers
- Spring boot
- Flyway

## Requirements
- Docker desktop
- Java 17

## Modules

- Calculator-api

## Installation

This module contains a `build.gradle` with listed gradle dependencies, it can be built with `./gradlew build`, which will
also run unit and integration tests using testcontainers.
Running `./gradlew docker` will create a docker image with the prefix `tn`, for example: tn/calculator-api:1.0.0

## Docker Compose

As stated above, by running `/gradlew docker` a docker image will be created, allowing us to run it next to postgresql by using `docker-compose`.
Open file `./calculator-api/docker-compose.yml` and review configurations for the service, including an image for postgresql.

Then you can use command line to go to the folder where `docker-compose.yml` is located and then run `docker-compose up -d`.
It will create docker containers for each one of the services configured in docker compose file.

## Seeded Data
Notice that under the folder `./src/main/resources/db/migration` there are some .sql files which belongs to the initial
database schema and initial data.
When starting the application there will be an admin user with username `admin@tncalculator.com` and password `passwordtn` 
which can be used as initial point to login to the application and start creating another users and opertaions.
There are also initial operations created in these seed files: ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION,
SQUARE_ROOT and RANDOM_STRING.

## Access to exposed REST endpoints
Once all the services are started you can use the following urls:

* *To access swagger documentation:*
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

* *To access OpenAPI docs:*
```
http://localhost:8080/api/v1/v3/api-docs
```

* *Base url for calculator rest APIs:*
```
http://localhost:8080/api/v1
```

## Available REST endpoints (refer to swagger documentation for further details)
They are all handled by `calculator-api` module:

### Authentication
| HTTP Method | Url             | Description                                                                                                                       | User roles authorized       |
|-------------|-----------------|-----------------------------------------------------------------------------------------------------------------------------------|-----------------------------|
| POST        | {baseUrl}/login | Validate user credentials and generate a jwt token returned in the header `Authorization` which can be used for incoming requests | USER_ADMIN, USER_CALCULATOR |

### Users
| HTTP Method | Url                     | Description                                 | User roles authorized       |
|-------------|-------------------------|---------------------------------------------|-----------------------------|
| POST        | {baseUrl}/users         | Create an user                              | USER_ADMIN                  |
| GET         | {baseUrl}/users/{id}    | Get a user given an id                      | USER_ADMIN                  |
| PUT         | {baseUrl}/users/{id}    | Update a user                               | USER_ADMIN                  |
| DELETE      | {baseUrl}/users/{id}    | Delete a user                               | USER_ADMIN                  |
| PATCH       | {baseUrl}/users/{id}    | Patch a user                                | USER_ADMIN                  |
| GET         | {baseUrl}/users         | List users                                  | USER_ADMIN                  |
| POST        | {baseUrl}/users/current | Get logged in user                          | USER_ADMIN, USER_CALCULATOR |

### Operations
| HTTP Method | Url                                 | Description                              | User roles authorized       |
|-------------|-------------------------------------|------------------------------------------|-----------------------------|
| POST        | {baseUrl}/operations                | Create an operation                      | USER_ADMIN, USER_CALCULATOR |
| GET         | {baseUrl}/operations/{id}           | Get an operation given an id             | USER_ADMIN, USER_CALCULATOR |
| PUT         | {baseUrl}/operations/{id}           | Update an operation                      | USER_ADMIN                  |
| DELETE      | {baseUrl}/operations/{id}           | Delete an operation                      | USER_ADMIN                  |
| PATCH       | {baseUrl}/operations/{id}           | Patch an operation                       | USER_ADMIN                  |
| GET         | {baseUrl}/operations                | List operations                          | USER_ADMIN, USER_CALCULATOR |
| POST        | {baseUrl}/operations/{id}/calculate | Perform calculation of a given operation | USER_ADMIN, USER_CALCULATOR |

### Records
| HTTP Method | Url                    | Description              | User roles authorized       |
|-------------|------------------------|--------------------------|-----------------------------|
| GET         | {baseUrl}/records/{id} | Get a record given an id | USER_ADMIN, USER_CALCULATOR |
| DELETE      | {baseUrl}/records/{id} | Delete a record          | USER_ADMIN, USER_CALCULATOR |
| GET         | {baseUrl}/records      | List records             | USER_ADMIN, USER_CALCULATOR |

