package hu.koko

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import hu.koko.screen.ItemListScreen
import hu.koko.screen.LoginScreen
import hu.koko.ui.theme.EszakBalatoniKozlekedesiKozossegTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
        enableEdgeToEdge()
        setContent {
            EszakBalatoniKozlekedesiKozossegTheme {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    LoginScreen(onLoginSuccess = {
                        recreate()
                    })
                } else {
                    ItemListScreen(applicationContext, user.uid)
                }
            }
        }
    }
}