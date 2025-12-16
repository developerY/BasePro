package com.ylabz.basepro.ashbike.mobile.features.glass.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GearSelectionScreen
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiEvent
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassViewModel
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.HomeScreen
import kotlinx.serialization.Serializable

/* -------------------------------------------------------------------------- */
/* Nav Keys (Routes)                                                           */
/* -------------------------------------------------------------------------- */

sealed interface GlassRoute : NavKey {

    @Serializable
    data object Home : GlassRoute

    @Serializable
    data object GearList : GlassRoute
}

/* -------------------------------------------------------------------------- */
/* Router                                                                      */
/* -------------------------------------------------------------------------- */

@Composable
fun GlassRouter(
    onClose: () -> Unit
) {
    /* ---------------------------------------------------------------------- */
    /* Back stack                                                             */
    /* ---------------------------------------------------------------------- */

    val backStack = rememberNavBackStack( GlassRoute.Home)


    /*
 * IMPORTANT:
 * This lambda runs in the context of a NavBackStackEntry.
 * That entry IS a ViewModelStoreOwner.
 * This is the correct Nav3 ViewModel scope.
 */
    // ✅ Composable scope here
    val viewModel: GlassViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    /* ---------------------------------------------------------------------- */
    /* NavDisplay                                                             */
    /* ---------------------------------------------------------------------- */

    NavDisplay(
        backStack = backStack,
        modifier = Modifier
    ) { key ->


        when (key) {

            /* -------------------------------------------------------------- */
            /* Home                                                           */
            /* -------------------------------------------------------------- */

            GlassRoute.Home -> {
                HomeScreen(
                    currentGear = uiState.currentGear,
                    onGearChange = { gear ->
                        viewModel.onEvent(
                            //GlassUiEvent.OnGearChange(gear)
                        )
                    },
                    onOpenGearList = {
                        // Navigate forward
                        backStack.add(GlassRoute.GearList)
                    },
                    onClose = onClose
                )
            }

            /* -------------------------------------------------------------- */
            /* Gear List                                                      */
            /* -------------------------------------------------------------- */

            GlassRoute.GearList -> {
                GearSelectionScreen(
                    currentGear = uiState.currentGear,
                    onGearSelected = { selectedGear ->
                        viewModel.onEvent(
                            //GlassUiEvent.OnGearSelect(selectedGear)
                        )
                        // Navigate back
                        backStack.removeLast()
                    }
                )
            }
        }
    }
}
