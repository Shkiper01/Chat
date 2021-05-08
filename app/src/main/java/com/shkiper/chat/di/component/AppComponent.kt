package com.shkiper.chat.di.component

import dagger.Component
import com.shkiper.chat.di.module.NetworkModule
import com.shkiper.chat.firebase.FireBaseServiceImpl
import com.shkiper.chat.repositories.MainRepository
import com.shkiper.chat.ui.archive.ArchiveActivity
import com.shkiper.chat.ui.chat.ChatActivity
import com.shkiper.chat.ui.main.MainActivity
import com.shkiper.chat.ui.users.UsersActivity
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun getMainRepository(): MainRepository {
        return NetworkModule.providesMainRepository(NetworkModule.providesFireBaseService())
    }

}