package com.example.uinavegacion.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ImagenesAnimadas(imagenes: List<Int>) {
    val animOffsets = remember {
        imagenes.mapIndexed { index, _ ->
            val startX = if (index % 2 == 0) -800f else 800f // unas desde izquierda, otras desde derecha
            Animatable(startX)
        }
    }

    // Lanzamos la animación
    LaunchedEffect(Unit) {
        animOffsets.forEachIndexed { i, anim ->
            delay(i * 300L) // entra una tras otra
            anim.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 900,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        imagenes.forEachIndexed { i, img ->
            Image(
                painter = painterResource(id = img),
                contentDescription = "Imagen animada $i",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset { IntOffset(animOffsets[i].value.toInt(), 0) }
                    .size(200.dp)
                    .padding(vertical = 8.dp)
            )
        }
    }
}