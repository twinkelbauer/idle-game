import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

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
        topBar = {
            Text("Top bar")
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

            }
        }
    )
}

@Composable
fun Dingi(
    name: String,
    duration: String,
    money: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Icon(Icons.Default.Call, contentDescription = null)
        Column {
            Text("$name")
            Row {
                Text("$duration min")
                Text("$money Euro")
            }
        }
    }
}