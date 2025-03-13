package com.cloudsbay.utterandroid.post.data.api

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PostDataStore @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "upload_media")



}