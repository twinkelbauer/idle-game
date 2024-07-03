import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
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
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }


            Column(

                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(Color(245, 245, 160))
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.End

            ) {
                Column() {
                    Text("datum:")
                    Text("3•7•24")
                }
                Text(
                    "Shnée",
                    style = MaterialTheme.typography.h1,
                    color = Color(179, 230, 255)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(
                            255, 153, 0
                        )
                    )
                ) {
                    Text("Reset Game",
                        fontFamily = FontFamily.Serif
                        )
                }

                gameState?.let { state ->
                    Text(
                        "Schneeflocken: ${currentMoney?.toHumanReadableString()} ",
                        style = MaterialTheme.typography.h4,
                    )
                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                244, 244, 244
                            )
                        )
                    ) {
                        Text("Click",)
                    }

                    Text("Zeug zum Kaufen:")
                    Text("ƒ= Flocken, cl= Klick,\n" +
                            "-- xyƒ = Preis")

                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .padding(8.dp)
            .background(Color(255, 202, 12), RoundedCornerShape(3.dp))
            .padding(8.dp)



    ) {
        Column() {
            //Text("Generator ${gameJob.id}")
            //Text("Level: ${gameJob.level.level}")
            Text(" ${gameJob.level.earn.toHumanReadableString()}ƒ/sec")
            Text("-- ${gameJob.level.cost.toHumanReadableString()} ƒ")
            //Text("Duration: ${gameJob.level.duration.inWholeSeconds} ƒ")
        }
        if (!alreadyBought) {
            Button(onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        255, 255, 255
                    )
                )) {
                Text("Buy", color = Color(74, 101, 241))
            }
        } else {
            Text("Bought")
        }
        Button(onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(
                    255, 255, 255
                )
            )) {
            Text("Upgrade", color = Color(74, 101, 241))
        }
    }
}