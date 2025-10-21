package com.example.uinavegacion.ui.onboarding

import androidx.annotation.DrawableRes

data class VentanaOnboarding(
    val titulo: String,
    val descripcion: String,
    @DrawableRes val imagenes: List<Int>
)
