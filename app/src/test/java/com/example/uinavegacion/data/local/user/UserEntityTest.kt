package com.example.uinavegacion.data.local.user

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * USERENTITYTEST - TESTS UNITARIOS PARA ENTIDAD DE USUARIO
 * 
 * 🎯 PUNTO CLAVE: Tests para verificar que UserEntity funciona correctamente
 * - Verifica creación de usuarios
 * - Verifica valores por defecto
 * - Verifica roles
 * - Verifica igualdad de objetos
 * 
 * 📊 COBERTURA:
 * - Casos exitosos (creación OK)
 * - Casos con valores por defecto
 * - Casos de igualdad/desigualdad
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserEntityTest {

    // ---------- TESTS DE CREACIÓN ----------
    
    @Test
    fun userEntity_ok_con_todos_los_campos() {
        val user = UserEntity(
            id = 1L,
            name = "Juan Pérez",
            email = "juan@email.com",
            phone = "12345678",
            password = "password123",
            role = "CLIENT"
        )
        assertEquals(1L, user.id)
        assertEquals("Juan Pérez", user.name)
        assertEquals("juan@email.com", user.email)
    }

    @Test
    fun userEntity_ok_con_rol_por_defecto() {
        val user = UserEntity(
            id = 0L,
            name = "María",
            email = "maria@email.com",
            phone = "87654321",
            password = "pass"
        )
        assertEquals("CLIENT", user.role)
    }

    // ---------- TESTS DE ROLES ----------
    
    @Test
    fun userEntity_ok_rol_client() {
        val user = UserEntity(1L, "Test", "test@test.com", "111", "pass", "CLIENT")
        assertEquals("CLIENT", user.role)
    }

    @Test
    fun userEntity_ok_rol_mechanic() {
        val user = UserEntity(2L, "Mecanico", "mech@test.com", "222", "pass", "MECHANIC")
        assertEquals("MECHANIC", user.role)
    }

    @Test
    fun userEntity_ok_rol_admin() {
        val user = UserEntity(3L, "Admin", "admin@test.com", "333", "pass", "ADMIN")
        assertEquals("ADMIN", user.role)
    }

    // ---------- TESTS DE IGUALDAD ----------
    
    @Test
    fun userEntity_ok_igualdad_mismos_datos() {
        val user1 = UserEntity(1L, "Test", "test@test.com", "123", "pass", "CLIENT")
        val user2 = UserEntity(1L, "Test", "test@test.com", "123", "pass", "CLIENT")
        assertEquals(user1, user2)
    }

    @Test
    fun userEntity_error_desigualdad_diferente_id() {
        val user1 = UserEntity(1L, "Test", "test@test.com", "123", "pass", "CLIENT")
        val user2 = UserEntity(2L, "Test", "test@test.com", "123", "pass", "CLIENT")
        assertNotEquals(user1, user2)
    }

    @Test
    fun userEntity_error_desigualdad_diferente_email() {
        val user1 = UserEntity(1L, "Test", "test1@test.com", "123", "pass", "CLIENT")
        val user2 = UserEntity(1L, "Test", "test2@test.com", "123", "pass", "CLIENT")
        assertNotEquals(user1, user2)
    }

    // ---------- TESTS DE COPY ----------
    
    @Test
    fun userEntity_ok_copy_cambiar_nombre() {
        val original = UserEntity(1L, "Original", "test@test.com", "123", "pass", "CLIENT")
        val modificado = original.copy(name = "Modificado")
        assertEquals("Modificado", modificado.name)
        assertEquals(original.email, modificado.email)
    }

    @Test
    fun userEntity_ok_copy_cambiar_rol() {
        val original = UserEntity(1L, "Test", "test@test.com", "123", "pass", "CLIENT")
        val modificado = original.copy(role = "ADMIN")
        assertEquals("ADMIN", modificado.role)
    }
}

