package com.ylabz.basepro.applications.photodo.ui.navigation.main

// --- THIS IS THE CORRECT, REAL IMPORT ---
// --- END ---
import FabMain
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.core.ui.components.AddCategoryBottomSheet
import com.ylabz.basepro.core.ui.components.AddListBottomSheet
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

enum class TopLevelDestination(val key: NavKey) {
    HOME(PhotoDoNavKeys.HomeFeedKey),
    TASK_LIST(PhotoDoNavKeys.TaskListKey(0)),
    SETTINGS(PhotoDoNavKeys.SettingsKey)
}

fun NavBackStack<NavKey>.isTopLevelDestinationInBackStack(topLevelDestinations: List<NavKey>): Boolean {
    return this.any { entry: NavKey -> topLevelDestinations.any { it::class == entry::class } }
}

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    // 1. Get the one and only STATEFUL ViewModel
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    // 0 Activity
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact


    // 1. COLLECT THE STATE from the single source of truth
    val uiState by mainScreenViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // --- Navigation State ---
    val backStack = rememberNavBackStack(PhotoDoNavKeys.HomeFeedKey)
    // 2. Use the *real* function from the 'adaptive-navigation3' library
    val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()

    // --- Bottom Sheet State ---
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomSheet = uiState.currentSheet != BottomSheetType.NONE

    // --- Top Bar State ---
    var topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) by remember { mutableStateOf({}) }
    val setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit = { topBar = it }

    // --- Back Stack Management ---
    val isAtTopLevel by rememberSaveable(backStack) {
        mutableStateOf(backStack.isTopLevelDestinationInBackStack(TopLevelDestination.entries.map { it.key }))
    }

    var currentTopLevelKey: NavKey by remember(backStack) {
        mutableStateOf(
            backStack.lastOrNull { entry: NavKey ->
                TopLevelDestination.entries.any { dest -> dest.key::class == entry::class }
            } ?: PhotoDoNavKeys.HomeFeedKey
        )
    }

    // --- Remembered State for "Add List" ---
    var lastSelectedCategoryId by rememberSaveable { mutableStateOf(1L) }


    // --- Navigation Handler ---
    /*val onNavigate: (NavKey) -> Unit = { navKey ->
        val keyToNavigate = if (navKey is PhotoDoNavKeys.TaskListKey) {
            PhotoDoNavKeys.TaskListKey(lastSelectedCategoryId)
        } else {
            navKey
        }
        if (currentTopLevelKey::class != keyToNavigate::class) {
            currentTopLevelKey = keyToNavigate
            // --- THIS IS THE FIX ---
            // Call your helper function `replace` (one L)
            // instead of the Java function `replaceAll` (two Ls)
            // backStack.replaceAll({keyToNavigate})
            //NOTE: Need to fix
            backStack.replace(keyToNavigate)
        }
    }*/

    // --- 4. Define the app content ONCE ---
    // This is the NavGraph that contains all the screens
    val appContent = @Composable { modifier: Modifier ->
        PhotoDoNavGraph(
            modifier = modifier,
            backStack = backStack,
            sceneStrategy = sceneStrategy,
            isExpandedScreen = isExpandedScreen,
            scrollBehavior = scrollBehavior,
            setTopBar = { topBar = { it(scrollBehavior) } },

            // 5. Pass the VM functions down
            setFabState = mainScreenViewModel::setFabState,
            onEvent = mainScreenViewModel::onEvent, // Pass the single event handler

            onCategorySelected = { categoryId ->
                lastSelectedCategoryId = categoryId
            }
        )
    }

    // --- 6. This is YOUR correct adaptive layout logic ---
    if (isExpandedScreen) {
        // **Expanded Layout: Show Navigation Rail**
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(
                currentTopLevelKey = currentTopLevelKey,
                onNavigate = { key ->
                    if (key::class != currentTopLevelKey::class) {
                        currentTopLevelKey = key
                        backStack.replace(key)
                    }
                }
            )
            Scaffold(
                // **Expanded Layout: Show Bottom Bar**
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = { topBar(scrollBehavior) },
                floatingActionButton = {
                    // 7. Render the FAB from the VM state
                    FabMain(fabState = uiState.fabState)
                }
            ) { padding ->
                appContent(Modifier.padding(padding))
            }
        }
    } else {
        // **Compact Layout: Show Bottom Bar**
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { topBar(scrollBehavior) },
            bottomBar = {
                HomeBottomBar(
                    currentTopLevelKey = currentTopLevelKey,
                    // onNavigate = onNavigate // Use the correct lambda name
                    onNavigate = { key ->
                        if (key::class != currentTopLevelKey::class) {
                            currentTopLevelKey = key
                            backStack.replace(key)
                        }
                    }
                )
            },
            floatingActionButton = {
                // 7. Render the FAB from the VM state
                FabMain(fabState = uiState.fabState)
            }
        ) { padding ->
            appContent(Modifier.padding(padding))
        }
    }
    // --- END OF YOUR CORRECT LAYOUT LOGIC ---

    // --- END OF YOUR CORRECT LAYOUT LOGIC ---
    // G. SHOW BOTTOM SHEETS based on the collected state
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
            },
            sheetState = modalSheetState
        ) {
            when (uiState.currentSheet) {
                BottomSheetType.ADD_CATEGORY -> AddCategoryBottomSheet(
                    onDismiss = {
                        mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                    },
                    onSaveCategory = { name: String, description: String ->
                        mainScreenViewModel.onEvent(MainScreenEvent.OnSaveCategory(name, description))
                        mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                    }
                )

                BottomSheetType.ADD_LIST -> AddListBottomSheet(
                    onDismiss = { mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed) },
                    onSaveList = { title: String, description: String ->
                        // TODO: Implement save list
                    }
                )

                BottomSheetType.ADD_ITEM -> AddItemBottomSheet(
                    onAddClick = {
                        Log.d(TAG, "Adding Item (Photo) - Not yet implemented")
                        scope.launch { modalSheetState.hide() }.invokeOnCompletion {
                            mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                        }
                    }
                )

                BottomSheetType.NONE -> {}
            }
        }
    }
}

@Composable
fun AddItemBottomSheet(onAddClick: () -> Unit) {
    Text("Add Item Bottom Sheet")
}

fun <T : Any> MutableList<T>.replaceTop(item: T) {
    if (isNotEmpty()) this[lastIndex] = item else add(item)
}