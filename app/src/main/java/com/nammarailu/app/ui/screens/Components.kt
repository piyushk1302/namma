package com.nammarailu.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammarailu.app.ui.theme.*

// ── Status Bar Spacer ─────────────────────────────────────────────────────────
@Composable
fun StatusBarSpacer() {
    Spacer(modifier = Modifier.height(28.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("9:41", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        Text("▐▐▐ WiFi ██", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
    }
}

// ── Top Back Bar ──────────────────────────────────────────────────────────────
@Composable
fun TopBackBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                tint = BrightYellow, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            if (subtitle.isNotEmpty())
                Text(subtitle, color = BrightYellow, fontSize = 12.sp)
        }
        trailingContent?.invoke()
    }
}

// ── Bottom White Card ─────────────────────────────────────────────────────────
@Composable
fun BottomCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape    = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
        content  = { Column(modifier = Modifier.fillMaxSize(), content = content) }
    )
}

// ── Section Label ─────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String, icon: ImageVector) {
    Row(
        modifier          = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null,
            tint = NavyBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = NavyBlue)
    }
}

// ── Status Toast ──────────────────────────────────────────────────────────────
@Composable
fun StatusBanner(message: String, isSuccess: Boolean = true) {
    if (message.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(
                if (isSuccess) SuccessLight else ErrorLight,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isSuccess) "✓  $message" else "⚠  $message",
            color = if (isSuccess) SuccessGreen else ErrorRed,
            fontSize = 13.sp
        )
    }
}

// ── Chip Row ──────────────────────────────────────────────────────────────────
@Composable
fun InfoChip(label: String, icon: ImageVector, color: Color) {
    Row(
        modifier          = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
