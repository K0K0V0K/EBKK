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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import hu.koko.R
import hu.koko.model.Travel
import hu.koko.model.User
import hu.koko.repository.TravelRepository
import hu.koko.repository.UserRepository
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
            .padding(vertical = 12.dp) // Increase vertical padding for better spacing
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(24.dp), // More rounded corners for a modern look
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), // Increase elevation for a better floating effect
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant) // Subtle surface background color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Car icon with larger size and vibrant color
            Icon(
                imageVector = Icons.Filled.DirectionsCar,
                contentDescription = "Car Icon",
                modifier = Modifier.size(48.dp), // Increase icon size
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Display travel details with improved text styling
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${travel.originCity} → ${travel.destinationCity}",
                    style = MaterialTheme.typography.headlineMedium, // Use larger text style for the title
                    color = MaterialTheme.colorScheme.onSurface
                )

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                Text(
                    text = "Indulás: ${travel.startTime.format(formatter)}",
                    style = MaterialTheme.typography.bodyMedium, // Use larger body text style
                    color = MaterialTheme.colorScheme.secondary
                )

                // Driver's name clickable for messenger link
                Text(
                    text = "Sofőr: ${travel.driverName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Delete button - only visible if the driver name matches the user's name
            if (travel.driverName == currentUser.value?.driverName) {
                IconButton(
                    onClick = {
                        travelRepository.delete(travel.id!!)
                    },
                    modifier = Modifier
                        .size(36.dp) // Increase button size
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), // Soft red background
                            shape = RoundedCornerShape(50) // Rounded button
                        )
                        .padding(6.dp) // Add some padding for better click area
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        userRepository.getByDriver(travel.driverName) {
                            val intent = Intent(Intent.ACTION_VIEW, "https://m.me/${it.messenger}".toUri())
                            try {
                                applicationContext.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, "Messenger is not installed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(36.dp) // Increase button size
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // Soft red background
                            shape = RoundedCornerShape(50) // Rounded button
                        )
                        .padding(6.dp) // Add some padding for better click area
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook_messenger_brands_solid), // Replace with your drawable name
                        contentDescription = "Messenger Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}