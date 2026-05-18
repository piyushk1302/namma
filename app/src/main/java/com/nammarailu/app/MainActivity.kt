package com.nammarailu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nammarailu.app.ui.theme.NavyBlue
import com.nammarailu.app.ui.theme.NammaRailuTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permissions handled — geofence will work if granted
        val fine   = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        android.util.Log.d("Permissions", "Fine: $fine, Coarse: $coarse")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init Firebase
        FirebaseApp.initializeApp(this)

        // Request location permissions for geofencing
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            NammaRailuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = NavyBlue
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
