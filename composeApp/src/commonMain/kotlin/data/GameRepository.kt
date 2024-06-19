package data

import GameState
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGame(): Flow<GameState>
    fun saveGame(gameState: GameState)
}

expect class GameRepositoryImpl() : GameRepository