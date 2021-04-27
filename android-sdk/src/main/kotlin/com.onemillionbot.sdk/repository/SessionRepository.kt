package com.onemillionbot.sdk.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.onemillionbot.sdk.entities.ProviderLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionRepository(context: Context) {
    private val dataStore: DataStore<Preferences> by lazy {
        context.createDataStore(DATA_STORE_NAME)
    }

    private val legalTermsKey by lazy { booleanPreferencesKey(LEGAL_TERMS_KEY) }
    private val languageKey by lazy { stringPreferencesKey(LANGUAGE_KEY) }
    private val userIdKey by lazy { stringPreferencesKey(USER_ID_KEY) }
    private val companyIdKey by lazy { stringPreferencesKey(COMPANY_ID_KEY) }
    private val userNameKey by lazy { stringPreferencesKey(USER_NAME_KEY) }

    private val chatIdKey by lazy { stringPreferencesKey(CHAT_ID_KEY) }

    suspend fun acceptLegalTerms() {
        dataStore.edit { settings -> settings[legalTermsKey] = true }
    }

    suspend fun hasUserAcceptedLegalTerms(): Boolean {
        return dataStore.data.map { it[legalTermsKey] ?: false }.first()
    }

    suspend fun selectLanguage(providerLanguage: ProviderLanguage) {
        dataStore.edit { settings -> settings[languageKey] = providerLanguage.name }
    }

    suspend fun hasUserSelectedLanguage(): Boolean {
        return dataStore.data.map { it[languageKey]?.isNotEmpty() == true }.first()
    }

    suspend fun getSelectedLanguage(): ProviderLanguage {
        return dataStore.data.map {
            ProviderLanguage.valueOf(it[languageKey] ?: ProviderLanguage.SPAIN.name)
        }.first()
    }

    suspend fun getUserId(): String? {
        return dataStore.data
            .map { it[userIdKey] }
            .first()
            .let { userId -> if (userId?.isEmpty() == true) null else userId }
    }

    suspend fun setUserId(userId: String) {
        dataStore.edit { settings -> settings[userIdKey] = userId }
    }

    suspend fun setCompanyId(idCompany: String) {
        dataStore.edit { settings -> settings[companyIdKey] = idCompany }
    }

    suspend fun getCompanyId(): String {
        return dataStore.data
            .map { it[companyIdKey] }
            .first()!!
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { settings -> settings[userNameKey] = name }
    }

    suspend fun getUserName(): String {
        return dataStore.data
            .map { it[userNameKey] }
            .first()!!
    }

    suspend fun getChatId(): String? {
        return dataStore.data
            .map { it[chatIdKey] }
            .first()
            .let { chatId -> if (chatId?.isEmpty() == true) null else chatId }
    }

    suspend fun setChatId(chatId: String) {
        dataStore.edit { settings -> settings[chatIdKey] = chatId }
    }

    suspend fun closeSession() {
        dataStore.edit { settings ->
            settings[legalTermsKey] = false
            settings[userIdKey] = ""
            settings[chatIdKey] = ""
            settings[companyIdKey] = ""
            settings[userNameKey] = ""
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "oneMillionBot"
        private const val LEGAL_TERMS_KEY = "legalTerms"
        private const val LANGUAGE_KEY = "language"
        private const val USER_ID_KEY = "userId"
        private const val CHAT_ID_KEY = "chatId"
        private const val COMPANY_ID_KEY = "companyIdKey"
        private const val USER_NAME_KEY = "userNameKey"

    }
}
