import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

fun main() {
    val database = getDatabase()
    val collection = database.getCollection<CommentsInfo>(collectionName = "comments")

    runBlocking {
        //addComment(database)
        //readComment(collection)
        //updateComment(collection)
        delete(collection)
    }

}

fun getDatabase(): MongoDatabase{
    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    return client.getDatabase(databaseName = "sample_mflix")
}

data class CommentsInfo(
    @BsonId
    val id: ObjectId,
    val name: String,
    val email: String,
    val text: String,

    @BsonProperty("movie_id")
    val movieId: ObjectId
)

suspend fun addComment(database: MongoDatabase){
    val info = CommentsInfo(
        id = ObjectId(),
        name = "Mongo",
        email = "mongooo@gmail.com",
        text = "This is the best movie ever!",
        movieId = ObjectId()
    )

    val secondItem = info.copy(movieId = ObjectId(), id = ObjectId())

    val collection = database.getCollection<CommentsInfo>(collectionName = "comments")
    collection.insertMany(listOf(info, secondItem)).also{
        println(it.insertedIds)
    }
}

suspend fun readComment(collection: MongoCollection<CommentsInfo>){

    val query = Filters.or(
        listOf(
            Filters.eq(CommentsInfo::name.name, "Mercedes Tyler"),
            Filters.eq(CommentsInfo::email.name, "roxanne_mckee@gameofthron.es"),
        )
    )

    collection.find<CommentsInfo>(filter = query).limit(2).collect{
        println(it)
    }

}

suspend fun updateComment(collection: MongoCollection<CommentsInfo>){
    val query = Filters.eq(CommentsInfo::name.name, "Mongo")
    val updateSet = Updates.set(CommentsInfo::name.name, "MongoDB")

    collection.updateMany(filter = query, update = updateSet).also{
        println("Matched docs ${it.matchedCount} and update docs ${it.modifiedCount}")
    }
}

suspend fun delete(collection: MongoCollection<CommentsInfo>){
    val query = Filters.eq(CommentsInfo::name.name, "MongoDB")
    collection.deleteMany(query).also{
        println("Deleted ${it.deletedCount} docs")
    }
}