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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammarailu.app.data.Train
import com.nammarailu.app.data.sampleTrains
import com.nammarailu.app.ui.theme.*
import com.nammarailu.app.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onTrainSelected: (Train) -> Unit,
    onSetDestination: () -> Unit,
    onOpenAI: () -> Unit,
    onOpenPlatformPing: () -> Unit
) {
    val station     by viewModel.selectedStation.collectAsState()
    val destination by viewModel.destinationStation.collectAsState()
    val alarmActive by viewModel.alarmActive.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        StatusBarSpacer()

        // ── Header ────────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Train, contentDescription = null,
                            tint = BrightYellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Namma-Railu Buddy",
                            color = BrightYellow, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(station?.name ?: "Station",
                        color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(station?.state ?: "",
                        color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                }
                // AI button
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .background(NavyLight, RoundedCornerShape(12.dp))
                        .clickable { onOpenAI() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = "AI Assistant",
                        tint = BrightYellow, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Destination alarm card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetDestination() },
                shape  = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (alarmActive) Color(0xFF1E6B3A) else NavyLight
                )
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector        = if (alarmActive) Icons.Default.Alarm else Icons.Default.AddAlarm,
                        contentDescription = null,
                        tint               = BrightYellow,
                        modifier           = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = if (alarmActive) "Alarm active → ${destination?.name}" else "Set destination alarm",
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp
                        )
                        Text(
                            text     = if (alarmActive) "Will ring 5 km before your stop" else "Get alerted 5 km before your stop",
                            color    = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector        = if (alarmActive) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                        tint               = BrightYellow,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── Train List ────────────────────────────────────────────────────────
        BottomCard {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Train, contentDescription = null,
                        tint = NavyBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Today's trains",
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NavyBlue)
                }
                TextButton(onClick = onOpenPlatformPing) {
                    Icon(Icons.Default.Notifications, contentDescription = null,
                        tint = NavyBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Platform Ping", color = NavyBlue, fontSize = 12.sp)
                }
            }

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleTrains) { train ->
                    TrainCard(train = train, onClick = { onTrainSelected(train) })
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun TrainCard(train: Train, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(50.dp)
                    .background(NavyBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Train, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(train.name,
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
                Text("# ${train.number}",
                    fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoChip(train.departureTime, Icons.Default.Schedule, GeneralGreen)
                    InfoChip(train.arrivalTime,   Icons.Default.Flag,     SleeperBlue)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = BrightYellow, modifier = Modifier.size(22.dp))
        }
    }
}
