package com.example.uinavegacion.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.local.service.ServiceRequest
import com.example.uinavegacion.data.local.service.ServiceRequestDao
import com.example.uinavegacion.data.local.mechanic.MechanicEntity
import com.example.uinavegacion.data.local.mechanic.MechanicDao
import com.example.uinavegacion.data.local.vehicle.VehicleEntity
import com.example.uinavegacion.data.local.vehicle.VehicleDao
import com.example.uinavegacion.data.local.address.AddressEntity
import com.example.uinavegacion.data.local.address.AddressDao
import com.example.uinavegacion.data.local.request.RequestHistoryEntity
import com.example.uinavegacion.data.local.request.RequestHistoryDao
import com.example.uinavegacion.data.local.image.ImageEntity
import com.example.uinavegacion.data.local.image.ImageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * APPDATABASE - BASE DE DATOS PRINCIPAL
 * 
 * Esta es la base de datos principal de la aplicación usando Room.
 * Contiene todas las entidades y DAOs necesarios para el funcionamiento de la app.
 * 
 * Entidades incluidas:
 * - UserEntity: Usuarios de la aplicación
 * - ServiceRequest: Solicitudes de servicio
 * - MechanicEntity: Mecánicos disponibles
 */
@Database(
    entities = [UserEntity::class, ServiceRequest::class, MechanicEntity::class, VehicleEntity::class, AddressEntity::class, RequestHistoryEntity::class, ImageEntity::class],
    version = 5, // Incrementado por agregar tabla de imágenes con BLOB
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase(){
    // DAOs para acceder a los datos de cada entidad
    abstract fun userDao(): UserDao                    // Acceso a datos de usuarios
    abstract fun serviceRequestDao(): ServiceRequestDao // Acceso a solicitudes de servicio
    abstract fun mechanicDao(): MechanicDao            // Acceso a datos de mecánicos
    abstract fun vehicleDao(): VehicleDao             // Acceso a datos de vehículos
    abstract fun addressDao(): AddressDao             // Acceso a datos de direcciones
    abstract fun requestHistoryDao(): RequestHistoryDao // Acceso a historial de solicitudes
    abstract fun imageDao(): ImageDao                 // Acceso a imágenes almacenadas como BLOB

    companion object {
        // Patrón Singleton para la base de datos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //variable para el nombre del archivo para la BD
        private const val DB_NAME = "ui_navegacion.db"

        //creamos la instancia unica de la BD
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                //ejecute cuando la BD se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //lanzamos una corrutina para insertar los datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                // Evitamos llamar recursivamente a getInstance desde aquí. En su lugar abrimos una BD temporal.
                                val database = Room.databaseBuilder(
                                    context.applicationContext,
                                    AppDatabase::class.java,
                                    DB_NAME
                                ).build()
                                val userDao = database.userDao()
                                val mechanicDao = database.mechanicDao()
                                val vehicleDao = database.vehicleDao()
                                val addressDao = database.addressDao()
                                
                                // Precargamos usuarios de prueba con contraseñas que cumplen validaciones
                                // Requisitos: mín. 8 chars, mayúscula, minúscula, número, símbolo
                                val userSeed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "a@a.cl",
                                        phone = "12345678",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Jose",
                                        email = "b@b.cl",
                                        phone = "12345678",
                                        password = "Jose123!"
                                    ),
                                    UserEntity(
                                        name = "Usuario Prueba",
                                        email = "test@test.com",
                                        phone = "912345678",
                                        password = "Test123!"
                                    )
                                )
                                
                                //precargamos mecánicos ficticios
                                val mechanicSeed = listOf(
                                    MechanicEntity(
                                        id = "1",
                                        name = "Carlos Mendoza",
                                        specialty = "Motor y transmisión",
                                        rating = 4.8f,
                                        distance = "2.3 km",
                                        phone = "+56 9 1234 5678",
                                        isAvailable = true,
                                        address = "Av. Principal 123, Santiago",
                                        experience = 8,
                                        pricePerHour = 25000
                                    ),
                                    MechanicEntity(
                                        id = "2",
                                        name = "Ana Rodríguez",
                                        specialty = "Frenos y suspensión",
                                        rating = 4.9f,
                                        distance = "1.8 km",
                                        phone = "+56 9 8765 4321",
                                        isAvailable = false,
                                        address = "Calle Secundaria 456, Providencia",
                                        experience = 12,
                                        pricePerHour = 30000
                                    ),
                                    MechanicEntity(
                                        id = "3",
                                        name = "Miguel Torres",
                                        specialty = "Electricidad automotriz",
                                        rating = 4.7f,
                                        distance = "3.1 km",
                                        phone = "+56 9 5555 1234",
                                        isAvailable = true,
                                        address = "Boulevard Norte 789, Las Condes",
                                        experience = 6,
                                        pricePerHour = 22000
                                    ),
                                    MechanicEntity(
                                        id = "4",
                                        name = "Patricia Silva",
                                        specialty = "Aire acondicionado",
                                        rating = 4.6f,
                                        distance = "4.2 km",
                                        phone = "+56 9 7777 8888",
                                        isAvailable = true,
                                        address = "Plaza Mayor 321, Ñuñoa",
                                        experience = 5,
                                        pricePerHour = 20000
                                    ),
                                    MechanicEntity(
                                        id = "5",
                                        name = "Roberto González",
                                        specialty = "Carrocería y pintura",
                                        rating = 4.5f,
                                        distance = "5.1 km",
                                        phone = "+56 9 9999 1111",
                                        isAvailable = false,
                                        address = "Avenida Costanera 654, Vitacura",
                                        experience = 15,
                                        pricePerHour = 35000
                                    )
                                )
                                
                                //insertar solo si las tablas están vacías
                                if(userDao.count() == 0){
                                    userSeed.forEach { userDao.insert(it) }
                                }
                                
                                if(mechanicDao.count() == 0){
                                    mechanicDao.insertAll(mechanicSeed)
                                }
                                
                                // Precargar vehículos de ejemplo para usuarios
                                val vehicleSeed = listOf(
                                    VehicleEntity(
                                        userId = 1L, // Admin
                                        brand = "Toyota",
                                        model = "Corolla",
                                        year = 2020,
                                        plate = "ABC123",
                                        color = "Blanco",
                                        isDefault = true
                                    ),
                                    VehicleEntity(
                                        userId = 2L, // Jose
                                        brand = "Honda",
                                        model = "Civic",
                                        year = 2019,
                                        plate = "DEF456",
                                        color = "Negro",
                                        isDefault = true
                                    )
                                )
                                
                                if(vehicleDao.countByUser(1L) == 0){
                                    vehicleSeed.forEach { vehicleDao.insert(it) }
                                }
                                
                                // Precargar direcciones de ejemplo
                                val addressSeed = listOf(
                                    AddressEntity(
                                        userId = 1L, // Admin
                                        name = "Casa",
                                        address = "Av. Principal 123",
                                        city = "Santiago",
                                        region = "Metropolitana",
                                        postalCode = "7500000",
                                        isDefault = true
                                    ),
                                    AddressEntity(
                                        userId = 2L, // Jose
                                        name = "Trabajo",
                                        address = "Calle Secundaria 456",
                                        city = "Providencia",
                                        region = "Metropolitana",
                                        postalCode = "7500000",
                                        isDefault = true
                                    )
                                )
                                
                                if(addressDao.countByUser(1L) == 0){
                                    addressSeed.forEach { addressDao.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}