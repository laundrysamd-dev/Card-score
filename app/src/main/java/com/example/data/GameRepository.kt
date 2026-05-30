package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val appDao: AppDao) {
    val allCardGames: Flow<List<CardGame>> = appDao.getAllCardGames()
    val allChessMatches: Flow<List<ChessMatch>> = appDao.getAllChessMatches()

    suspend fun getActiveCardGame(): CardGame? = appDao.getActiveCardGame()
    
    suspend fun getCardGameById(id: Long): CardGame? = appDao.getCardGameById(id)

    suspend fun insertCardGame(cardGame: CardGame): Long = appDao.insertCardGame(cardGame)

    suspend fun updateCardGame(cardGame: CardGame) = appDao.updateCardGame(cardGame)

    suspend fun deleteCardGame(cardGame: CardGame) {
        appDao.deleteCardGame(cardGame)
        appDao.deleteRoundsForGame(cardGame.id)
    }

    suspend fun clearAllCardGames() = appDao.clearAllCardGames()

    fun getRoundsForGameFlow(gameId: Long): Flow<List<CardGameRound>> = appDao.getRoundsForGameFlow(gameId)

    suspend fun getRoundsForGame(gameId: Long): List<CardGameRound> = appDao.getRoundsForGame(gameId)

    suspend fun insertCardGameRound(round: CardGameRound) = appDao.insertCardGameRound(round)

    suspend fun insertCardGameRounds(rounds: List<CardGameRound>) = appDao.insertCardGameRounds(rounds)

    suspend fun deleteRoundsForGame(gameId: Long) = appDao.deleteRoundsForGame(gameId)

    suspend fun deleteSpecificRound(gameId: Long, roundNumber: Int) = appDao.deleteSpecificRound(gameId, roundNumber)

    suspend fun insertChessMatch(match: ChessMatch): Long = appDao.insertChessMatch(match)

    suspend fun clearAllChessMatches() = appDao.clearAllChessMatches()
}
