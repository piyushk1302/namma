package com.nammarailu.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.nammarailu.app.data.ChatMessage
import com.nammarailu.app.ui.theme.*
import com.nammarailu.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val messages   by viewModel.chatMessages.collectAsState()
    val aiQuery    by viewModel.aiQuery.collectAsState()
    val aiLoading  by viewModel.aiLoading.collectAsState()
    val listState  = rememberLazyListState()
    val scope      = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
    ) {
        StatusBarSpacer()
        TopBackBar(
            title    = "AI Assistant",
            subtitle = "Ask in Kannada or English",
            onBack   = onBack,
            trailingContent = {
                Icon(Icons.Default.SmartToy, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(24.dp))
            }
        )

        // Quick prompts
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Platform?", "Coach position?", "ತಡ ಆಗುತ್ತಾ?").forEach { prompt ->
                SuggestionChip(
                    onClick = {
                        viewModel.onAiQueryChanged(prompt)
                        viewModel.sendAiMessage()
                    },
                    label = { Text(prompt, fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = NavyLight,
                        labelColor     = BrightYellow
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled       = true,
                        borderColor   = NavyLight,
                        borderWidth   = 0.dp
                    )
                )
            }
        }

        // Chat area
        Card(
            modifier = Modifier.weight(1f),
            shape    = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            LazyColumn(
                state               = listState,
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier            = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
                if (aiLoading) {
                    item { TypingIndicator() }
                }
                item { Spacer(modifier = Modifier.height(4.dp)) }
            }

            // Input row
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .background(CardWhite)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value         = aiQuery,
                    onValueChange = { viewModel.onAiQueryChanged(it) },
                    placeholder   = { Text("Ask about your train...", fontSize = 13.sp, color = Color.Gray) },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = BrightYellow,
                        unfocusedBorderColor = DividerColor
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .background(
                            if (aiQuery.isNotBlank() && !aiLoading) NavyBlue else Color.LightGray,
                            RoundedCornerShape(12.dp)
                        )
                        .let {
                            if (aiQuery.isNotBlank() && !aiLoading)
                                it.then(Modifier.background(NavyBlue, RoundedCornerShape(12.dp)))
                            else it
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick  = { viewModel.sendAiMessage() },
                        enabled  = aiQuery.isNotBlank() && !aiLoading
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send",
                            tint = BrightYellow, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .background(NavyBlue, RoundedCornerShape(8.dp))
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = null,
                    tint = BrightYellow, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 270.dp)
                .background(
                    if (message.isUser) NavyBlue else CardWhite,
                    RoundedCornerShape(
                        topStart     = 14.dp,
                        topEnd       = 14.dp,
                        bottomStart  = if (message.isUser) 14.dp else 4.dp,
                        bottomEnd    = if (message.isUser) 4.dp else 14.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text      = message.text,
                color     = if (message.isUser) Color.White else TextDark,
                fontSize  = 13.sp,
                lineHeight = 19.sp
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .background(BrightYellow, RoundedCornerShape(8.dp))
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null,
                    tint = NavyBlue, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier         = Modifier
                .size(30.dp)
                .background(NavyBlue, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = null,
                tint = BrightYellow, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(CardWhite, RoundedCornerShape(14.dp, 14.dp, 14.dp, 4.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            CircularProgressIndicator(
                modifier  = Modifier.size(16.dp),
                color     = NavyBlue,
                strokeWidth = 2.dp
            )
        }
    }
}
