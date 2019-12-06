package com.android.exampleapp.kotlin.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.exampleapp.kotlin.R
import com.squareup.picasso.Picasso
import com.vibes.vibes.InboxMessage

class MessagesAdapter(context: Context?, messages: List<InboxMessage?>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    private var messages: List<InboxMessage?>? = null
    private var layoutInflater: LayoutInflater? = null
    private var context: Context? = null
    var onItemClick: ((InboxMessage?) -> Unit)? = null

    init {
        this.messages = messages
        this.context = context
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = layoutInflater!!.inflate(R.layout.inbox_messages_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inboxMessage = messages!![position]
        holder.subject.text = inboxMessage!!.subject
        holder.content.text = inboxMessage.content
        Picasso.get().load(inboxMessage.iconImage).into(holder.iconImage)
        val dateFormat = DateFormat.getDateFormat(context)
        holder.expiresAt.text = dateFormat.format(inboxMessage.expirationDate)
    }

    override fun getItemCount(): Int {
        return messages!!.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var subject: TextView = view.findViewById(R.id.subject)
        var content: TextView = view.findViewById(R.id.content)
        var expiresAt: TextView = view.findViewById(R.id.expires_at)
        var iconImage: ImageView = view.findViewById(R.id.imageIconView)

        init {
            view.setOnClickListener { onItemClick?.invoke(messages?.get(adapterPosition)) }
        }
    }
}
