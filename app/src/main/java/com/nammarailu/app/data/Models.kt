package com.nammarailu.app.data

// ── Core Models ───────────────────────────────────────────────────────────────

data class Station(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val state: String = "Karnataka"
)

data class Train(
    val id: String,
    val name: String,
    val number: String,
    val departureTime: String,
    val arrivalTime: String,
    val coachLayout: List<Coach>
)

data class Coach(
    val type: CoachType,
    val number: String
)

enum class CoachType { ENGINE, GENERAL, SLEEPER, LADIES, PANTRY }

data class PlatformPing(
    val stationId: String  = "",
    val trainId: String    = "",
    val platform: Int      = 0,
    val confirmations: Int = 0,
    val timestamp: Long    = 0L
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

// ── Station Data ──────────────────────────────────────────────────────────────

val sampleStations = listOf(
    Station("SBC",  "KSR Bengaluru",  12.9775, 77.5713),
    Station("MYS",  "Mysuru",         12.2958, 76.6394),
    Station("MDA",  "Mandya",         12.5218, 76.8951),
    Station("HAS",  "Hassan",         13.0072, 76.0962),
    Station("BRR",  "Birur",          13.5981, 76.0413),
    Station("TK",   "Tumakuru",       13.3379, 77.1173),
    Station("ASK",  "Arsikere",       13.3137, 76.2575),
    Station("DVG",  "Davangere",      14.4644, 75.9218),
    Station("UBL",  "Hubballi",       15.3647, 75.1240),
    Station("BGM",  "Belagavi",       15.8497, 74.4977),
    Station("BAY",  "Ballari",        15.1394, 76.9214),
    Station("GDG",  "Gadag",          15.4165, 75.6260),
    Station("RRB",  "Raichur",        16.2120, 77.3439),
)

// ── Train Data ────────────────────────────────────────────────────────────────

val sampleTrains = listOf(
    Train(
        id = "T01", name = "Mysuru Passenger", number = "56201",
        departureTime = "06:15", arrivalTime = "09:30",
        coachLayout = listOf(
            Coach(CoachType.ENGINE,  "ENG"),
            Coach(CoachType.GENERAL, "GS1"),
            Coach(CoachType.GENERAL, "GS2"),
            Coach(CoachType.GENERAL, "GS3"),
            Coach(CoachType.LADIES,  "LS1"),
            Coach(CoachType.SLEEPER, "S1"),
            Coach(CoachType.SLEEPER, "S2"),
            Coach(CoachType.PANTRY,  "PC"),
        )
    ),
    Train(
        id = "T02", name = "Bangalore Express", number = "56202",
        departureTime = "07:45", arrivalTime = "11:00",
        coachLayout = listOf(
            Coach(CoachType.ENGINE,  "ENG"),
            Coach(CoachType.GENERAL, "GS1"),
            Coach(CoachType.GENERAL, "GS2"),
            Coach(CoachType.LADIES,  "LS1"),
            Coach(CoachType.SLEEPER, "S1"),
            Coach(CoachType.SLEEPER, "S2"),
            Coach(CoachType.SLEEPER, "S3"),
        )
    ),
    Train(
        id = "T03", name = "Hassan Passenger", number = "56203",
        departureTime = "09:00", arrivalTime = "13:15",
        coachLayout = listOf(
            Coach(CoachType.ENGINE,  "ENG"),
            Coach(CoachType.GENERAL, "GS1"),
            Coach(CoachType.GENERAL, "GS2"),
            Coach(CoachType.LADIES,  "LS1"),
            Coach(CoachType.SLEEPER, "S1"),
        )
    ),
    Train(
        id = "T04", name = "Hubli Fast Passenger", number = "56204",
        departureTime = "11:30", arrivalTime = "17:45",
        coachLayout = listOf(
            Coach(CoachType.ENGINE,  "ENG"),
            Coach(CoachType.GENERAL, "GS1"),
            Coach(CoachType.GENERAL, "GS2"),
            Coach(CoachType.GENERAL, "GS3"),
            Coach(CoachType.LADIES,  "LS1"),
            Coach(CoachType.SLEEPER, "S1"),
            Coach(CoachType.SLEEPER, "S2"),
            Coach(CoachType.PANTRY,  "PC"),
        )
    ),
)
