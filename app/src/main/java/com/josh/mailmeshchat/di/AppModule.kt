package com.josh.mailmeshchat.di

import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.josh.mailmeshchat.MainViewModel
import com.josh.mailmeshchat.core.data.DefaultMmcRepository
import com.josh.mailmeshchat.core.data.MmcRepository
import com.josh.mailmeshchat.core.database.MailMeshChatDatabase
import com.josh.mailmeshchat.core.database.datasource.LocalMessageDataSource
import com.josh.mailmeshchat.core.database.datasource.RoomLocalMessageDataSource
import com.josh.mailmeshchat.core.mailclient.GmailClient
import com.josh.mailmeshchat.core.mailclient.JavaMailClient
import com.josh.mailmeshchat.core.sharedpreference.EncryptedUserInfoStorage
import com.josh.mailmeshchat.core.sharedpreference.UserInfoStorage
import com.josh.mailmeshchat.core.util.validator.EmailPatternValidator
import com.josh.mailmeshchat.core.util.validator.PatternValidator
import com.josh.mailmeshchat.core.util.validator.UserDataValidator
import com.josh.mailmeshchat.feature.chat.ChatViewModel
import com.josh.mailmeshchat.feature.contact.ContactViewModel
import com.josh.mailmeshchat.feature.group.GroupViewModel
import com.josh.mailmeshchat.feature.login.LoginViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        EncryptedSharedPreferences(
            androidApplication(),
            "auth_prefs",
            MasterKey(androidApplication()),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    single<PatternValidator> {
        EmailPatternValidator
    }

    singleOf(::UserDataValidator)
    singleOf(::EncryptedUserInfoStorage).bind<UserInfoStorage>()
    singleOf(::RoomLocalMessageDataSource).bind<LocalMessageDataSource>()
    singleOf(::GmailClient).bind<JavaMailClient>()
    singleOf(::DefaultMmcRepository).bind<MmcRepository>()

    viewModelOf(::MainViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::GroupViewModel)
    viewModelOf(::ContactViewModel)
    viewModelOf(::ChatViewModel)

    single {
        Room.databaseBuilder(
            androidApplication(),
            MailMeshChatDatabase::class.java,
            "mailmeshchat_db"
        ).build()
    }
    single { get<MailMeshChatDatabase>().messageDao() }
}