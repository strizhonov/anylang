package com.strizhonovapps.anylangapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.types.LanguageType
import com.strizhonovapps.anylangapp.viewsupport.PagerAdapter

private const val FROM_DIALOG_REQ_CODE = 99

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_main)
        super.setSupportActionBar(findViewById(R.id.toolbar))

        val tabLayout = getConfiguredTabLayout()
        configureViewPager(tabLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_native_item -> {
                val intent = Intent(this, SelectLanguageDialog::class.java)
                intent.putExtra(getString(R.string.extra_message_key), LanguageType.NATIVE_LANGUAGE.toString())
                startActivityForResult(intent, FROM_DIALOG_REQ_CODE)
            }
            R.id.change_study_item -> {
                intent = Intent(this, SelectLanguageDialog::class.java)
                intent.putExtra(getString(R.string.extra_message_key), LanguageType.STUDY_LANGUAGE.toString())
                startActivityForResult(intent, FROM_DIALOG_REQ_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FROM_DIALOG_REQ_CODE -> onResume()
            else -> onResume()
        }
    }

    private fun configureViewPager(tabLayout: TabLayout) {
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun getConfiguredTabLayout(): TabLayout {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.first_tab_name_content)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.second_tab_name_content)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.third_tab_name_content)))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        return tabLayout
    }

}