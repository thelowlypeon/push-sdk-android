package com.android.exampleapp.kotlin

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.android.exampleapp.kotlin.fragments.InboxMessagesFragment
import com.android.exampleapp.kotlin.fragments.VibesMainFragment

class MainActivity : AppCompatActivity() {

    companion object {
        val INBOX_MESSAGE_KEY = "message"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        loadFragment(VibesMainFragment())
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.action_home -> {
                    item.isEnabled = true
                    fragment = VibesMainFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_inbox_messages -> {
                    item.isEnabled = true
                    fragment = InboxMessagesFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(backStateName)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
