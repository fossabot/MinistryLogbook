package app.ministrylogbook.ui.home.backup

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import app.ministrylogbook.ui.AppGraph
import app.ministrylogbook.ui.FadeInTransitionMillis
import app.ministrylogbook.ui.FadeOutTransitionMillis
import app.ministrylogbook.ui.SlideInTransitionMillis
import app.ministrylogbook.ui.SlideOutTransitionMillis
import app.ministrylogbook.ui.home.HomeGraph
import app.ministrylogbook.ui.home.backup.viewmodel.BackupViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import org.koin.androidx.compose.getViewModel

sealed class BackupGraph(private val rawRoute: String) {

    object Root : BackupGraph("")

    override fun toString() = route

    val route
        get() = "${AppGraph.Backup}/$rawRoute"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.backupGraph() {
    navigation(
        route = AppGraph.Backup.route,
        startDestination = BackupGraph.Root.route,
        enterTransition = {
            slideInHorizontally(tween(SlideInTransitionMillis)) { it / 6 } + fadeIn(
                tween(FadeInTransitionMillis)
            )
        },
        exitTransition = {
            slideOutHorizontally(tween(SlideOutTransitionMillis)) { it / 6 } + fadeOut(
                tween(FadeOutTransitionMillis)
            )
        }
    ) {
        composable(BackupGraph.Root.route) {
            val viewModel = getViewModel<BackupViewModel>()
            BackupPage(viewModel)
        }
    }
}

fun NavController.navigateToBackup() = navigate(AppGraph.Backup.route) {
    popUpTo(HomeGraph.Root.route)
}
