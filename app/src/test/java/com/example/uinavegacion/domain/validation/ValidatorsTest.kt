package com.example.uinavegacion.domain.validation

import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * VALIDATORSTEST - TESTS UNITARIOS PARA VALIDADORES
 * 
 * 🎯 PUNTO CLAVE: Tests para verificar que los validadores funcionan correctamente
 * - Valida formato de email
 * - Valida nombre (solo letras)
 * - Valida teléfono (solo números)
 * - Valida contraseña segura
 * - Valida confirmación de contraseña
 * 
 * 📊 COBERTURA:
 * - Casos exitosos (validación OK)
 * - Casos de error (validación falla)
 * - Casos límite
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidatorsTest {

    // ---------- TESTS DE EMAIL ----------
    
    @Test
    fun validateEmail_ok() {
        val error = validateEmail("personal@gmail.com")
        assertNull("Email válido no debería retornar error", error)
    }

    @Test
    fun validateEmail_error_con_email_vacio() {
        val error = validateEmail("")
        assertEquals("El email es obligatorio", error)
    }

    @Test
    fun validateEmail_error_con_formato_invalido() {
        val error = validateEmail("email-invalido")
        assertEquals("Formato de email inválido", error)
    }

    @Test
    fun validateEmail_error_sin_arroba() {
        val error = validateEmail("emailgmail.com")
        assertEquals("Formato de email inválido", error)
    }

    // ---------- TESTS DE NOMBRE ----------
    
    @Test
    fun validateNameLettersOnly_ok() {
        val error = validateNameLettersOnly("Juan Pérez")
        assertNull("Nombre válido no debería retornar error", error)
    }

    @Test
    fun validateNameLettersOnly_error_con_numeros() {
        val error = validateNameLettersOnly("Juan123")
        assertEquals("Solo letras y espacios", error)
    }

    @Test
    fun validateNameLettersOnly_error_vacio() {
        val error = validateNameLettersOnly("")
        assertEquals("El nombre es obligatorio", error)
    }

    @Test
    fun validateNameLettersOnly_ok_con_tildes() {
        val error = validateNameLettersOnly("José María")
        assertNull("Nombre con tildes debería ser válido", error)
    }

    // ---------- TESTS DE TELÉFONO ----------
    
    @Test
    fun validatePhoneDigitsOnly_ok() {
        val error = validatePhoneDigitsOnly("12345678")
        assertNull("Teléfono válido no debería retornar error", error)
    }

    @Test
    fun validatePhoneDigitsOnly_error_con_letras() {
        val error = validatePhoneDigitsOnly("123abc")
        assertEquals("Solo números", error)
    }

    @Test
    fun validatePhoneDigitsOnly_error_muy_corto() {
        val error = validatePhoneDigitsOnly("123")
        assertEquals("Debe tener entre 8 y 15 dígitos", error)
    }

    @Test
    fun validatePhoneDigitsOnly_error_muy_largo() {
        val error = validatePhoneDigitsOnly("1234567890123456")
        assertEquals("Debe tener entre 8 y 15 dígitos", error)
    }

    @Test
    fun validatePhoneDigitsOnly_error_vacio() {
        val error = validatePhoneDigitsOnly("")
        assertEquals("El teléfono es obligatorio", error)
    }

    // ---------- TESTS DE CONTRASEÑA ----------
    
    @Test
    fun validateStrongPassword_ok() {
        val error = validateStrongPassword("Password123!")
        assertNull("Contraseña válida no debería retornar error", error)
    }

    @Test
    fun validateStrongPassword_error_muy_corta() {
        val error = validateStrongPassword("Pass1!")
        assertEquals("Mínimo 8 caracteres", error)
    }

    @Test
    fun validateStrongPassword_error_sin_mayuscula() {
        val error = validateStrongPassword("password123!")
        assertEquals("Debe incluir una mayúscula", error)
    }

    @Test
    fun validateStrongPassword_error_sin_minuscula() {
        val error = validateStrongPassword("PASSWORD123!")
        assertEquals("Debe incluir una minúscula", error)
    }

    @Test
    fun validateStrongPassword_error_sin_numero() {
        val error = validateStrongPassword("Password!")
        assertEquals("Debe incluir un número", error)
    }

    @Test
    fun validateStrongPassword_error_sin_simbolo() {
        val error = validateStrongPassword("Password123")
        assertEquals("Debe incluir un símbolo", error)
    }

    @Test
    fun validateStrongPassword_error_con_espacios() {
        val error = validateStrongPassword("Password 123!")
        assertEquals("No debe contener espacios", error)
    }

    @Test
    fun validateStrongPassword_error_vacio() {
        val error = validateStrongPassword("")
        assertEquals("La contraseña es obligatoria", error)
    }

    // ---------- TESTS DE CONFIRMACIÓN ----------
    
    @Test
    fun validateConfirm_ok() {
        val error = validateConfirm("Password123!", "Password123!")
        assertNull("Confirmación válida no debería retornar error", error)
    }

    @Test
    fun validateConfirm_error_no_coincide() {
        val error = validateConfirm("Password123!", "Password456!")
        assertEquals("Las contraseñas no coinciden", error)
    }

    @Test
    fun validateConfirm_error_vacio() {
        val error = validateConfirm("Password123!", "")
        assertEquals("Confirma tu contraseña", error)
    }
}

