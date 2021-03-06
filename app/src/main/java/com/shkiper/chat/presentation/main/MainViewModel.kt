package com.shkiper.chat.presentation.main

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shkiper.chat.extensions.mutableLiveData
import com.shkiper.chat.extensions.shortFormat
import com.shkiper.chat.domain.entities.data.Chat
import com.shkiper.chat.domain.entities.data.ChatItem
import com.shkiper.chat.domain.entities.data.ChatType
import com.shkiper.chat.data.repository.RepositoryImpl
import com.shkiper.chat.domain.interactors.ChatsInteractor
import com.shkiper.chat.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val chatsInteractor: ChatsInteractor
    ): ViewModel() {

    private val query = mutableLiveData("")
    private val chats by lazy { MutableLiveData<Resource<List<ChatItem>>>() }

    private val disposable: CompositeDisposable = CompositeDisposable()



    init {
        fetchChats()
    }


    private fun fetchChats(){
        chats.postValue(Resource.loading(null))
        disposable.add(
            chatsInteractor.getChats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ data ->
                    val archived = data.filter { it.archived }
                    if (archived.isEmpty()) {
                        chats.postValue(Resource.success(data.map { chat ->  chat.toChatItem() }))
                    } else {
                         val listWithArchive = mutableListOf<ChatItem>()
                        listWithArchive.add(0, makeArchiveItem(archived))
                        listWithArchive.addAll((data.filter { !it.archived }.map { chat -> chat .toChatItem() }))
                        chats.postValue(Resource.success(listWithArchive))
            } },{
                    chats.postValue(Resource.error(it.printStackTrace().toString(),null))
                })
        )
    }

    fun getChatData() : MutableLiveData<Resource<List<ChatItem>>> {
        val result = MediatorLiveData<Resource<List<ChatItem>>>()

        val filterF = {
            val queryStr = query.value!!
            val chats = if (chats.value == null) Resource.loading(null) else chats.value

            result.value = if(queryStr.isEmpty()) chats as Resource<List<ChatItem>>?
            else Resource.success(chats?.data?.filter { it.title.contains(queryStr,true) })
        }

        result.addSource(chats){filterF.invoke()}
        result.addSource(query){filterF.invoke()}

        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatsInteractor.findChatById(chatId)
        chatsInteractor.updateChat(chat.copy(archived = true))
    }

    fun restoreFromArchive(chatId: String){
        val chat = chatsInteractor.findChatById(chatId)
        chatsInteractor.updateChat(chat.copy(archived = false))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }


    private fun makeArchiveItem(archived: List<Chat>): ChatItem {
        val count = archived.fold(0) { acc, chat -> acc + chat.unreadMessageCount() }

        val lastChat: Chat =
                if (archived.none { it.unreadMessageCount() != 0 }) archived.last() else
                    archived.filter { it.unreadMessageCount() != 0 }
                        .maxByOrNull { it.lastMessageDate()!! }!!

        return ChatItem(
                "-1",
                null,
                "",
                "Архив чатов",
                lastChat.lastMessageShort().first,
                count,
                lastChat.lastMessageDate()?.shortFormat(),
                false,
                ChatType.ARCHIVE,
                lastChat.lastMessageShort().second
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}