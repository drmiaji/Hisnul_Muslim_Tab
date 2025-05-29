package com.drmiaji.hisnulmuslim.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drmiaji.hisnulmuslim.R
import com.drmiaji.hisnulmuslim.activity.About
import com.drmiaji.hisnulmuslim.activity.BaseActivity
import com.drmiaji.hisnulmuslim.activity.SettingsActivity
import com.drmiaji.hisnulmuslim.adapter.DuaAdapter
import com.drmiaji.hisnulmuslim.data.database.HisnulMuslimDatabase
import com.drmiaji.hisnulmuslim.data.entities.DuaName
import com.drmiaji.hisnulmuslim.data.repository.HisnulMuslimRepository
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ChapterListActivity : BaseActivity() {
    private lateinit var adapter: DuaAdapter
    private lateinit var repository: HisnulMuslimRepository
    private var allDuaNames: List<DuaName> = emptyList()

    override fun getLayoutResource() = R.layout.activity_chapter_list

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupToolbar()
        setupDatabase()
        setupRecyclerView()
        loadDuaNames()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val titleTextView = findViewById<TextView>(R.id.toolbar_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        titleTextView.text = getString(R.string.app_name)
        val typeface = Typeface.createFromAsset(assets, "fonts/solaimanlipi.ttf")
        titleTextView.typeface = typeface

        val iconColor = ContextCompat.getColor(this, R.color.toolbar_icon_color)
        toolbar.navigationIcon?.let { drawable ->
            val wrapped = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrapped, iconColor)
            toolbar.navigationIcon = wrapped
        }
    }

    private fun setupDatabase() {
        val database = HisnulMuslimDatabase.getDatabase(this)
        repository = HisnulMuslimRepository(
            database.categoryDao(),
            database.duaNameDao(),
            database.duaDetailDao()
        )
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.chapter_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DuaAdapter(emptyList()) { duaName ->
            // Launch coroutine to get the first DuaDetail of this chapter
            lifecycleScope.launch {
                // Use .firstOrNull() from Flow for a single result; avoid nested .firstOrNull()?.firstOrNull()
                val duaDetails = repository.getDuaDetailsByGlobalId(duaName.chap_id).firstOrNull()
                val firstDetail = duaDetails?.firstOrNull()
                if (firstDetail != null) {
                    val intent = Intent(this@ChapterListActivity, com.drmiaji.hisnulmuslim.ui.WebViewActivity::class.java).apply {
                        putExtra("dua_id", firstDetail.id)
                        putExtra("chap_id", duaName.chap_id)
                        putExtra("chapter_name", duaName.chapname ?: "")
                        putExtra("title", duaName.chapname)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ChapterListActivity, "No dua details found for this chapter", Toast.LENGTH_SHORT).show()
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun loadDuaNames() {
        val category = intent.getStringExtra("category") ?: ""

        lifecycleScope.launch {
            val duaNamesFlow = if (category.isNotBlank()) {
                repository.getDuaNamesByCategory(category)
            } else {
                repository.getAllDuaNames()
            }

            duaNamesFlow.collect { duaNames ->
                allDuaNames = duaNames
                adapter.updateData(duaNames, "")
            }
        }
    }

    private fun filterDuaNames(query: String) {
        lifecycleScope.launch {
            if (query.isBlank()) {
                adapter.updateData(allDuaNames, "")
            } else {
                repository.searchDuaNames(query).collect { searchResults ->
                    adapter.updateData(searchResults, query)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterDuaNames(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterDuaNames(it) }
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.share -> {
                val myIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                }
                startActivity(Intent.createChooser(myIntent, "Share using!"))
                return true
            }
            R.id.more_apps -> {
                val moreApp = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/dev?id=5204491413792621474".toUri()
                }
                startActivity(moreApp)
                return true
            }
            R.id.action_about_us -> {
                startActivity(Intent(this, About::class.java))
                return true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}