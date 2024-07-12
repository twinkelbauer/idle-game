import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import idle_game.composeapp.generated.resources.*
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.emfaticknfregular
import idle_game.composeapp.generated.resources.superfly
import idle_game.composeapp.generated.resources.velcro
import idle_game.composeapp.generated.resources.wunderland
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
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

    val CFFsuperfly = FontFamily(Font(Res.font.superfly, FontWeight.Normal))
    val CFFemfatick = FontFamily(Font(Res.font.emfaticknfregular, FontWeight.Normal))
    val CFFvelcro = FontFamily(Font(Res.font.velcro, FontWeight.Normal))
    val CFFwunderland = FontFamily(Font(Res.font.wunderland, FontWeight.Normal))

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
            var showDialog by remember { mutableStateOf(false) }

            gameState?.let { state ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(end = 16.dp)
                        .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF4A48C3), Color(0xFF807DE3))
                        )
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Spacer(modifier = Modifier.height(100.dp))
                    Image(
                        painterResource(Res.drawable.clickcloud),
                        contentDescription = "Click on this cloud",
                        modifier = Modifier.clickable { viewModel.clickMoney(state) }
                    )
                    val infiniteTransition = rememberInfiniteTransition()
                    val kommaZahl = infiniteTransition.animateFloat(
                        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
                            animation = tween(3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Image(
                        painterResource(Res.drawable.snowflake),
                        contentDescription = "Falling snowflake",
                        modifier = Modifier.offset(
                            x = 0.dp, y = kommaZahl.value.dp)
                            .rotate(kommaZahl.value)
                    )
                }
            }

            Column(modifier = Modifier
                .background(Color(0, 0, 0, 0))
                .padding(start = 16.dp, top = 16.dp)
            ){
                Column(modifier = Modifier
                    .clip(shape = RoundedCornerShape(3.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0x759DF3FF), Color(0x75D4F9FE))
                        )
                    )
                    .padding(start = 12.dp,  end = 12.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text(
                        "Schneeflocken:",
                        style = MaterialTheme.typography.h4,
                        fontFamily = CFFemfatick,
                        color = Color(255, 255, 255),
                    )
                    Text(
                        "${currentMoney?.toHumanReadableString()} ",
                        fontFamily = CFFvelcro,
                        style = MaterialTheme.typography.h2,
                        color = Color(255, 255, 255),
                    )
                    Image(
                        painterResource(Res.drawable.Dastollebild),
                        contentDescription = "picture of a town on an island",
                    )

                }
            }

            gameState?.let { state ->
                Column(modifier = Modifier
                    .background(Color(0, 0, 0, 0))
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {

                    Column(modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
                        .background(Color(255, 245, 160)) //dieses Sandfarbene gelb
                        .fillMaxHeight()
                        .padding(start = 12.dp,  end = 12.dp, top = 4.dp, bottom = 4.dp)
                    ){
                        Text(
                            "ZEUG zUm\n" +
                                    "KAUFeN:",
                            fontFamily = CFFsuperfly,
                            style = MaterialTheme.typography.h2,
                        )
                        Text(
                            "ƒ= Flocken, cl= Klick,\n" +
                                    "-- xyƒ = Preis",
                            fontFamily = CFFemfatick,
                            style = MaterialTheme.typography.h4
                        )

                        state.availableJobs.forEach { availableJob ->
                            Generator(
                                gameJob = availableJob,
                                alreadyBought = state.workers.any { it.jobId == availableJob.id },
                                onBuy = { viewModel.addWorker(state, availableJob) },
                                onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                            )
                        }

                        Button(
                            onClick = { viewModel.reset() },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(
                                    255, 153, 0
                                )
                            )
                        ) {
                            Text(
                                "Reset Game",
                                fontFamily = FontFamily.Serif
                            )
                        }
                        Text("© 2024")
                    }
                }
            }


            /*Column(

                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(end = 16.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF4A48C3), Color(0xFF807DE3))
                        )
                    ),
                horizontalAlignment = Alignment.Start,

                ) {

                Column() {
                    Text("© 2024")
                }
                Column(){
                        Text(
                            "Schneeflocken:",
                            style = MaterialTheme.typography.h4,
                            fontFamily = CFFemfatick
                        )
                        Text(
                            "${currentMoney?.toHumanReadableString()} ",
                            fontFamily = CFFvelcro,
                            style = MaterialTheme.typography.h2,
                        )
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
                    Text(
                        "Reset Game",
                        fontFamily = FontFamily.Serif
                    )
                }

                gameState?.let { state ->




                }
            }

             */
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
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(
                        255, 255, 255
                    )
                )
            ) {
                Text("Buy", color = Color(74, 101, 241))
            }
        } else {
            Text("Bought")
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(
                    255, 255, 255
                )
            )
        ) {
            Text("Upgrade", color = Color(74, 101, 241))
        }
    }
}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "This is a minimal dialog",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}







