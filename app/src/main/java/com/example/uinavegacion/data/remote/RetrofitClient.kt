package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.remote.api.ServiceRequestApiService
import com.example.uinavegacion.data.remote.api.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit configurado para consumir los microservicios
 * 
 * Configuración:
 * - Base URLs para cada microservicio
 * - Interceptor de logging para debug
 * - Timeouts para conexiones
 * - Conversor Gson para JSON
 */
object RetrofitClient {
    
    // URLs base de los microservicios
    private const val BASE_URL_USUARIOS = "http://10.0.2.2:8081/" // Emulador Android
    private const val BASE_URL_SOLICITUDES = "http://10.0.2.2:8082/" // Emulador Android
    private const val BASE_URL_VEHICULOS = "http://10.0.2.2:8085/" // Emulador Android
    
    // Para dispositivo físico, usar la IP de tu PC:
    // private const val BASE_URL_USUARIOS = "http://192.168.1.X:8081/"
    // private const val BASE_URL_SOLICITUDES = "http://192.168.1.X:8082/"
    // private const val BASE_URL_VEHICULOS = "http://192.168.1.X:8085/"
    
    /**
     * Cliente HTTP con logging y timeouts configurados
     */
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Muestra requests y responses completos
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Retrofit para el servicio de usuarios
     */
    private val retrofitUsuarios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_USUARIOS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Retrofit para el servicio de solicitudes
     */
    private val retrofitSolicitudes: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SOLICITUDES)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Retrofit para el servicio de vehículos
     */
    private val retrofitVehiculos: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_VEHICULOS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * API Service para usuarios
     */
    val userApiService: UserApiService by lazy {
        retrofitUsuarios.create(UserApiService::class.java)
    }
    
    /**
     * API Service para solicitudes
     */
    val serviceRequestApiService: ServiceRequestApiService by lazy {
        retrofitSolicitudes.create(ServiceRequestApiService::class.java)
    }
    
    /**
     * API Service para vehículos
     */
    val vehicleApiService: com.example.uinavegacion.data.remote.api.VehicleApiService by lazy {
        retrofitVehiculos.create(com.example.uinavegacion.data.remote.api.VehicleApiService::class.java)
    }
}

