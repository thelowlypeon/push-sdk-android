package com.android.exampleapp.kotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.exampleapp.kotlin.MainActivity
import com.android.exampleapp.kotlin.R
import com.squareup.picasso.Picasso
import com.vibes.vibes.InboxMessage
import java.util.*


class InboxDetailFragment : androidx.fragment.app.Fragment() {
    private var inboxMessage: InboxMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val bundle = this.arguments
        if (bundle != null) {
            inboxMessage = bundle.getSerializable(MainActivity.INBOX_MESSAGE_KEY) as InboxMessage
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_inbox_detail, container, false)
        val subject = view.findViewById<TextView>(R.id.subject)
        val expirationDate = view.findViewById<TextView>(R.id.expires_at)
        val detailImage = view.findViewById<ImageView>(R.id.imageIcon)
        val content = view.findViewById<TextView>(R.id.content)

        subject.text = inboxMessage!!.subject
        val dateFormat = DateFormat.getDateFormat(context)
        expirationDate.text = dateFormat.format(inboxMessage!!.expirationDate)
        Picasso.get().load(inboxMessage!!.detail).into(detailImage)
        content.text = inboxMessage!!.content

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val toolbarTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)

        (Objects.requireNonNull(activity) as AppCompatActivity).setSupportActionBar(toolbar)
        Objects.requireNonNull((activity as AppCompatActivity?)!!.supportActionBar)!!.setDisplayShowTitleEnabled(false)

        toolbarTitle.text = "Inbox Detail"

        Objects.requireNonNull((activity as AppCompatActivity?)!!.supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull((activity as AppCompatActivity?)!!.supportActionBar)!!.setDisplayShowHomeEnabled(true)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Objects.requireNonNull(activity)!!.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
