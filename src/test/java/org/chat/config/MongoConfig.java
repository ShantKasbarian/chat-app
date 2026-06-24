package org.chat.config;

import org.testcontainers.containers.MongoDBContainer;

public class MongoConfig {
    private static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer("mongo:8.0")
            .withReuse(true);

    public static MongoDBContainer getContainer() {
        return MONGO_CONTAINER;
    }
}
