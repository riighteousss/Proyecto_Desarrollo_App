package com.example.uinavegacion.ui.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore para el perfil de usuario (imagen de perfil)
private val Context.profileDataStore by preferencesDataStore(name = "profile_prefs")

/**
 * Datos del perfil de usuario
 * Almacena la información visual del perfil (imagen)
 * Los datos de usuario (nombre, email, phone) se manejan en UserPreferences
 */
data class UserProfile(
    val name: String = "",                    // Nombre completo del usuario
    val email: String = "",                   // Email del usuario
    val phone: String = "",                   // Teléfono del usuario
    val profileImageUri: String? = null,      // URI local de la imagen de perfil
    val profileImageId: Long? = null         // ID de la imagen en el microservicio
)

/**
 * PROFILEVIEWMODEL - MANEJO DEL PERFIL DE USUARIO
 * 
 * Este ViewModel maneja la imagen de perfil del usuario usando DataStore.
 * Los datos de usuario (nombre, email, phone) se sincronizan desde UserPreferences
 * y el microservicio.
 * 
 * NOTA: La contraseña NUNCA se guarda localmente por seguridad.
 */
class ProfileViewModel : ViewModel() {
    
    // Keys para DataStore
    private val profileImageKey = stringPreferencesKey("profile_image_uri")
    private val profileImageIdKey = stringPreferencesKey("profile_image_id")
    
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    /**
     * Carga el perfil del usuario desde DataStore
     * Solo carga la imagen de perfil (los demás datos vienen de UserPreferences)
     */
    fun loadProfile(context: Context) {
        viewModelScope.launch {
            val imageUri = context.profileDataStore.data
                .map { prefs -> prefs[profileImageKey] }
                .first()
            val imageIdStr = context.profileDataStore.data
                .map { prefs -> prefs[profileImageIdKey] }
                .first()
            val imageId = imageIdStr?.toLongOrNull()
            
            _userProfile.value = _userProfile.value.copy(
                profileImageUri = imageUri,
                profileImageId = imageId
            )
        }
    }
    
    /**
     * Actualiza el perfil del usuario
     * Guarda la imagen en DataStore local
     */
    fun updateProfile(name: String, email: String, phone: String, imageUri: String?, context: Context, imageId: Long? = null) {
        viewModelScope.launch {
            // Guardamos la imagen de perfil en DataStore local
            context.profileDataStore.edit { prefs ->
                if (imageUri != null) {
                    prefs[profileImageKey] = imageUri
                } else {
                    prefs.remove(profileImageKey)
                }
                if (imageId != null) {
                    prefs[profileImageIdKey] = imageId.toString()
                } else {
                    prefs.remove(profileImageIdKey)
                }
            }
            
            // Actualizamos el estado local
            _userProfile.value = UserProfile(
                name = name,
                email = email,
                phone = phone,
                profileImageUri = imageUri ?: _userProfile.value.profileImageUri,
                profileImageId = imageId ?: _userProfile.value.profileImageId
            )
        }
    }
    
    /**
     * Limpia la imagen de perfil
     */
    fun clearProfileImage(context: Context) {
        viewModelScope.launch {
            context.profileDataStore.edit { prefs ->
                prefs.remove(profileImageKey)
                prefs.remove(profileImageIdKey)
            }
            _userProfile.value = _userProfile.value.copy(
                profileImageUri = null,
                profileImageId = null
            )
        }
    }
    
    /**
     * Sincroniza el perfil con el servidor (descarga imagen de perfil)
     */
    fun syncProfileWithServer(
        context: Context,
        remoteDataSource: com.example.uinavegacion.data.remote.RemoteDataSource,
        userId: Long
    ) {
        if (userId <= 0) return

        viewModelScope.launch {
            try {
                val result = remoteDataSource.getProfileImagesByUserId(userId)
                result.onSuccess { images ->
                    if (images.isNotEmpty()) {
                        // Tomamos la ultima imagen subida
                        val lastImage = images.last()
                        val imageUrl = lastImage.downloadUrl
                        
                        // Si tenemos URL, actualizamos
                        if (!imageUrl.isNullOrEmpty()) {
                            // Construir URL completa si es relativa
                            val fullUrl = if (imageUrl.startsWith("http")) {
                                imageUrl
                            } else {
                                com.example.uinavegacion.data.remote.RetrofitClient.BASE_URL_IMAGENES.removeSuffix("/") + imageUrl
                            }
                            
                            // Actualizar DataStore
                            context.profileDataStore.edit { prefs ->
                                prefs[profileImageKey] = fullUrl
                                prefs[profileImageIdKey] = lastImage.id.toString()
                            }
                            
                            // Actualizar estado
                            _userProfile.value = _userProfile.value.copy(
                                profileImageUri = fullUrl,
                                profileImageId = lastImage.id
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Carga la imagen de perfil desde el microservicio
     * @param imageId ID de la imagen en el microservicio
     * @param context Contexto de la aplicacion
     */
    suspend fun loadProfileImageFromServer(
        imageId: Long,
        context: Context,
        remoteDataSource: com.example.uinavegacion.data.remote.RemoteDataSource
    ): Result<String?> {
        return try {
            // Obtener la imagen desde el microservicio
            val imageResult = remoteDataSource.getImagesByRequestId(imageId)
            // Nota: Este metodo esta disenado para solicitudes, necesitamos uno para imagenes de perfil
            // Por ahora, retornamos un resultado vacio
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
