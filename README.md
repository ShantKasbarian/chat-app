# chat-app

This guide walks you through setting up and running the application on your local machine using Java, Maven and MongoDB.

---

## Prerequisites

Make sure the following tools are installed on your system:

1. **Java 25**
   Download
   from: [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/)

2. **Apache Maven**
   Download from: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

3. **MongoDB**
   Download from: [https://www.mongodb.com/try/download/community](https://www.mongodb.com/try/download/community)

4. **OpenSSL**
   Download from: [https://openssl-library.org/source/](https://openssl-library.org/source/)

You can verify that the required tools are installed by running:

```bash
java -version
```

```bash
mvn -version
```

```bash
mongosh --version
```

```bash
openssl version
```

## Generate Private and Public SSH Keys

The application requires a private and public RSA key pair.

Generate a private key:

```bash
openssl genpkey -algorithm RSA -out privateKey.pem
```

Generate a public key from the private key:

```bash
openssl rsa -pubout -in privateKey.pem -out publicKey.pem
```

Make sure the generated keys are placed in `src/main/resourses`.

Important: Do not commit the private key (privateKey.pem) to the repository.

## Database Setup

Make sure MongoDB is installed and running on your system.

Configure the MongoDB connection in: `src/main/resources/application.properties`

For example:

```properties
quarkus.mongodb.connection-string=mongodb://localhost:27017
quarkus.mongodb.database=<database_name>
```

Replace <database_name> with the name of your MongoDB database.

## Run the Application

Run the application in dev mode:

```bash
mvn quarkus:dev
```

The application will be available at: http://localhost:8080

## Build the Application

To package the application, run:

```bash
mvn clean package
```

The packaged application will be created in the target/quarkus-app/ directory.

You can run the packaged application with:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## API Documentation

The application provides API documentation using OpenAPI.

Once the application is running, you can access:

- **Swagger UI:** http://localhost:8080/q/swagger-ui
- **OpenAPI specification:** http://localhost:8080/q/openapi

## License & Attribution

- This project is provided for learning and personal use.
- Do not claim this project or its code as your own work.
- If you use or modify this project, please provide appropriate credit to the original author.
