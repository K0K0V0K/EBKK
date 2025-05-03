package hu.koko.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hu.koko.model.Travel
import hu.koko.model.User
import hu.koko.repository.TravelRepository
import hu.koko.repository.UserRepository
import hu.koko.service.LoggerService
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri
import hu.koko.service.ContextStorage


@Composable
fun ItemListScreen(applicationContext: Context, uid: String) {
    val travelRepository = TravelRepository()
    val userRepository = UserRepository()
    val contextStorage = ContextStorage(applicationContext)

    val travels = remember { mutableStateOf<List<Travel>>(emptyList()) }
    val user = remember { mutableStateOf<User?>(null) }

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userRepository.getByUid(uid) {
            user.value = it
            contextStorage.setUser(it.driverName)
            LoggerService.log("User set $user")
        }
        travelRepository.listenToTravels() {
            travels.value = it
            LoggerService.log("Travels set $travels")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A8D92),
                        Color(0xFF4E6871)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(travels.value) { travel ->
                TravelCard(
                    travel = travel,
                    applicationContext = applicationContext,
                    userRepository = userRepository,
                    travelRepository = travelRepository,
                    currentUser = user
                )
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF89CFF0)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Travel", tint = Color.White)
        }

        if (showDialog && user.value != null) {
            AddTravelDialog(
                onAdd = { newTravel ->
                    travelRepository.add(newTravel)
                    showDialog = false
                },
                onCancel = { showDialog = false },
                user = user.value!!
            )
        }
    }
}
