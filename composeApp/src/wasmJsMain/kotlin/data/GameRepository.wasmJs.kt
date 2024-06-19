package data

import GameState
import gelds
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.Storage
import org.w3c.dom.set

actual class GameRepositoryImpl actual constructor() : GameRepository {

    private val storage: Storage by lazy { window.localStorage }
    private val jsonSerializer = Json {
        prettyPrint = false
    }
    private val gameStateFlow: MutableStateFlow<GameState> =
        MutableStateFlow(
            storage.getItem(GAME_STATE_KEY)?.let {
                jsonSerializer.decodeFromString(it)
            } ?: GameState(0, 0.gelds, emptyList())
        )

    override fun getGame(): Flow<GameState> = gameStateFlow

    override fun saveGame(gameState: GameState) {
        val newState = gameState.copy(savedAt = Clock.System.now().toEpochMilliseconds())
        storage[GAME_STATE_KEY] = jsonSerializer.encodeToString(newState)
        gameStateFlow.update { newState }
    }

    companion object {
        private const val GAME_STATE_KEY = "gameState"
    }
}