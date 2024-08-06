package com.josh.mailmeshchat

import android.app.Application
import com.josh.mailmeshchat.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MailMeshChatApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MailMeshChatApp)
            modules(appModule)
        }
    }
}
