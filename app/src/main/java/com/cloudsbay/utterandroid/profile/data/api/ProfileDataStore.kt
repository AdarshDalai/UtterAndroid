package com.cloudsbay.utterandroid.profile.data.api

import android.content.Context
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProfileDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "user_profiles")
    private val gson = Gson()

    /**
     * Saves a user's profile data to DataStore using their ID as a key.
     */
    suspend fun saveProfileData(profile: ProfileResponse.UserProfile) {
        val profileKey = stringPreferencesKey(profile.id) // Unique key for the user's profile
        val profileJson = gson.toJson(profile) // Convert UserProfile to JSON string

        context.dataStore.edit { preferences ->
            preferences[profileKey] = profileJson
        }
    }

    /**
     * Retrieves a user's profile data from DataStore using their ID.
     */
    suspend fun getProfileData(userId: String): ProfileResponse.UserProfile? {
        val profileKey = stringPreferencesKey(userId) // Use the user ID as the key
        val preferences = context.dataStore.data.first() // Get the preferences snapshot
        val profileJson = preferences[profileKey] // Get the JSON string for the user

        return profileJson?.let { gson.fromJson(it, ProfileResponse.UserProfile::class.java) }
    }

    /**
     * Deletes a user's profile data from DataStore.
     */
    suspend fun deleteProfileData(userId: String) {
        val profileKey = stringPreferencesKey(userId) // Use the user ID as the key
        context.dataStore.edit { preferences ->
            preferences.remove(profileKey) // Remove the entry for the user
        }
    }

    /**
     * Retrieves all stored user profiles.
     */
    suspend fun getAllProfiles(): List<ProfileResponse.UserProfile> {
        val preferences = context.dataStore.data.first()
        return preferences.asMap().mapNotNull { (key, value) ->
            if (key is Key<*> && value is String) {
                gson.fromJson(value, ProfileResponse.UserProfile::class.java)
            } else null
        }
    }
}