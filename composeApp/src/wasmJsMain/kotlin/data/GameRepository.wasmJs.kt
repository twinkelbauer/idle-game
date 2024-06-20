package data

import GameState
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.Storage
import org.w3c.dom.set
import util.gelds

actual class GameRepositoryImpl actual constructor() : GameRepository {

    private val storage: Storage by lazy { window.localStorage }
    private val jsonSerializer = Json {
        prettyPrint = false
    }
    private val gameStateFlow: MutableStateFlow<GameState> = MutableStateFlow(getStateFromStorage())

    override fun getGame(): Flow<GameState> = gameStateFlow

    override fun saveGame(gameState: GameState) {
        storage[GAME_STATE_KEY] = jsonSerializer.encodeToString(gameState)
        gameStateFlow.update { gameState }
    }

    private fun getStateFromStorage(): GameState = runCatching {
        val string = requireNotNull(storage.getItem(GAME_STATE_KEY))
        jsonSerializer.decodeFromString<GameState>(string)
    }
        .onFailure { storage.removeItem(GAME_STATE_KEY) }
        .getOrDefault(GameState(0.gelds, emptyList()))

    companion object {
        private const val GAME_STATE_KEY = "gameState"
    }
}