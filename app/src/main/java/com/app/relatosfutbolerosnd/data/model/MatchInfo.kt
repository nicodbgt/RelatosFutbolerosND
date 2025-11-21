package com.app.relatosfutbolerosnd.data.model

data class MatchInfo(
    val team1Name: String = " ",
    val team2Name: String = " ",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val matchTime: String = "00:00",
    val isMatchRunning: Boolean = false,
    val totalSeconds: Int = 0
)
