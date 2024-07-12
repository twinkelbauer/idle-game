package data

import GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import util.gelds

actual class GameRepositoryImpl actual constructor() : GameRepository {

    private var _memory: GameState = GameState(0.gelds, emptyList())

    override fun getGame(): Flow<GameState> = MutableStateFlow(_memory)

    override fun saveGame(gameState: GameState) {
        _memory = gameState
    }
}