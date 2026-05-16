package tech.soc.soar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.soc.soar.di.AppDependencies
import tech.soc.soar.presentation.navigation.AppNavGraph
import tech.soc.soar.presentation.root.RootViewModel
import tech.soc.soar.presentation.root.RootViewModelFactory
import tech.soc.soar.ui.theme.SoarTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SoarTheme {
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