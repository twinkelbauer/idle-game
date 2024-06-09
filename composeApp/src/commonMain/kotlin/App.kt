import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.GAME_TICK
import util.clock
import util.tick
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

private val availableJobs: List<GameJob> = listOf(
    GameJob(earn = 10, cost = 10, 5.tick),
    GameJob(earn = 20, cost = 20, 7.tick),
    GameJob(earn = 30, cost = 30, 12.tick),
    GameJob(earn = 40, cost = 40, 20.tick),
    GameJob(earn = 50, cost = 50, 30.tick),
)

@Composable
@Preview
fun Screen() {
    Scaffold(
        topBar = {
            Text("Top bar")
        },
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                        clock = clock
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState by viewModel.gameState.collectAsState()
            val currentMoney: Long by viewModel.currentMoney.collectAsState()
            val now by viewModel.clock.nowState.collectAsState()

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Current tick: ${now / GAME_TICK} sec")
                Text("Current money: ${currentMoney} Euro")
                Text("Active Workers: ${gameState.workers.size}")

                Button(onClick = { viewModel.clickMoney() }) {
                    Text("Click money")
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(availableJobs) {
                        Worker(
                            name = "Job ${it.earn} Cost: ${it.cost}",
                            duration = "${it.duration.raw / GAME_TICK} sec",
                            money = "${it.earn} Euro",
                            modifier = Modifier.clickable(enabled = currentMoney > it.cost) {
                                viewModel.addWorker(it)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun Worker(
    name: String,
    duration: String,
    money: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(name)
            Text("Duration: $duration")
            Text("Earns: $money")
        }
    }
}