package com.shkiper.chat.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.shkiper.chat.models.data.Chat
import com.shkiper.chat.extensions.mutableLiveData
import com.shkiper.chat.firebase.FireBaseChatsImpl
import com.shkiper.chat.models.TextMessage
import com.shkiper.chat.models.data.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainRepository @Inject constructor(private val fireBaseService: FireBaseChatsImpl) {

    private val chats = mutableLiveData(listOf<Chat>())
    private val users = mutableLiveData(listOf<User>())

    init {
        fireBaseService.getEngagedChats(this::setChats)
    }

    fun loadChats() : MutableLiveData<List<Chat>> {
        return chats
    }

    fun loadUsers(): MutableLiveData<List<User>> {
        return users
    }


    fun createChat(user: User){
        fireBaseService.getOrCreateChat(user)
    }



    fun addUser(user: User){
        val copy = users.value!!.toMutableList()
        copy.add(user)
        users.value = copy
    }


    fun findUser(userId: String): User?{
        users.value!!.forEach {
            if(userId == it.id){
                return it
            }
        }
        return null
    }

    fun findUsersById(ids: List<String>): List<User> {
        return users.value!!.filter { ids.contains(it.id) }
    }




    fun update(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        val ind = chats.value!!.indexOfFirst { it.id == chat.id }
        if(ind == -1) return
        copy[ind] = chat
        chats.value = copy
    }


    private fun setChats(listOfChats: List<Chat>){
        Log.d("ChatsRepository", listOfChats.toString())
        chats.value = listOfChats
    }


    fun find(chatId: String): Chat {
        val ind = chats.value!!.indexOfFirst { it.id == chatId}
        return chats.value!![ind]
    }


}