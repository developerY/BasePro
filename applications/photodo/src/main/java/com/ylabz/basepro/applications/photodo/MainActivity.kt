package com.ylabz.basepro.applications.photodo

//import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreen
import com.ylabz.basepro.applications.photodo.ui.theme.PhotoDoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class) // Needed for scrollBehavior
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoDoTheme {
                // --- THIS IS THE FIX ---
                // 1. Create the scrollBehavior here at the top level.
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


                // 2. Pass the scrollBehavior to MainScreen.
                MainScreen(scrollBehavior = scrollBehavior)
                // --- END OF FIX ---

                //SimpleAdaptiveBottomBar() // Main Composable for Nav3
                /*
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
            }
        }
    }
}

// This Greeting composable is no longer used by MainActivity
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        //RootNavGraph(navController = navController)
    }
}

/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhotoDoTheme {
        Greeting("Android")
    }
}

 */