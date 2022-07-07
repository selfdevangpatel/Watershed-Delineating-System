package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;

public class DB_CRUD {

    static DB_Connection conn = new DB_Connection();
    static MongoCollection<Document> userColl = conn.mongoDB.getCollection("users");

    // Check if username exists.
    public static boolean unameExists(String uname) {

        Document unameExistsDoc = new Document("uname", uname);
        long lUE = userColl.countDocuments(unameExistsDoc);

        return (lUE != 0);
    }

    // Check if email exists.
    public static boolean emailExists(String email) {

        Document emailExistsDoc = new Document("email", email);
        long lEE = userColl.countDocuments(emailExistsDoc);

        return (lEE != 0);
    }

    // User registration.
    public static void registerUser(String uname, String pword, String email) {

        Document addUserDoc = new Document();
        addUserDoc.append("uname", uname);
        addUserDoc.append("pword", pword);
        addUserDoc.append("email", email);

        userColl.insertOne(addUserDoc);
    }

    // Validate login credentials.
    public static boolean validateLogin(String uname, String pword) {

        Document valLogDoc = new Document();
        valLogDoc.append("uname", uname);
        valLogDoc.append("pword", pword);
        long luE = userColl.countDocuments(valLogDoc);

        return (luE != 0);
    }

    // Get email of user.
    public static String getEmail(String recipient) {

        Document getRcptDoc = new Document();
        getRcptDoc.append("uname", recipient);
        FindIterable<Document> iterable = userColl.find(getRcptDoc);
        MongoCursor<Document> cursor = iterable.iterator();

        String email = "";

        while(cursor.hasNext()) email = cursor.next().getString("email");

        return email;
    }

    // Set password.
    public static boolean setPassword(String uname, String newPass) {

        userColl.updateOne(Filters.eq("uname", uname), Updates.set("pword", newPass));

        return true;
    }
}