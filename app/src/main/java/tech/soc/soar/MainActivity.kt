package tech.soc.soar

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.soc.soar.di.AppDependencies
import tech.soc.soar.presentation.navigation.AppNavGraph
import tech.soc.soar.presentation.root.RootViewModel
import tech.soc.soar.presentation.root.RootViewModelFactory
import tech.soc.soar.ui.theme.SoarTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SoarTheme {
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
                val isDarkTheme = isSystemInDarkTheme()

                SideEffect {
                    window.statusBarColor = backgroundColor
                    window.navigationBarColor = backgroundColor

                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !isDarkTheme
                        isAppearanceLightNavigationBars = !isDarkTheme
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    val rootViewModel: RootViewModel = viewModel(
                        factory = RootViewModelFactory(
                            checkSessionUseCase = AppDependencies.checkSessionUseCase
                        )
                    )

                    AppNavGraph(
                        rootViewModel = rootViewModel
                    )
                }
            }
        }
    }
}