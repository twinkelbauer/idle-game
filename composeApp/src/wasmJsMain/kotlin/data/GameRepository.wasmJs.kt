package data

import GameState
import gelds
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.Storage
import org.w3c.dom.set

actual class GameRepositoryImpl actual constructor() : GameRepository {

    private val storage: Storage by lazy { window.localStorage }
    private val jsonSerializer = Json {
        prettyPrint = false
    }

    override fun getGame(): GameState {
        storage.getItem(GAME_STATE_KEY)?.let {
            return jsonSerializer.decodeFromString(it)
        } ?: return GameState(0.gelds, emptyList())
    }

    override fun saveGame(gameState: GameState) {
        storage[GAME_STATE_KEY] = jsonSerializer.encodeToString(gameState)
    }

    companion object {
        private const val GAME_STATE_KEY = "gameState"
    }
}