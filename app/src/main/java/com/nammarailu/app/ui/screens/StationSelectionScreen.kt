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
import com.nammarailu.app.data.Station
import com.nammarailu.app.ui.theme.*
import com.nammarailu.app.viewmodel.MainViewModel

@Composable
fun StationSelectionScreen(
    viewModel: MainViewModel,
    onStationSelected: (Station) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stations    by viewModel.filteredStations.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        StatusBarSpacer()

        // ── Header ────────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Train, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Namma-Railu Buddy",
                    color = BrightYellow, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("Select your station",
                color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Where are you boarding today?",
                color = Color.White.copy(alpha = 0.55f), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value          = searchQuery,
                onValueChange  = { viewModel.onSearchQueryChanged(it) },
                placeholder    = { Text("Search station...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon    = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = BrightYellow)
                },
                trailingIcon   = {
                    if (searchQuery.isNotEmpty())
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                },
                modifier       = Modifier.fillMaxWidth(),
                shape          = RoundedCornerShape(14.dp),
                singleLine     = true,
                colors         = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor      = BrightYellow,
                    unfocusedBorderColor    = Color.Transparent,
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Station List ──────────────────────────────────────────────────────
        BottomCard {
            Row(
                modifier          = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null,
                    tint = NavyBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("${stations.size} stations available",
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = NavyBlue)
            }

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stations) { station ->
                    StationCard(station = station, onClick = { onStationSelected(station) })
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun StationCard(station: Station, onClick: () -> Unit) {
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
                    .size(46.dp)
                    .background(NavyBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = station.id.take(3),
                    color      = BrightYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(station.name,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDark)
                Text(station.state,
                    fontSize = 12.sp, color = TextMuted)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = BrightYellow, modifier = Modifier.size(22.dp))
        }
    }
}
