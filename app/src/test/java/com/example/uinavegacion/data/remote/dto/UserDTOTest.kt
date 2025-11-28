package com.example.uinavegacion.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * USERDTOTEST - TESTS UNITARIOS PARA DTOs DE USUARIO
 * 
 * 🎯 PUNTO CLAVE: Tests para verificar que los DTOs funcionan correctamente
 * - Verifica UserDTO (respuesta del servidor)
 * - Verifica UserRequestDTO (envío al servidor)
 * - Verifica LoginRequestDTO (login)
 * 
 * 📊 COBERTURA:
 * - Casos exitosos (creación OK)
 * - Casos con valores nulos
 * - Casos de igualdad/desigualdad
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserDTOTest {

    // ---------- TESTS DE USERDTO ----------
    
    @Test
    fun userDTO_ok_con_todos_los_campos() {
        val dto = UserDTO(
            id = 1L,
            email = "test@email.com",
            name = "Test User",
            phone = "12345678",
            role = "CLIENT"
        )
        assertEquals(1L, dto.id)
        assertEquals("test@email.com", dto.email)
        assertEquals("Test User", dto.name)
    }

    @Test
    fun userDTO_ok_con_campos_nulos() {
        val dto = UserDTO(
            id = 1L,
            email = null,
            name = null,
            phone = null,
            role = null
        )
        assertEquals(1L, dto.id)
        assertNull(dto.email)
        assertNull(dto.name)
    }

    @Test
    fun userDTO_ok_igualdad() {
        val dto1 = UserDTO(1L, "test@email.com", "Test", "123", "CLIENT")
        val dto2 = UserDTO(1L, "test@email.com", "Test", "123", "CLIENT")
        assertEquals(dto1, dto2)
    }

    @Test
    fun userDTO_error_desigualdad() {
        val dto1 = UserDTO(1L, "test1@email.com", "Test1", "123", "CLIENT")
        val dto2 = UserDTO(2L, "test2@email.com", "Test2", "456", "MECHANIC")
        assertNotEquals(dto1, dto2)
    }

    // ---------- TESTS DE USERREQUESTDTO ----------
    
    @Test
    fun userRequestDTO_ok_con_todos_los_campos() {
        val dto = UserRequestDTO(
            email = "new@email.com",
            password = "password123",
            name = "New User",
            phone = "87654321",
            role = "MECHANIC"
        )
        assertEquals("new@email.com", dto.email)
        assertEquals("password123", dto.password)
        assertEquals("MECHANIC", dto.role)
    }

    @Test
    fun userRequestDTO_ok_con_rol_por_defecto() {
        val dto = UserRequestDTO(
            email = "test@email.com",
            password = "pass",
            name = "Test",
            phone = "123"
        )
        assertEquals("CLIENT", dto.role)
    }

    @Test
    fun userRequestDTO_ok_sin_password_para_update() {
        val dto = UserRequestDTO(
            email = "test@email.com",
            password = null,
            name = "Test",
            phone = "123",
            role = "CLIENT"
        )
        assertNull(dto.password)
    }

    @Test
    fun userRequestDTO_ok_igualdad() {
        val dto1 = UserRequestDTO("test@email.com", "pass", "Test", "123", "CLIENT")
        val dto2 = UserRequestDTO("test@email.com", "pass", "Test", "123", "CLIENT")
        assertEquals(dto1, dto2)
    }

    // ---------- TESTS DE LOGINREQUESTDTO ----------
    
    @Test
    fun loginRequestDTO_ok_creacion() {
        val dto = LoginRequestDTO(
            email = "user@email.com",
            password = "securePassword"
        )
        assertEquals("user@email.com", dto.email)
        assertEquals("securePassword", dto.password)
    }

    @Test
    fun loginRequestDTO_ok_igualdad() {
        val dto1 = LoginRequestDTO("test@email.com", "pass123")
        val dto2 = LoginRequestDTO("test@email.com", "pass123")
        assertEquals(dto1, dto2)
    }

    @Test
    fun loginRequestDTO_error_desigualdad_diferente_password() {
        val dto1 = LoginRequestDTO("test@email.com", "pass123")
        val dto2 = LoginRequestDTO("test@email.com", "differentPass")
        assertNotEquals(dto1, dto2)
    }
}

