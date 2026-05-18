package com.nammarailu.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammarailu.app.data.PlatformPing
import com.nammarailu.app.data.Train
import com.nammarailu.app.data.sampleTrains
import com.nammarailu.app.ui.theme.*
import com.nammarailu.app.viewmodel.MainViewModel

@Composable
fun PlatformPingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val station    by viewModel.selectedStation.collectAsState()
    val pings      by viewModel.platformPings.collectAsState()
    val pingStatus by viewModel.pingStatus.collectAsState()

    var showDialog       by remember { mutableStateOf(false) }
    var activeTrainId    by remember { mutableStateOf("") }
    var activeTrainName  by remember { mutableStateOf("") }
    var platformInput    by remember { mutableStateOf("") }

    LaunchedEffect(station?.id) {
        station?.id?.let { viewModel.listenToPlatformPings(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        StatusBarSpacer()
        TopBackBar(
            title    = "Platform Ping",
            subtitle = "${station?.name ?: ""} · live updates",
            onBack   = onBack,
            trailingContent = {
                Icon(Icons.Default.People, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(24.dp))
            }
        )

        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    if (pingStatus.isNotEmpty()) {
                        StatusBanner(message = pingStatus)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                item {
                    Row(
                        modifier          = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live updates from passengers",
                            fontSize   = 12.sp,
                            color      = TextMuted,
                            fontWeight = FontWeight.Medium)
                    }
                }

                items(sampleTrains) { train ->
                    PlatformPingCard(
                        train  = train,
                        ping   = pings[train.id],
                        onConfirm = {
                            if (pings[train.id] != null) {
                                station?.id?.let { sid ->
                                    viewModel.confirmExistingPing(sid, train.id)
                                }
                            } else {
                                activeTrainId   = train.id
                                activeTrainName = train.name
                                showDialog      = true
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // ── Report Dialog ─────────────────────────────────────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest   = { showDialog = false; platformInput = "" },
            containerColor     = CardWhite,
            shape              = RoundedCornerShape(18.dp),
            title = {
                Text("Report Platform",
                    fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
            },
            text = {
                Column {
                    Text("Which platform is $activeTrainName arriving on?",
                        color = TextMuted, fontSize = 14.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value         = platformInput,
                        onValueChange = { if (it.length <= 2) platformInput = it.filter { c -> c.isDigit() } },
                        label         = { Text("Platform number") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrightYellow,
                            focusedLabelColor  = NavyBlue
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val platform = platformInput.toIntOrNull()
                        if (platform != null && platform > 0) {
                            station?.id?.let { sid ->
                                viewModel.submitPlatformPing(sid, activeTrainId, platform)
                            }
                        }
                        showDialog    = false
                        platformInput = ""
                    },
                    enabled = platformInput.isNotEmpty(),
                    colors  = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Submit", color = BrightYellow, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; platformInput = "" }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun PlatformPingCard(
    train: Train,
    ping: PlatformPing?,
    onConfirm: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Train info row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(42.dp)
                        .background(NavyBlue, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Train, contentDescription = null,
                        tint = BrightYellow, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(train.name,
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
                    Text("# ${train.number}  ·  Dep ${train.departureTime}",
                        fontSize = 12.sp, color = TextMuted)
                }
            }

            Divider(
                modifier  = Modifier.padding(vertical = 12.dp),
                color     = DividerColor,
                thickness = 0.5.dp
            )

            // Platform status row
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (ping != null) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Platform ${ping.platform}",
                            color      = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 22.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier          = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(Icons.Default.People, contentDescription = null,
                                tint     = TextMuted,
                                modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${ping.confirmations} passengers confirmed",
                                fontSize = 12.sp, color = TextMuted)
                        }
                    }
                    Button(
                        onClick  = onConfirm,
                        modifier = Modifier.height(38.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null,
                            modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Confirm", fontSize = 13.sp)
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Platform unknown",
                            color      = ErrorRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp)
                        Text("Be the first to report!",
                            fontSize = 12.sp, color = TextMuted)
                    }
                    OutlinedButton(
                        onClick   = onConfirm,
                        modifier  = Modifier.height(38.dp),
                        shape     = RoundedCornerShape(10.dp),
                        colors    = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null,
                            modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Report", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
