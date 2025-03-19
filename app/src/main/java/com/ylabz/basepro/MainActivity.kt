package com.ylabz.basepro

import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.ui.navigation.root.RootNavGraph
import com.ylabz.basepro.ui.theme.BaseProTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Use the non-compose property delegate to obtain the ViewModel
    private val nfcViewModel: NfcViewModel by viewModels()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action in listOf(
                NfcAdapter.ACTION_NDEF_DISCOVERED,
                NfcAdapter.ACTION_TECH_DISCOVERED,
                NfcAdapter.ACTION_TAG_DISCOVERED
            )
        ) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            }

            tag?.let {
                nfcViewModel.onNfcTagScanned(it)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BaseProTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppUI(innerPadding = innerPadding)
                    //TwinTabView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class
)
@Composable
fun AppUI(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    // padding in scaffold.
    RootNavGraph(navHostController = navController )
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BaseProTheme {
        Greeting("Android")
    }
}