package com.nammarailu.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammarailu.app.data.Station
import com.nammarailu.app.service.GeofenceManager
import com.nammarailu.app.ui.theme.*
import com.nammarailu.app.viewmodel.MainViewModel

@Composable
fun DestinationAlarmScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val destination    by viewModel.destinationStation.collectAsState()
    val alarmActive    by viewModel.alarmActive.collectAsState()
    val destinations   by viewModel.filteredDestinations.collectAsState()
    val destSearchQuery by viewModel.destSearchQuery.collectAsState()
    val context        = LocalContext.current

    LaunchedEffect(Unit) { viewModel.refreshDestinations() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        StatusBarSpacer()
        TopBackBar(
            title    = "Destination Alarm",
            subtitle = "Rings 5 km before your stop",
            onBack   = onBack,
            trailingContent = {
                Icon(Icons.Default.Alarm, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(24.dp))
            }
        )

        // How it works card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = CardDefaults.cardColors(containerColor = NavyLight)
        ) {
            Row(
                modifier          = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("How it works",
                        color = BrightYellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Select your destination below. The alarm fires automatically when your GPS is within 5 km — even if your phone is in your pocket.",
                        color      = Color.White.copy(alpha = 0.8f),
                        fontSize   = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Active alarm status
        if (alarmActive && destination != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E6B3A))
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        tint = BrightYellow, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alarm set → ${destination!!.name}",
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp)
                        Text("Will ring 5 km before destination",
                            color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = {
                            viewModel.clearDestination()
                            GeofenceManager.removeGeofence(context)
                        }
                    ) {
                        Text("Cancel", color = Color(0xFFFF8A80), fontSize = 12.sp)
                    }
                }
            }
        }

        // Search
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value         = destSearchQuery,
                onValueChange = { viewModel.onDestSearchChanged(it) },
                placeholder   = { Text("Search destination...", color = Color.Gray, fontSize = 13.sp) },
                leadingIcon   = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = BrightYellow)
                },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor      = BrightYellow,
                    unfocusedBorderColor    = Color.Transparent
                )
            )
        }

        // Destination list
        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            Text(
                text     = "Select destination",
                fontSize = 13.sp,
                color    = TextMuted,
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp)
            )
            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(destinations) { station ->
                    DestinationCard(
                        station   = station,
                        isActive  = destination?.id == station.id,
                        onClick   = {
                            viewModel.selectDestination(station)
                            GeofenceManager.addGeofence(context, station)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun DestinationCard(station: Station, isActive: Boolean, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isActive) GeneralLight else CardWhite
        ),
        border    = if (isActive) CardDefaults.outlinedCardBorder() else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 0.dp else 2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(46.dp)
                    .background(
                        if (isActive) SuccessGreen else NavyBlue,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isActive)
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(22.dp))
                else
                    Text(station.id.take(3),
                        color = BrightYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(station.name,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    color = if (isActive) SuccessGreen else TextDark)
                Text(station.state, fontSize = 12.sp, color = TextMuted)
            }
            if (isActive)
                Text("Active", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            else
                Icon(Icons.Default.ChevronRight, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(20.dp))
        }
    }
}
