import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import aarav.kharade.addharux.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AddharUX Desktop",
    ) {
        App()
    }
}
