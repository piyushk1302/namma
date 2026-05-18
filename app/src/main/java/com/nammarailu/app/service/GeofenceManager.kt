package com.nammarailu.app.service

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.nammarailu.app.data.Station

object GeofenceManager {

    private const val GEOFENCE_RADIUS_METERS = 5000f   // 5 km
    private const val TAG = "GeofenceManager"

    fun addGeofence(context: Context, station: Station) {
        val hasLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocation) {
            Log.w(TAG, "Location permission not granted — geofence skipped")
            return
        }

        val client = LocationServices.getGeofencingClient(context)

        val geofence = Geofence.Builder()
            .setRequestId(station.id)
            .setCircularRegion(station.latitude, station.longitude, GEOFENCE_RADIUS_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        client.addGeofences(request, getPendingIntent(context))
            .addOnSuccessListener { Log.d(TAG, "Geofence added for ${station.name}") }
            .addOnFailureListener { e -> Log.e(TAG, "Geofence add failed: ${e.message}") }
    }

    fun removeGeofence(context: Context) {
        LocationServices.getGeofencingClient(context)
            .removeGeofences(getPendingIntent(context))
            .addOnSuccessListener { Log.d(TAG, "Geofence removed") }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}

// ── Broadcast Receiver ────────────────────────────────────────────────────────

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            val msg = GeofenceStatusCodes.getStatusCodeString(event.errorCode)
            Log.e("GeofenceReceiver", "Error: $msg")
            return
        }
        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val ids = event.triggeringGeofences?.map { it.requestId } ?: emptyList()
            Log.d("GeofenceReceiver", "Entered geofence(s): $ids")
            AlarmService.triggerAlarm(context, ids.firstOrNull() ?: "destination")
        }
    }
}
