# chat-app
How to use this application:

```generate private and public keys
To generate a private key execute the following command in bash: 
    openssl genpkey -algorithm RSA -out privateKey.pem

To generate a public key based on the private key execute the following command in bash:
    openssl rsa -pubout -in privateKey.pem -out publicKey.pem

```application.propertis
In the application.properties file change the following:

    quarkus.datasource.db-kind=your-database-kind
    quarkus.datasource.username=your-database-username
    quarkus.datasource.password=your-database-password
    quarkus.datasource.jdbc.url=your-database-url
    quarkus.http.port=to-your-prefered-port (optional) 
    mp.jwt.verify.issuer=your-issuer
    smallrye.jwt.sign.key.location=your-private-key-location
    mp.jwt.verify.publickey.location=your-public-key-location
    
    note: how to generate private and public key is mentioned above

```tables
To create tables in your database execute the following scripts in this exact order:

    CREATE TABLE IF NOT EXISTS public.users
    (
        id character varying(255) NOT NULL,
        password character varying(255) COLLATE pg_catalog."default",
        username character varying(20) COLLATE pg_catalog."default",
        CONSTRAINT users_pkey PRIMARY KEY (id)
    )
    
    CREATE TABLE IF NOT EXISTS public.groups
    (
        id character varying(255) NOT NULL,
        name character varying COLLATE pg_catalog."default",
        CONSTRAINT "Group_pkey" PRIMARY KEY (id),
        CONSTRAINT unique_name UNIQUE (name)
    )

    CREATE TABLE IF NOT EXISTS public.messages
    (
        id character varying(255) NOT NULL,
        message character varying(255) COLLATE pg_catalog."default",
        recipient_id character varying(255),
        sender_id character varying(255),
        group_id character varying(255),
        timestamp without time zone,
        CONSTRAINT messages_pkey PRIMARY KEY (id),
        CONSTRAINT group_fkey FOREIGN KEY (group_id)
            REFERENCES public.groups (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT recepient_fkey FOREIGN KEY (recipient_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT sender_fkey FOREIGN KEY (sender_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )
    
    CREATE TABLE IF NOT EXISTS public.contacts
    (
        id character varying(255),
        user_id character varying(255) NOT NULL,
        contact_id character varying(255) NOT NULL,
        CONSTRAINT contact_fkey FOREIGN KEY (contact_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT user_fkey FOREIGN KEY (user_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )
    
    CREATE TABLE IF NOT EXISTS public.group_users
    (
        id character varying(255),
        group_id character varying(255) NOT NULL,
        user_id character varying(255) NOT NULL,
        is_creator boolean DEFAULT false,
        is_member boolean DEFAULT false,
        CONSTRAINT group_fkey FOREIGN KEY (group_id)
            REFERENCES public.groups (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT user_fkey FOREIGN KEY (user_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )

```endpoints:
    http://localhost:8080/auth/signup
    Method: POST
    Description: Allows new user to create an account
    Payload:
        {
            "username": "your-username",
            "password": "your-password"
        }
    Response:
        String "user successfully registered"
        
    note: username must be unique and password must include
    at least 1 uppercase, 1 lowercase, 1 number, 1 special character

    http://localhost:8080/auth/login
    Method: POST
    Description: Allows user to login
    Payload:
        {
            "username": "your-username",
            "password": "your-password"
        }
    Response:
        String similar to this "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0ODAwMCIsInVwbiI6IkNvYmxlcG90IiwidXNlcklkIjoiMiIsImdyb3VwcyI6WyJ1c2VyIl0sImV4cCI6MTc0MDY1MjU1MCwiaWF0IjoxNzQwNjQ4OTUwLCJqdGkiOiIxYj"
        
    http://localhost:8080/users/{userId}/contact
    Method: POST
    Description: Allows user to add another user to their contacts list
    Payload: String userId: "your-friend-userId"
    Response:
        {
            "id": "6550ce1e-5874-4b26-83c5-86f906beec2e",
            "userId": ""2d8d1c70-d8ae-4c97-917f-a4edf0a479e5"",
            "username": "username"
        }

    http://localhost:8080/users/contacts
    Method: GET
    Description: Allows user to check contacts their contacts
    Response:
        [
            {
                "id": "aaaa58fa-c5d1-42e8-aa48-a964e676a440",
                "userId": "2d8d1c70-d8ae-4c97-917f-a4edf0a479f5",
                "username": "username1"
            },
            {
                "id": "bbba58fa-c5d1-42e8-aa48-a964e676a440",
                "userId": "3d9d1c70-d8ae-4c97-917f-a4edf0a479f5",
                "username": "username2"
            }
        ]
        
    http://localhost:8080/users/{username}
    Method: GET
    Description: Allows user to search for users by username
    Payload: String username: "username"
    Response:
        [            
            {
                "id": "d6df32ab-25dd-4691-934e-f1727b2ebcf6",
                "username": "username1"
            },
            {
                "id": "bdebb3c1-7b88-4dd5-b5db-3258c6a44fc2",
                "username": "username2"
            }
        ]
    
    http://localhost:8080/groups
    Method: POST
    Description: Allows user create a group to chat with multiple other users
    Payload:
        {
            "name": "your-group-name",
            "creators": ["other-users(as admins)"]
        }
    Response:
        {
            "id": "4b61c427-1936-46be-bd2a-a8a9e4ec4e33",
            "name": "groupName"
        }
    
    http://localhost:8080/groups/{groupId}/join
    Method: POST  
    Description: Allows user to join a group as a member
    Payload: String groupId: "your-group-id"
    Response:
        {
            "id": "f013d0c6-edc8-45f7-ad66-ddb6bb75907b",
            "groupId": "3ece348c-e2f1-466d-ae6f-578827e356dd",
            "groupName": "groupName",
            "userId": "bdebb3c1-7b88-4dd5-b5db-3258c6a44fc2",
            "username": "username",
            "isMember": false,
            "isCreator": false
        }
     
    http://localhost:8080/groups/{groupId}/leave
    Method: DELETE
    Description: Allows user to leave a group
    Payload: String groupId: "your-group-id"
    
    http://localhost:8080/groups/{groupId}/waiting/users
    Method: GET
    Payload: String groupId: "your-groupId"
    Description: Allows creators of the group to check pending join requests
    Response:
        [
            {
                "id": "f013d0c6-edc8-45f7-ad66-ddb6bb75907b",
                "groupId": "3ece348c-e2f1-466d-ae6f-578827e356dd",
                "groupName": "groupName",
                "userId": "bdebb3c1-7b88-4dd5-b5db-3258c6a44fc2",
                "username": "username1",
                "isMember": false,
                "isCreator": false
            },
            {
                "id": "fl17d0c6-edc8-45f7-ad66-ddb6bb75907b",
                "groupId": "3ece348c-e2f1-466d-ae6f-578827e356dd",
                "groupName": "groupName",
                "userId": "bdebb3c1-7b88-4dd5-b5db-3258c6a44fc2",
                "username": "username2",
                "isMember": false,
                "isCreator": false
            }
        ]
    
    http://localhost:8080/groups/{groupId}/accept/user/{userId}
    Method: PUT
    Description: Allows creators of the group to accept a join group request
    Payload: String groupId: "groupId", String userId: "userId"
    Response:
        {
            "id": "f013d0c6-edc8-45f7-ad66-ddb6bb75907b",
            "groupId": "3ece348c-e2f1-466d-ae6f-578827e356dd",
            "groupName": "gr1",
            "userId": "bdebb3c1-7b88-4dd5-b5db-3258c6a44fc2",
            "username": "username",
            "isMember": true,
            "isCreator": false
        }
    
    http://localhost:8080/groups/{groupId}/reject/user/{userId}
    Method: DELETE
    Description: Allows creators of the group to reject a join group request
    Payload: String groupId: "groupId", String userId: "userId"
    
    http://localhost:8080/groups/joined
    Method: GET
    Description: Allows user to check joined groups
    Response:
        [
            {
                "id": "3ece348c-e2f1-466d-ae6f-578827e356dd",
                "name": "groupName1"
            },
            {
                "id": "66702469-fa49-4ab1-b103-1d5caab51c7a",
                "name": "groupName2"
            }
        ]

    http://localhost:8080/messages
    Method: POST
    Description: Allows user to send message to another user
    Payload:
        {
            "message": "your-message",
            "receiverId": "receiver-id"
        }
    Response
        {
            "messageId": "3833b98d-1165-4151-8658-c0d5e9ce0dfk",
            "senderId": "4b61c427-1936-46be-bd2a-a8a9e4ec4e33",
            "senderUsername": "senderUsername",
            "recipientId": 2,
            "recipientUsername": "recipientUsername",
            "message": "some message",
            "time": "sent time in dd-MM-yyyy HH:mm:ss fromat"
        }

    http://localhost:8080/messages/{userId}
    Method: GET
    Description: Allows user to check sent and received messages from another user
    Payload: String userId: "userId"
    Response:
        [
            {
                "messageId": "3833b98d-1165-4151-8658-c0d5e9ce0dfk",
                "senderId": "4b61c427-1936-46be-bd2a-a8a9e4ec4e33",
                "senderUsername": "senderUsername",
                "recipientId": "4b61c427-1936-46be-bd2a-a8a9e4ec4e37",
                "recipientUsername": "recipientUsername",
                "message": "some message",
                "time": "sent time in dd-MM-yyyy HH:mm:ss fromat"
            },
            {
                "messageId": "d8b4956c-f37b-449e-9c0c-6152cd6d9685",
                "senderId": "2d8d1c70-d8ae-4c97-917f-a4edf0a479f5",
                "senderUsername": "senderUsername",
                "recipientId": "4b61c427-1936-46be-bd2a-a8a9e4ec4e36",
                "recipientUsername": "recipientUsername",
                "message": "some message",
                "time": "sent time in dd-MM-yyyy HH:mm:ss fromat"
            }
        ]
    
    http://localhost:8080/messages/group
    Method: POST
    Description: Allows a member of the group to send message to other members of the same group
    Payload:
        {
            "message": "message",
            "groupId": "group-id"
        }
    Response: 
        {
                "messageId": "d8b4956c-f37b-449e-9c0c-6152cd6d9685",
                "senderId": "2d8d1c70-d8ae-4c97-917f-a4edf0a479f5",
                "senderUsername": "senderUsername",
                "groupId": "bcbbe704-5316-48ca-b529-db9a2244470b",
                "groupName": "groupName",
                "message": "some message",
                "time": "sent time in dd-MM-yyyy HH:mm:ss fromat"
        }
    
    http://localhost:8080/messages/group/{groupId}
    Method: GET
    Description: Allows user to check group messages
    Payload: String groupId: "group-id"
    Response:
        [
            {
                "messageId": "d8b4956c-f37b-449e-9c0c-6152cd6d9685",
                "senderId": "2d8d1c70-d8ae-4c97-917f-a4edf0a479f5",
                "senderUsername": "senderUsername",
                "groupId": "bcbbe704-5316-48ca-b529-db9a2244470b",
                "groupName": "groupName",
                "message": "some message",
                "time": "sent time in dd-MM-yyyy HH:mm:ss fromat"
            }
            {
                "messageId": "9d6a9bb0-98bb-4d32-ad9e-52959c9457c8",
                "senderId": "4b61c427-1936-46be-bd2a-a8a9e4ec4e36",
                "senderUsername": "senderUsername",
                "groupId": "bcbbe704-5316-48ca-b529-db9a2244470b",
                "groupName": "groupName",
                "message": "some message",
                "time": "sent time in dd-MM-yyyy HH:mm:ss fromat"
            }
        ]
    
    http://localhost:8080/groups/{groupName}
    Description: Allows user to search for groups
    Payload: String groupName: "group-name"
    Response:
        [
            {
                "id": "3ece348c-e2f1-466d-ae6f-578827e356dd",
                "name": "groupName1 matching the inputed pattern"
            },
            {
                "id": "66702469-fa49-4ab1-b103-1d5caab51c7a",
                "name": "groupName2 matching the inputed pattern"
            }
        ]

    
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/chat-app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
