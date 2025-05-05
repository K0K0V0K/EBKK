package hu.koko.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import hu.koko.R
import hu.koko.model.Travel
import hu.koko.model.User
import hu.koko.repository.TravelRepository
import hu.koko.repository.UserRepository
import hu.koko.service.LoggerService
import java.time.format.DateTimeFormatter

@Composable
fun TravelCard(
    travel: Travel,
    applicationContext: Context,
    userRepository: UserRepository,
    travelRepository: TravelRepository,
    currentUser: MutableState<User?>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { },
        shape = RoundedCornerShape(50), // Match LoginScreen's rounded fields
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsCar,
                contentDescription = "Car Icon",
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFF4E6871).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp),
                tint = Color(0xFF4E6871)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${travel.originCity} → ${travel.destinationCity}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4E6871)
                )

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                Text(
                    text = "Indulás: ${travel.startTime.format(formatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Text(
                    text = "Sofőr: ${travel.driverName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (travel.driverName == currentUser.value?.driverName) {
                IconButton(
                    onClick = { travelRepository.delete(travel.id!!) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFB00020).copy(alpha = 0.1f), RoundedCornerShape(50))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFB00020),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        userRepository.getByDriver(travel.driverName) {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://m.me/${it.messenger}".toUri()
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            try {
                                applicationContext.startActivity(intent)
                            } catch (e: Exception) {
                                LoggerService.error(e)
                                Toast.makeText(applicationContext, "Messenger is not installed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF4E6871).copy(alpha = 0.1f), RoundedCornerShape(50))
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook_messenger_brands_solid),
                        contentDescription = "Messenger Icon",
                        tint = Color(0xFF4E6871),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
