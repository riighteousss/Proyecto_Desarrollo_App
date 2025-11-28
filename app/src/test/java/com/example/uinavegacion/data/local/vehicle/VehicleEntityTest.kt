package com.example.uinavegacion.data.local.vehicle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * VEHICLEENTITYTEST - TESTS UNITARIOS PARA ENTIDAD DE VEHÍCULO
 * 
 * 🎯 PUNTO CLAVE: Tests para verificar que VehicleEntity funciona correctamente
 * - Verifica creación de vehículos
 * - Verifica valores por defecto
 * - Verifica vehículo predeterminado
 * - Verifica igualdad de objetos
 * 
 * 📊 COBERTURA:
 * - Casos exitosos (creación OK)
 * - Casos con valores por defecto
 * - Casos de igualdad/desigualdad
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VehicleEntityTest {

    // ---------- TESTS DE CREACIÓN ----------
    
    @Test
    fun vehicleEntity_ok_con_todos_los_campos() {
        val vehicle = VehicleEntity(
            id = 1L,
            userId = 100L,
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            plate = "ABC123",
            color = "Blanco",
            isDefault = true,
            createdAt = 1700000000000L
        )
        assertEquals(1L, vehicle.id)
        assertEquals("Toyota", vehicle.brand)
        assertEquals("Corolla", vehicle.model)
        assertEquals(2020, vehicle.year)
    }

    @Test
    fun vehicleEntity_ok_con_valores_por_defecto() {
        val vehicle = VehicleEntity(
            userId = 1L,
            brand = "Honda",
            model = "Civic",
            year = 2021,
            plate = "XYZ789",
            color = "Negro"
        )
        assertEquals(0L, vehicle.id)
        assertFalse(vehicle.isDefault)
    }

    // ---------- TESTS DE VEHÍCULO PREDETERMINADO ----------
    
    @Test
    fun vehicleEntity_ok_es_predeterminado() {
        val vehicle = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", true, 0L)
        assertTrue(vehicle.isDefault)
    }

    @Test
    fun vehicleEntity_ok_no_es_predeterminado() {
        val vehicle = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 0L)
        assertFalse(vehicle.isDefault)
    }

    // ---------- TESTS DE IGUALDAD ----------
    
    @Test
    fun vehicleEntity_ok_igualdad_mismos_datos() {
        val v1 = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 1000L)
        val v2 = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 1000L)
        assertEquals(v1, v2)
    }

    @Test
    fun vehicleEntity_error_desigualdad_diferente_id() {
        val v1 = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 0L)
        val v2 = VehicleEntity(2L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 0L)
        assertNotEquals(v1, v2)
    }

    @Test
    fun vehicleEntity_error_desigualdad_diferente_placa() {
        val v1 = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 0L)
        val v2 = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "XYZ789", "Blanco", false, 0L)
        assertNotEquals(v1, v2)
    }

    // ---------- TESTS DE COPY ----------
    
    @Test
    fun vehicleEntity_ok_copy_cambiar_color() {
        val original = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 0L)
        val modificado = original.copy(color = "Rojo")
        assertEquals("Rojo", modificado.color)
        assertEquals("Blanco", original.color)
    }

    @Test
    fun vehicleEntity_ok_copy_hacer_predeterminado() {
        val original = VehicleEntity(1L, 1L, "Toyota", "Corolla", 2020, "ABC123", "Blanco", false, 0L)
        val modificado = original.copy(isDefault = true)
        assertTrue(modificado.isDefault)
        assertFalse(original.isDefault)
    }

    // ---------- TESTS DE AÑOS ----------
    
    @Test
    fun vehicleEntity_ok_vehiculo_antiguo() {
        val oldCar = VehicleEntity(1L, 1L, "Ford", "Mustang", 1965, "OLD001", "Rojo", false, 0L)
        assertEquals(1965, oldCar.year)
    }

    @Test
    fun vehicleEntity_ok_vehiculo_nuevo() {
        val newCar = VehicleEntity(2L, 1L, "Tesla", "Model 3", 2024, "NEW001", "Blanco", false, 0L)
        assertEquals(2024, newCar.year)
    }
}

