package data

import GameState

interface GameRepository {
    fun getGame(): GameState
    fun saveGame(gameState: GameState)
}

expect class GameRepositoryImpl() : GameRepository