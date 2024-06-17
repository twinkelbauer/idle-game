import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {
    Scaffold(
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
            val currentMoney: Gelds by viewModel.currentMoney.collectAsState()
            val now by viewModel.clock.nowState.collectAsState()

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Current tick: ${now / GAME_TICK} sec")
                Text("Current money: $currentMoney Euro")
                Text("Active Workers: ${gameState.workers.size}")

                Button(onClick = { viewModel.clickMoney() }) {
                    Text("Click money")
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(gameState.availableJobs) { availableJob ->
                        Worker(
                            gameJob = availableJob,
                            alreadyBought = gameState.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(availableJob) },
                            onUpgrade = { viewModel.upgradeJob(availableJob) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun Worker(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text("Name 1")
            Text("Level: ${gameJob.level.level}")
            Text("Costs: ${gameJob.level.cost}")
            Text("Earns: ${gameJob.level.earn}")
            Text("Duration: ${gameJob.level.duration.raw} Ticks")
        }
        if (!alreadyBought) {
            Button(onClick = onBuy) {
                Text("Buy")
            }
        } else {
            Text("Bought")
        }
        Button(onClick = onUpgrade) {
            Text("Upgrade")
        }
    }
}