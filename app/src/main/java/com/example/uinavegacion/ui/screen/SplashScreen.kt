package com.example.uinavegacion.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de Splash Screen simple para Fixsy
 * 
 * Muestra el logo y nombre de la aplicación con animación
 * antes de navegar a la pantalla principal
 */
@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit
) {
    // Estados de animación
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.3f) }
    
    // Color naranja de Fixsy
    val primaryOrange = Color(0xFFFF6B35)
    
    LaunchedEffect(key1 = true) {
        // Animación de fade in y scale simultánea
        kotlinx.coroutines.coroutineScope {
            launch {
                alphaAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            
            launch {
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
        
        // Esperar antes de navegar
        delay(2000)
        
        // Navegar a la pantalla principal
        onNavigateToMain()
    }
    
    // UI del Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryOrange),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(id = com.example.uinavegacion.R.mipmap.ic_launcher),
                contentDescription = "Fixsy Logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

