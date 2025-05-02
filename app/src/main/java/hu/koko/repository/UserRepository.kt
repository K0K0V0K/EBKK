package hu.koko.repository

import androidx.core.util.Consumer
import com.google.firebase.firestore.FirebaseFirestore
import hu.koko.model.User

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getByUid(uid: String, consumer: Consumer<User>) = db
        .collection("user")
        .whereEqualTo("uid", uid)
        .get()
        .addOnSuccessListener { doc -> consumer.accept(doc.documents.firstNotNullOf { User(it) })}

    fun getByDriver(driverName: String, consumer: Consumer<User>) = db
        .collection("user")
        .whereEqualTo("driver_name", driverName)
        .get()
        .addOnSuccessListener { doc -> consumer.accept(doc.documents.firstNotNullOf { User(it) })}

}