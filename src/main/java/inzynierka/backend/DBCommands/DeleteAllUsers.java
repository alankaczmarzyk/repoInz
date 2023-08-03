package inzynierka.backend.DBCommands;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DeleteAllUsers {
    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";


        try (MongoClient client = MongoClients.create(uri)) {
            MongoDatabase db = client.getDatabase("db_inz");
            MongoCollection<Document> collection = db.getCollection("user");
            db.getCollection("user").deleteMany(new Document());
            System.out.println("Deleted all documents");
        } catch (MongoException e) {
            System.err.println(e);
        }
    }
}
