package com.guitartuner.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guitartuner.app.data.TuningStatus
import kotlin.math.abs

/**
 * Custom composable that displays a color-coded tuning bar with smooth animations
 */
@Composable
fun TuningBar(
    cents: Double,
    tuningStatus: TuningStatus,
    modifier: Modifier = Modifier
) {
    // Colors for tuning status
    val greenColor = Color(0xFF00FF41) // Fluorescent green
    val yellowColor = Color(0xFFFFD700) // Yellow
    val redColor = Color(0xFFFF3131) // Red
    val backgroundColor = Color(0xFF2A2A2A) // Dark background
    
    // Calculate fill percentage based on cents deviation
    // Map cents to 0.0-1.0 range, with 0.5 being in tune
    val maxCents = 50.0 // Maximum cents deviation to show
    val normalizedCents = (cents / maxCents).coerceIn(-1.0, 1.0)
    val fillPercentage = ((normalizedCents + 1.0) / 2.0).toFloat()
    
    // Animate the fill percentage
    val animatedFill by animateFloatAsState(
        targetValue = fillPercentage,
        animationSpec = tween(durationMillis = 100),
        label = "fillAnimation"
    )
    
    // Determine color based on tuning status
    val barColor = when (tuningStatus) {
        TuningStatus.IN_TUNE -> greenColor
        TuningStatus.TOO_LOW -> yellowColor
        TuningStatus.TOO_HIGH -> redColor
        TuningStatus.DETECTING -> Color.Gray
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The tuning bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 24.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val cornerRadius = 12.dp.toPx()
                
                // Draw background
                drawRoundRect(
                    color = backgroundColor,
                    size = Size(canvasWidth, canvasHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
                
                // Draw the filled portion
                val fillWidth = canvasWidth * animatedFill
                if (fillWidth > 0) {
                    drawRoundRect(
                        color = barColor.copy(alpha = 0.8f),
                        size = Size(fillWidth, canvasHeight),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                }
                
                // Draw center line indicator
                val centerX = canvasWidth / 2
                drawLine(
                    color = Color.White,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, canvasHeight),
                    strokeWidth = 3.dp.toPx()
                )
                
                // Draw the position indicator (triangle)
                if (tuningStatus != TuningStatus.DETECTING) {
                    val indicatorX = canvasWidth * animatedFill
                    val indicatorSize = 16.dp.toPx()
                    val indicatorY = canvasHeight / 2
                    
                    // Draw a circle indicator
                    drawCircle(
                        color = barColor,
                        radius = indicatorSize / 2,
                        center = Offset(indicatorX, indicatorY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = indicatorSize / 3,
                        center = Offset(indicatorX, indicatorY)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Loosen ←",
                style = MaterialTheme.typography.bodyMedium,
                color = yellowColor,
                fontSize = 14.sp
            )
            
            Text(
                text = "Tuned",
                style = MaterialTheme.typography.bodyMedium,
                color = greenColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "→ Tighten",
                style = MaterialTheme.typography.bodyMedium,
                color = redColor,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Cents display
        if (tuningStatus != TuningStatus.DETECTING) {
            Text(
                text = "${if (cents > 0) "+" else ""}${String.format("%.1f", cents)} cents",
                style = MaterialTheme.typography.bodyLarge,
                color = barColor,
                fontSize = 18.sp
            )
        }
    }
}


