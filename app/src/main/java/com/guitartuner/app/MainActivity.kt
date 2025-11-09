package com.guitartuner.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.guitartuner.app.ui.screens.TunerScreen
import com.guitartuner.app.ui.theme.GuitarTunerTheme
import com.guitartuner.app.viewmodel.TunerViewModel

class MainActivity : ComponentActivity() {
    
    private val viewModel: TunerViewModel by viewModels()
    private var hasPermission by mutableStateOf(false)
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startListening()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check for microphone permission
        checkMicrophonePermission()
        
        setContent {
            GuitarTunerTheme {
                val tuningState by viewModel.tuningState.collectAsState()
                val currentTuningMode by viewModel.currentTuningMode.collectAsState()
                val selectedStringIndex by viewModel.selectedStringIndex.collectAsState()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1A1A1A)
                ) {
                    if (hasPermission) {
                        TunerScreen(
                            tuningState = tuningState,
                            currentTuningMode = currentTuningMode,
                            selectedStringIndex = selectedStringIndex,
                            onTuningModeChanged = { mode ->
                                viewModel.setTuningMode(mode)
                            },
                            onStringSelected = { stringIndex ->
                                viewModel.selectString(stringIndex)
                            }
                        )
                    } else {
                        PermissionScreen(
                            onRequestPermission = {
                                requestMicrophonePermission()
                            }
                        )
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (hasPermission) {
            viewModel.startListening()
        }
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.stopListening()
    }
    
    private fun checkMicrophonePermission() {
        hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestMicrophonePermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}

/**
 * Screen displayed when microphone permission is not granted
 */
@Composable
fun PermissionScreen(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎸",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Microphone Access Required",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This app needs access to your microphone to detect the pitch of your guitar strings.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FF41)
            )
        ) {
            Text(
                text = "Grant Permission",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


