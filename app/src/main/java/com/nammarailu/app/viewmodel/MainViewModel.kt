package com.nammarailu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nammarailu.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val db = Firebase.database.reference

    // ── Station Selection ─────────────────────────────────────────────────────
    private val _searchQuery      = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredStations = MutableStateFlow(sampleStations)
    val filteredStations: StateFlow<List<Station>> = _filteredStations.asStateFlow()

    private val _selectedStation  = MutableStateFlow<Station?>(null)
    val selectedStation: StateFlow<Station?> = _selectedStation.asStateFlow()

    private val _destinationStation = MutableStateFlow<Station?>(null)
    val destinationStation: StateFlow<Station?> = _destinationStation.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _filteredStations.value = if (query.isBlank()) sampleStations
        else sampleStations.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun selectStation(station: Station) {
        _selectedStation.value = station
        listenToPlatformPings(station.id)
    }

    fun selectDestination(station: Station) {
        _destinationStation.value = station
        _alarmActive.value = true
    }

    fun clearDestination() {
        _destinationStation.value = null
        _alarmActive.value = false
    }

    // ── Train ─────────────────────────────────────────────────────────────────
    private val _selectedTrain = MutableStateFlow<Train?>(null)
    val selectedTrain: StateFlow<Train?> = _selectedTrain.asStateFlow()

    fun selectTrain(train: Train) { _selectedTrain.value = train }

    // ── Platform Ping ─────────────────────────────────────────────────────────
    private val _platformPings = MutableStateFlow<Map<String, PlatformPing>>(emptyMap())
    val platformPings: StateFlow<Map<String, PlatformPing>> = _platformPings.asStateFlow()

    private val _pingStatus = MutableStateFlow("")
    val pingStatus: StateFlow<String> = _pingStatus.asStateFlow()

    fun listenToPlatformPings(stationId: String) {
        db.child("pings").child(stationId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pings = mutableMapOf<String, PlatformPing>()
                    snapshot.children.forEach { trainSnap ->
                        val trainId       = trainSnap.key ?: return@forEach
                        val platform      = trainSnap.child("platform").getValue(Int::class.java) ?: 0
                        val confirmations = trainSnap.child("confirmations").getValue(Int::class.java) ?: 0
                        val timestamp     = trainSnap.child("timestamp").getValue(Long::class.java) ?: 0L
                        pings[trainId]    = PlatformPing(stationId, trainId, platform, confirmations, timestamp)
                    }
                    _platformPings.value = pings
                }
                override fun onCancelled(error: DatabaseError) {
                    _pingStatus.value = "Connection error: ${error.message}"
                }
            })
    }

    fun submitPlatformPing(stationId: String, trainId: String, platform: Int) {
        viewModelScope.launch {
            val ref = db.child("pings").child(stationId).child(trainId)
            ref.child("platform").setValue(platform)
            ref.child("confirmations").setValue(ServerValue.increment(1))
            ref.child("timestamp").setValue(System.currentTimeMillis())
            _pingStatus.value = "Platform $platform reported — thank you!"
        }
    }

    fun confirmExistingPing(stationId: String, trainId: String) {
        viewModelScope.launch {
            db.child("pings").child(stationId).child(trainId)
                .child("confirmations").setValue(ServerValue.increment(1))
            _pingStatus.value = "Confirmation added — thank you!"
        }
    }

    fun clearPingStatus() { _pingStatus.value = "" }

    // ── AI Chat ───────────────────────────────────────────────────────────────
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            "Namaskara! I'm your Namma-Railu Buddy. Ask me about platforms, coaches, or train delays — in Kannada or English.",
            isUser = false
        )
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiQuery = MutableStateFlow("")
    val aiQuery: StateFlow<String> = _aiQuery.asStateFlow()

    fun onAiQueryChanged(q: String) { _aiQuery.value = q }

    fun sendAiMessage() {
        val query = _aiQuery.value.trim()
        if (query.isBlank() || _aiLoading.value) return

        _chatMessages.value = _chatMessages.value + ChatMessage(query, isUser = true)
        _aiQuery.value = ""
        _aiLoading.value = true

        viewModelScope.launch {
            try {
                val station    = _selectedStation.value?.name ?: "Unknown"
                val dest       = _destinationStation.value?.name ?: "not set"
                val pingsText  = _platformPings.value.entries.joinToString("\n") {
                    "Train ${it.key}: Platform ${it.value.platform}, confirmed by ${it.value.confirmations} passengers"
                }.ifEmpty { "No live pings yet" }

                val prompt = """
                    You are Namma-Railu Buddy, a friendly Indian railway assistant for local passenger trains in Karnataka.
                    Current station: $station
                    Destination: $dest
                    Live platform pings:
                    $pingsText
                    
                    Passenger's question: $query
                    
                    Reply helpfully in 2-3 sentences. If asked in Kannada, reply in Kannada.
                    If asked about platforms, use the live ping data above.
                    Be warm and concise like a helpful fellow passenger.
                """.trimIndent()

                // TODO: Replace with your Gemini API key from aistudio.google.com (free)
                val apiKey = "YOUR_GEMINI_API_KEY"
                val model  = com.google.ai.client.generativeai.GenerativeModel(
                    modelName = "gemini-pro",
                    apiKey    = apiKey
                )
                val response = model.generateContent(prompt)
                val reply    = response.text ?: "Sorry, I could not understand that. Please try again."
                _chatMessages.value = _chatMessages.value + ChatMessage(reply, isUser = false)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    "AI assistant is unavailable right now. Please add your Gemini API key in MainViewModel.kt",
                    isUser = false
                )
            }
            _aiLoading.value = false
        }
    }

    // ── Alarm ─────────────────────────────────────────────────────────────────
    private val _alarmActive = MutableStateFlow(false)
    val alarmActive: StateFlow<Boolean> = _alarmActive.asStateFlow()

    // ── Destination station search ────────────────────────────────────────────
    private val _destSearchQuery     = MutableStateFlow("")
    val destSearchQuery: StateFlow<String> = _destSearchQuery.asStateFlow()

    private val _filteredDestinations = MutableStateFlow(sampleStations)
    val filteredDestinations: StateFlow<List<Station>> = _filteredDestinations.asStateFlow()

    fun onDestSearchChanged(query: String) {
        _destSearchQuery.value = query
        val source = _selectedStation.value
        _filteredDestinations.value = sampleStations
            .filter { it.id != source?.id }
            .let { list ->
                if (query.isBlank()) list
                else list.filter { it.name.contains(query, ignoreCase = true) }
            }
    }

    fun refreshDestinations() {
        val source = _selectedStation.value
        _filteredDestinations.value = sampleStations.filter { it.id != source?.id }
    }
}
