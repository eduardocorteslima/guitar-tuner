package com.guitartuner.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guitartuner.app.data.TuningMode
import com.guitartuner.app.data.TuningState
import com.guitartuner.app.ui.components.TuningBar

/**
 * Main tuner screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreen(
    tuningState: TuningState,
    currentTuningMode: TuningMode,
    selectedStringIndex: Int?,
    onTuningModeChanged: (TuningMode) -> Unit,
    onStringSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTuningModeMenu by remember { mutableStateOf(false) }
    val chromaticNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: Tuning mode selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTuningModeMenu = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tuning Mode:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = "${currentTuningMode.displayName} ▼",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF00FF41),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Dropdown menu
                DropdownMenu(
                    expanded = showTuningModeMenu,
                    onDismissRequest = { showTuningModeMenu = false },
                    modifier = Modifier.background(Color(0xFF2A2A2A))
                ) {
                    TuningMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = mode.displayName,
                                    color = Color.White
                                )
                            },
                            onClick = {
                                onTuningModeChanged(mode)
                                showTuningModeMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Middle section: Note display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Show which string is selected
                if (selectedStringIndex != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF00FF41)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "🎯 Afinando Corda ${selectedStringIndex + 1}: ${currentTuningMode.noteNames[selectedStringIndex]}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Current detected note
                Text(
                    text = "Note:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    fontSize = 20.sp
                )
                
                Text(
                    text = tuningState.detectedNote.displayName,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Frequency display
                if (tuningState.detectedNote.frequency > 0) {
                    Text(
                        text = "${String.format("%.2f", tuningState.detectedNote.frequency)} Hz",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontSize = 24.sp
                    )
                } else {
                    Text(
                        text = "Listening...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Chromatic scale display
                ChromaticScaleDisplay(
                    chromaticNotes = chromaticNotes,
                    currentNote = tuningState.detectedNote.name,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Bottom section: Tuning bar
            TuningBar(
                cents = tuningState.cents,
                tuningStatus = tuningState.tuningStatus,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Target notes for current tuning with string selector
            TargetNotesDisplay(
                targetNotes = currentTuningMode.noteNames,
                selectedStringIndex = selectedStringIndex,
                onStringSelected = onStringSelected,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Displays the chromatic scale with the current note highlighted
 */
@Composable
fun ChromaticScaleDisplay(
    chromaticNotes: List<String>,
    currentNote: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        chromaticNotes.forEach { note ->
            val isActive = note == currentNote && currentNote != "?"
            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) Color(0xFF00FF41) else Color.Gray,
                fontSize = if (isActive) 20.sp else 16.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * Displays the target notes for the current tuning mode with selection capability
 */
@Composable
fun TargetNotesDisplay(
    targetNotes: List<String>,
    selectedStringIndex: Int?,
    onStringSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selecione a Corda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                // "All strings" button
                TextButton(
                    onClick = { onStringSelected(null) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (selectedStringIndex == null) Color(0xFF00FF41) else Color.Gray
                    )
                ) {
                    Text(
                        text = "Todas",
                        fontSize = 12.sp,
                        fontWeight = if (selectedStringIndex == null) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                targetNotes.forEachIndexed { index, note ->
                    val isSelected = selectedStringIndex == index
                    
                    Card(
                        modifier = Modifier
                            .clickable { 
                                onStringSelected(if (isSelected) null else index)
                            }
                            .padding(horizontal = 2.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF00FF41) else Color(0xFF1A1A1A)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 8.dp else 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${index + 1}ª",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) Color.Black else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


