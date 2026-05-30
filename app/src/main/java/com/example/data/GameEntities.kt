package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_games")
data class CardGame(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val team1Name: String = "Team 1",
    val team1Player1: String = "Player 1",
    val team1Player2: String = "Player 2",
    val team2Name: String = "Team 2",
    val team2Player1: String = "Player 1",
    val team2Player2: String = "Player 2",
    val isFinished: Boolean = false
)

@Entity(
    tableName = "card_game_rounds",
    primaryKeys = ["gameId", "roundNumber"]
)
data class CardGameRound(
    val gameId: Long,
    val roundNumber: Int,
    val team1Score: Int,
    val team2Score: Int
)

@Entity(tableName = "chess_matches")
data class ChessMatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val outcome: Int = 0 // 1 = Player 1 Win, 2 = Player 2 Win, 0 = Draw
)
