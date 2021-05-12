package com.shkiper.chat.model

import com.shkiper.chat.extensions.humanizeDiff
import com.shkiper.chat.model.data.User
import java.util.*

class ImageMessage(
    id: String = "",
    from: User = User(),
    isIncoming: Boolean = false,
    isRead: Boolean = false,
    group: Boolean = false,
    date: Date = Date(),
    override val type: String = "image",
    var image: String = ""

) : BaseMessage(id, from, isIncoming, isRead, group, date, type){

    override fun formatMessage(): String = "id $id ${from.firstName} " +
            "${if(isIncoming) "получил" else " отправил"} сообщение \"$image\" ${date.humanizeDiff()}"

    companion object Factory {
        private var lastid : Int = -1
        fun makeMessage(imagePath: String, from: User, group: Boolean) : BaseMessage {
            lastid++
            return ImageMessage(
                    id = "$lastid",
                    from = from,
                    image = imagePath,
                    group = group
            )
        }
    }

}