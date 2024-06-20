package data

import GameState

actual class GameRepositoryImpl actual constructor() : GameRepository {

    private var _memory: GameState = GameState(0, emptyList())

    override fun getGame(): GameState = _memory

    override fun saveGame(gameState: GameState) {
        _memory = gameState
    }
}