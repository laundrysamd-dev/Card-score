package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Card Games
    @Query("SELECT * FROM card_games ORDER BY dateMillis DESC")
    fun getAllCardGames(): Flow<List<CardGame>>

    @Query("SELECT * FROM card_games WHERE isFinished = 0 LIMIT 1")
    suspend fun getActiveCardGame(): CardGame?

    @Query("SELECT * FROM card_games WHERE id = :id LIMIT 1")
    suspend fun getCardGameById(id: Long): CardGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardGame(cardGame: CardGame): Long

    @Update
    suspend fun updateCardGame(cardGame: CardGame)

    @Delete
    suspend fun deleteCardGame(cardGame: CardGame)

    @Query("DELETE FROM card_games")
    suspend fun clearAllCardGames()

    // Card Game Rounds
    @Query("SELECT * FROM card_game_rounds WHERE gameId = :gameId ORDER BY roundNumber ASC")
    fun getRoundsForGameFlow(gameId: Long): Flow<List<CardGameRound>>

    @Query("SELECT * FROM card_game_rounds WHERE gameId = :gameId ORDER BY roundNumber ASC")
    suspend fun getRoundsForGame(gameId: Long): List<CardGameRound>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardGameRound(round: CardGameRound)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardGameRounds(rounds: List<CardGameRound>)

    @Query("DELETE FROM card_game_rounds WHERE gameId = :gameId")
    suspend fun deleteRoundsForGame(gameId: Long)

    @Query("DELETE FROM card_game_rounds WHERE gameId = :gameId AND roundNumber = :roundNumber")
    suspend fun deleteSpecificRound(gameId: Long, roundNumber: Int)

    // Chess Matches
    @Query("SELECT * FROM chess_matches ORDER BY dateMillis DESC")
    fun getAllChessMatches(): Flow<List<ChessMatch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChessMatch(match: ChessMatch): Long

    @Query("DELETE FROM chess_matches")
    suspend fun clearAllChessMatches()
}
