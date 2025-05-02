package hu.koko.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("uid") val uid: String,
    @PropertyName("driver_name") val driverName: String,
    @PropertyName("messenger") val messenger: String
) {
    constructor(documentSnapshot: DocumentSnapshot) : this(
        documentSnapshot.getString("uid")!!,
        documentSnapshot.getString("driver_name")!!,
        documentSnapshot.getString("messenger")!!
    )

    fun toMap(): Map<String, Any> = mapOf(
        "uid" to this.uid,
        "driver_name" to this.driverName,
        "messenger" to this.messenger
    )
}
