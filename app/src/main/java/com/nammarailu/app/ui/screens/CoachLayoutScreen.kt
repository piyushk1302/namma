package com.nammarailu.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammarailu.app.data.Coach
import com.nammarailu.app.data.CoachType
import com.nammarailu.app.ui.theme.*
import com.nammarailu.app.viewmodel.MainViewModel

@Composable
fun CoachLayoutScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onPlatformPing: () -> Unit
) {
    val train by viewModel.selectedTrain.collectAsState()
    val coaches = train?.coachLayout ?: emptyList()
    val generalIdx = coaches.indexOfFirst { it.type == CoachType.GENERAL }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        StatusBarSpacer()
        TopBackBar(
            title    = train?.name ?: "Coach Layout",
            subtitle = "# ${train?.number ?: ""} · Dep ${train?.departureTime ?: ""}",
            onBack   = onBack,
            trailingContent = {
                TextButton(onClick = onPlatformPing) {
                    Text("Platform Ping", color = BrightYellow, fontSize = 12.sp,
                        fontWeight = FontWeight.Medium)
                }
            }
        )

        // Legend
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CoachLegendChip("Engine",  EngineGray)
            CoachLegendChip("General", GeneralGreen)
            CoachLegendChip("Sleeper", SleeperBlue)
            CoachLegendChip("Ladies",  LadiesRed)
        }

        // Coach cards
        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("← Front (Engine side)",
                        color    = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 4.dp))
                }

                itemsIndexed(coaches) { index, coach ->
                    CoachCard(coach = coach, position = index + 1)
                }

                item { Spacer(modifier = Modifier.height(4.dp)) }
                item {
                    Text("→ Rear (Last coach)",
                        color    = TextMuted,
                        fontSize = 11.sp)
                }

                if (generalIdx >= 0) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = CardDefaults.cardColors(containerColor = GeneralLight)
                        ) {
                            Row(
                                modifier          = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null,
                                    tint     = GeneralGreen,
                                    modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text     = "General Coach is at position ${generalIdx + 1}. Board from the engine side of the platform.",
                                    color    = GeneralGreen,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CoachCard(coach: Coach, position: Int) {
    val (color, label, emoji) = when (coach.type) {
        CoachType.ENGINE  -> Triple(EngineGray,   "Engine / Locomotive",         "🚂")
        CoachType.GENERAL -> Triple(GeneralGreen, "General Coach (Unreserved)",   "🟢")
        CoachType.SLEEPER -> Triple(SleeperBlue,  "Sleeper Coach (Reserved)",     "💙")
        CoachType.LADIES  -> Triple(LadiesRed,    "Ladies Coach (Women only)",    "🔴")
        CoachType.PANTRY  -> Triple(PantryOrange, "Pantry / Catering Car",        "🍱")
    }
    val borderColor = when (coach.type) {
        CoachType.GENERAL -> GeneralGreen
        CoachType.LADIES  -> LadiesRed
        CoachType.SLEEPER -> SleeperBlue
        CoachType.PANTRY  -> PantryOrange
        else              -> Color.Transparent
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(28.dp), contentAlignment = Alignment.Center) {
            Text("$position", color = TextMuted, fontSize = 11.sp)
        }
        Card(
            modifier  = Modifier.weight(1f),
            shape     = RoundedCornerShape(12.dp),
            colors    = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .let {
                        if (borderColor != Color.Transparent)
                            it.background(borderColor.copy(alpha = 0.05f))
                        else it
                    }
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(coach.number,
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label,
                        fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
                    Text("Coach ${coach.number}",
                        fontSize = 11.sp, color = TextMuted)
                }
                Text(emoji, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun CoachLegendChip(label: String, color: Color) {
    Row(
        modifier          = Modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .size(7.dp)
            .background(color, RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.width(5.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
