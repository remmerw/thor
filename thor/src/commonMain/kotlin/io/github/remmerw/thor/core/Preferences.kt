package io.github.remmerw.thor.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val homepageUri = stringPreferencesKey("homepageUri")
private val homepageTitle = stringPreferencesKey("homepageTitle")
private val homepageIcon = byteArrayPreferencesKey("homepageIcon")


suspend fun homepage(
    dataStore: DataStore<Preferences>,
    uri: String,
    title: String,
    icon: ByteArray?
) {
    dataStore.edit { settings ->
        settings[homepageUri] = uri
        settings[homepageTitle] = title
        if (icon != null) {
            settings[homepageIcon] = icon
        } else {
            settings.remove(homepageIcon)
        }
    }
}

suspend fun removeHomepage(dataStore: DataStore<Preferences>) {
    dataStore.edit { settings ->
        settings.remove(homepageUri)
        settings.remove(homepageIcon)
        settings.remove(homepageTitle)
    }
}

fun homepageUri(dataStore: DataStore<Preferences>, default: String): Flow<String> {
    return dataStore.data.map { settings ->
        settings[homepageUri] ?: default
    }
}
