package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DB_Connection {

    public MongoDatabase mongoDB;

    // Create a MongoDB connection.
    DB_Connection() {

        try {

            MongoClient mongoClient = new MongoClient("localhost", 27017);
            mongoDB = mongoClient.getDatabase("wds");
            System.out.println("Successfully connected to the database.");
        } catch (Exception e) {

            System.out.println("Connection to database failed!");
            e.printStackTrace();
        }
    }
}