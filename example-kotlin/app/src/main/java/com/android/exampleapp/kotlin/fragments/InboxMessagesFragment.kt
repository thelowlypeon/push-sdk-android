package com.android.exampleapp.kotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.android.exampleapp.kotlin.MainActivity
import com.android.exampleapp.kotlin.R
import com.android.exampleapp.kotlin.adapters.MessagesAdapter
import com.vibes.vibes.InboxMessage
import com.vibes.vibes.Vibes
import com.vibes.vibes.VibesListener
import java.util.*


class InboxMessagesFragment : androidx.fragment.app.Fragment() {
    private var adapter: MessagesAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_inbox_messages, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (Objects.requireNonNull(activity) as AppCompatActivity).setSupportActionBar(toolbar)
        Objects.requireNonNull((activity as AppCompatActivity?)!!.supportActionBar)!!.setDisplayShowTitleEnabled(false)

        progressBar = view.findViewById(R.id.progress_circular)

        recyclerView = view.findViewById(R.id.inbox_recycler_view)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        fetchMessages()
        return view
    }

    private fun fetchMessages() {
        progressBar!!.visibility = View.VISIBLE
        Vibes.getInstance().fetchInboxMessages(object : VibesListener<Collection<InboxMessage?>> {
            override fun onSuccess(value: Collection<InboxMessage?>) {
                adapter = MessagesAdapter(context, ArrayList(value))
                adapter!!.onItemClick = { message ->
                    message?.let { loadFragment(it) }
                }
                recyclerView!!.adapter = adapter
                progressBar!!.visibility = View.GONE
            }

            override fun onFailure(errorText: String) {
                progressBar!!.visibility = View.GONE
                Log.d("InboxMessagesFragment", "Error fetching Message:  $errorText")
            }
        })
    }

    private fun loadFragment(message: InboxMessage) {
        val fragment: androidx.fragment.app.Fragment = InboxDetailFragment()
        val fragmentManager = Objects.requireNonNull(activity)!!.supportFragmentManager
        if (message.detail != null) {
            val bundle = Bundle()
            bundle.putSerializable(MainActivity.INBOX_MESSAGE_KEY, message)
            fragment.arguments = bundle
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
