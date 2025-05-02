package hu.koko.repository

import androidx.core.util.Consumer
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import hu.koko.model.Travel
import hu.koko.service.LoggerService
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class TravelRepository {
    private val db = FirebaseFirestore.getInstance()

    fun listenToTravels(handler: Consumer<List<Travel>>) = db
        .collection("travel")
        .whereGreaterThanOrEqualTo("start_time", Timestamp(Instant.now()))
        .orderBy("start_time")
        .addSnapshotListener { snapshot, _ ->
            val travel = snapshot!!.documents.mapNotNull { document ->
                try {
                    Travel(document)
                } catch (e: Exception) {
                    LoggerService.error(e)
                    null
                }
            }
            handler.accept(travel)
        }

    fun add(travel: Travel) = db
        .collection("travel")
        .add(travel.toMap())
        .addOnSuccessListener { documentReference ->
            LoggerService.log( "DocumentSnapshot added with ID: ${documentReference.id}")
        }

    fun delete(id: String) = db
        .collection("travel")
        .document(id)
        .delete()
}