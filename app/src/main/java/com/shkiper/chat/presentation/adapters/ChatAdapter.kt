package com.shkiper.chat.presentation.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_group.tv_title_group
import kotlinx.android.synthetic.main.item_chat_single.*
import com.shkiper.chat.R
import com.shkiper.chat.presentation.glide.GlideApp
import com.shkiper.chat.domain.entities.data.ChatItem
import com.shkiper.chat.domain.entities.data.ChatType
import com.shkiper.chat.util.StorageUtils
import kotlinx.android.synthetic.main.item_chat_archive.*

class ChatAdapter(private val listener: (ChatItem)->Unit) : RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {
    companion object{
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items: List<ChatItem> = listOf()

    override fun getItemViewType(position: Int): Int = when(items[position].chatType){
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            SINGLE_TYPE -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            else -> ArchiveViewHolder(inflater.inflate(R.layout.item_chat_archive, parent, false))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatAdapter.ChatItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateData(data: List<ChatItem>){

        val diffCallback = object :DiffUtil.Callback(){
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].id == data[newPos].id

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].hashCode() == data[newPos].hashCode()
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    abstract inner class ChatItemViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView), LayoutContainer{
        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleViewHolder(convertView: View) : ChatItemViewHolder(convertView),
        ChatItemTouchHelperCallback.ItemTouchViewHolder {

        override fun bind(item: ChatItem, listener: (ChatItem)->Unit){
            if(item.avatar == null){
                GlideApp.with(itemView)
                    .clear(iv_avatar_single)
                iv_avatar_single.setInitials(item.initials)
            }
            else{
                GlideApp.with(itemView)
                    .load(StorageUtils.pathToReference(item.avatar))
                    .into(iv_avatar_single)
            }

            sv_indicator.visibility = if(item.isOnline) View.VISIBLE else View.GONE

            with(tv_date_single) {
                if (item.lastMessageDate != null){
                    visibility = View.VISIBLE
                    text = item.lastMessageDate
                } else{
                    visibility = View.GONE
                }

            }

            with(tv_counter_single){
                visibility = if(item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_message_single.text = item.shortDescription
            tv_title_single.text = item.title

            itemView.setOnClickListener{
                listener.invoke(item)
            }

        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class GroupViewHolder(convertView: View) : ChatItemViewHolder(convertView),
        ChatItemTouchHelperCallback.ItemTouchViewHolder {

        @SuppressLint("SetTextI18n")
        override fun bind(item: ChatItem, listener: (ChatItem)->Unit){

            iv_avatar_group.setInitials(item.title[0].toString())

            with(tv_date_group) {
                if (item.lastMessageDate != null){
                    visibility = View.VISIBLE
                    text = item.lastMessageDate
                } else{
                    visibility = View.GONE
                }

            }

            with(tv_counter_group){
                visibility = if(item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_title_group.text = item.title
            tv_message_group.text = item.shortDescription
            with(tv_message_author){
                visibility = if(item.messageCount > 0) View.VISIBLE else View.GONE
                text = "${item.author} :"
            }


            itemView.setOnClickListener{
                listener.invoke(item)

            }

        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }


    inner class ArchiveViewHolder(convertView: View) : ChatItemViewHolder(convertView){


        @SuppressLint("SetTextI18n")
        override fun bind(item: ChatItem, listener: (ChatItem)->Unit){

            with(tv_date_archive) {
                if (item.lastMessageDate != null){
                    visibility = View.VISIBLE
                    text = item.lastMessageDate
                } else{
                    visibility = View.GONE
                }

            }

            with(tv_counter_archive){
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            tv_title_archive.text = item.title
            tv_message_archive.text = item.shortDescription

            itemView.setOnClickListener {
                listener.invoke(item)
            }

            with(tv_message_author_archive){
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = "@${item.author}"
            }

        }
        
    }


}