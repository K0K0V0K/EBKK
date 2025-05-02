package hu.koko.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.PropertyName
import java.lang.reflect.Constructor
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

data class Travel(
    @PropertyName("id") val id: String?,
    @PropertyName("origin_city") val originCity: String,
    @PropertyName("destination_city") val destinationCity: String,
    @PropertyName("driver_name") val driverName: String,
    @PropertyName("start_time") val startTime: LocalDateTime,
    @PropertyName("created") val created: Long
) {
    constructor(documentSnapshot: DocumentSnapshot) : this(
        documentSnapshot.id,
        documentSnapshot.getString("origin_city")!!,
        documentSnapshot.getString("destination_city")!!,
        documentSnapshot.getString("driver_name")!!,
        LocalDateTime.ofInstant(documentSnapshot.getTimestamp("start_time")!!.toDate().toInstant(), ZoneId.systemDefault()),
        documentSnapshot.getLong("created")!!
    )

    fun toMap(): Map<String, Any> = mapOf(
        "origin_city" to this.originCity,
        "destination_city" to this.destinationCity,
        "start_time" to Timestamp(Date.from(this.startTime.atZone(ZoneId.systemDefault()).toInstant())),
        "driver_name" to this.driverName,
        "created" to this.created
    )
}
