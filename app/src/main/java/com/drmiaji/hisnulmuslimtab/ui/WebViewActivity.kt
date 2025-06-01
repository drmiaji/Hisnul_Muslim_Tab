package com.drmiaji.hisnulmuslimtab.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.drmiaji.hisnulmuslimtab.R
import com.drmiaji.hisnulmuslimtab.activity.About
import com.drmiaji.hisnulmuslimtab.activity.BaseActivity
import com.drmiaji.hisnulmuslimtab.activity.SettingsActivity
import com.drmiaji.hisnulmuslimtab.adapter.WebViewPagerAdapter
import com.drmiaji.hisnulmuslimtab.data.database.HisnulMuslimDatabase
import com.drmiaji.hisnulmuslimtab.data.entities.DuaDetail
import com.drmiaji.hisnulmuslimtab.data.repository.HisnulMuslimRepository
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class WebViewActivity : BaseActivity() {
    private lateinit var repository: HisnulMuslimRepository
    private lateinit var viewPager: ViewPager2
    private val duaIdToChapterName = mutableMapOf<Int, String>()
    private lateinit var btnFavorite: ImageButton
    private lateinit var btnCopy: ImageButton
    private lateinit var btnShare: ImageButton

    override fun getLayoutResource() = R.layout.activity_webview

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupToolbar()
        setupDatabase()
        setupViewPager()
        btnFavorite = findViewById(R.id.btnFavorite)
        btnCopy = findViewById(R.id.btnCopy)
        btnShare = findViewById(R.id.btnShare)

        btnFavorite.setOnClickListener { toggleFavoriteForCurrentChapter() }
        btnCopy.setOnClickListener {
            val htmlContent = getCurrentChapterText()
            copyCurrentChapterToClipboard(htmlContent)
        }

        btnShare.setOnClickListener {
            val htmlContent = getCurrentChapterText()
            shareCurrentChapter(htmlContent)
        }
        // Load all dua names to populate the chapter name map, then load pages
        lifecycleScope.launch {
            repository.getAllDuaNames().collect { duaNames ->
                duaIdToChapterName.clear()
                duaNames.forEach { duaName ->
                    duaIdToChapterName[duaName.chap_id] = duaName.chapname ?: ""
                }
                loadAllDuaPages()
            }
        }
        lifecycleScope.launch {
            repository.getAllFavorites().collect { favList ->
                favoriteChapterIds = favList.map { it.chapId }.toSet()
                updateFavoriteButtonState()
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val titleTextView = findViewById<TextView>(R.id.toolbar_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val title = intent.getStringExtra("title") ?: getString(R.string.app_name)
        titleTextView.text = title

        val typeface = Typeface.createFromAsset(assets, "fonts/solaimanlipi.ttf")
        titleTextView.typeface = typeface

        val iconColor = ContextCompat.getColor(this, R.color.toolbar_icon_color)
        toolbar.navigationIcon?.let { drawable ->
            val wrapped = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrapped, iconColor)
            toolbar.navigationIcon = wrapped
        }
    }

    private var currentChapterId: Int? = null
    private var favoriteChapterIds: Set<Int> = emptySet()

    private fun updateFavoriteButtonState() {
        val chapId = currentChapterId
        if (chapId != null && chapId in favoriteChapterIds) {
            btnFavorite.setImageResource(R.drawable.ic_star_added) // filled
        } else {
            btnFavorite.setImageResource(R.drawable.ic_star) // outline
        }
    }

    // Call this whenever chapter changes (onPageSelected):
    private fun onChapterChanged(chapId: Int) {
        currentChapterId = chapId
        updateFavoriteButtonState()
    }

    private fun toggleFavoriteForCurrentChapter() {
        val chapId = currentChapterId ?: return
        lifecycleScope.launch {
            if (chapId in favoriteChapterIds) {
                repository.removeFavorite(chapId)
            } else {
                repository.addFavorite(chapId)
            }
            // Re-fetch favorites to update state/UI
            repository.getAllFavorites().collect { favList ->
                favoriteChapterIds = favList.map { it.chapId }.toSet()
                updateFavoriteButtonState()
            }
        }
    }

    private fun getFormattedChapterText(htmlContent: String): String {
        val plainText = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY).toString()
        return "$plainText\n-হিননুল মূুসলিম থেকে। এপটি ডাউনলোড করতে নিচের লিংকে ক্লিক করুন:\n" +
                "https://play.google.com/store/apps/details?id=com.drmiaji.hisnulmuslimtab"
    }

    private fun copyCurrentChapterToClipboard(htmlContent: String) {
        val finalText = getFormattedChapterText(htmlContent)
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", finalText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareCurrentChapter(htmlContent: String) {
        val finalText = getFormattedChapterText(htmlContent)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, finalText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun getCurrentChapterText(): String {
        // You must keep the text/html for the current chapter accessible. For example:
        // If you keep a list of html/text pages as in your viewPager adapter, use the current index:
        val currentItem = viewPager.currentItem
        val adapter = viewPager.adapter as? WebViewPagerAdapter
        return adapter?.getPageText(currentItem) ?: ""
    }

    private fun setupDatabase() {
        val database = HisnulMuslimDatabase.getDatabase(this)
        repository = HisnulMuslimRepository(
            database.categoryDao(),
            database.duaNameDao(),
            database.duaDetailDao(),
            database.favoriteDao()
        )
    }

    private fun setupViewPager() {
        viewPager = findViewById(R.id.viewPager)
    }

    private fun loadAllDuaPages() {
        val selectedDuaId = intent.getIntExtra("dua_id", -1)
        val selectedChapterId = intent.getIntExtra("chap_id", -1)

        lifecycleScope.launch {
            repository.getAllDuaDetailsSorted().collect { allDuaDetails ->
                // Group dua details by dua_global_id (chapter)
                val groupedByChapter = allDuaDetails.groupBy { it.dua_global_id }

                // Build HTML for each chapter (group of dua details)
                val htmlPages = groupedByChapter.map { (chapterId, duaDetailsInChapter) ->
                    val chapterName = duaIdToChapterName[chapterId] ?: ""
                    generateHtmlContent(duaDetailsInChapter, chapterName)
                }

                viewPager.adapter = WebViewPagerAdapter(this@WebViewActivity, htmlPages)

                // Find the page containing the selected dua or chapter
                val startPageIndex = if (selectedDuaId != -1) {
                    // Find page by specific dua ID
                    groupedByChapter.entries.indexOfFirst { (_, duaList) ->
                        duaList.any { it.id == selectedDuaId }
                    }
                } else if (selectedChapterId != -1) {
                    // Find page by chapter ID
                    groupedByChapter.keys.indexOf(selectedChapterId)
                } else {
                    -1
                }

                if (startPageIndex >= 0) {
                    viewPager.setCurrentItem(startPageIndex, false)
                }

                // Update toolbar title as user swipes between chapters
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val chapterIds = groupedByChapter.keys.toList()
                        if (position < chapterIds.size) {
                            val currentChapterId = chapterIds[position]
                            val chapterName = duaIdToChapterName[currentChapterId] ?: getString(R.string.app_name)
                            val titleTextView = findViewById<TextView>(R.id.toolbar_title)
                            titleTextView.text = chapterName

                            // Add this line to update favorite/copy/share state for the current chapter:
                            onChapterChanged(currentChapterId)
                        }
                    }
                })
            }
        }
    }

    private fun generateHtmlContent(duaDetails: List<DuaDetail>, chapterName: String): String {
        val showTransliteration = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getBoolean("show_transliteration", true)

        val htmlBuilder = StringBuilder()
        htmlBuilder.append("""
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" type="text/css" href="style.css">
        </head>
        <body>
    """)

        if (chapterName.isNotEmpty()) {
            htmlBuilder.append("<h3 class='chapter-title'>$chapterName</h3>")
        }

        duaDetails.forEachIndexed { index, detail ->
            htmlBuilder.append("<div class='dua-container'><div class='segment'>")

            detail.top?.takeIf { it.isNotBlank() }?.let {
                htmlBuilder.append("<div class='top-text'>$it</div>")
            }
            detail.arabic?.takeIf { it.isNotBlank() }?.let {
                htmlBuilder.append("<div class='arabic'>$it</div>")
            }
            if (showTransliteration) {
                detail.transliteration?.takeIf { it.isNotBlank() }?.let {
                    htmlBuilder.append("<div class='transliteration'><b>উচ্চারণ:</b> $it</div>")
                }
            }
            detail.translations?.takeIf { it.isNotBlank() }?.let {
                htmlBuilder.append("<div class='translation'><b>অনুবাদ:</b> $it</div>")
            }
            detail.bottom?.takeIf { it.isNotBlank() }?.let {
                htmlBuilder.append("<div class='bottom-text'>$it</div>")
            }
            detail.reference?.takeIf { it.isNotBlank() }?.let {
                htmlBuilder.append("<div class='reference'><b>তথ্যসূত্র:</b> $it</div>")
            }

            htmlBuilder.append("</div></div>")

            if (index < duaDetails.size - 1) {
                htmlBuilder.append("<hr class='dua-divider'>")
            }
        }

        htmlBuilder.append("""
        </body>
        </html>
    """)
        return htmlBuilder.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        menu.findItem(R.id.action_search)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.share -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                }
                startActivity(Intent.createChooser(shareIntent, "Share using"))
                true
            }
            R.id.more_apps -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/dev?id=5204491413792621474".toUri()
                })
                true
            }
            R.id.action_about_us -> {
                startActivity(Intent(this, About::class.java))
                true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        loadAllDuaPages()
    }
}