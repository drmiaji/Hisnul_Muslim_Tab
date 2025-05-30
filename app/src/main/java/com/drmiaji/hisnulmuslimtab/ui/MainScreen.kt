package com.drmiaji.hisnulmuslimtab.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.drmiaji.hisnulmuslimtab.R
import com.drmiaji.hisnulmuslimtab.activity.SettingsActivity
import com.drmiaji.hisnulmuslimtab.viewmodel.MainTab
import com.drmiaji.hisnulmuslimtab.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val categories by viewModel.categories.collectAsState()
    val allChapters by viewModel.allChapters.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var selectedDrawerItem by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    ),
                drawerContainerColor = Color.Transparent
            ) {
                DrawerContent(
                    onMenuItemClick = { item ->
                        selectedDrawerItem = item.title
                        coroutineScope.launch { drawerState.close() }

                        // Handle navigation: activity, link, etc.
                        when {
                            item.activityClass != null -> {
                                context.startActivity(Intent(context, item.activityClass))
                            }

                            !item.linkUrl.isNullOrEmpty() -> {
                                val intent = Intent(Intent.ACTION_VIEW, item.linkUrl.toUri())
                                context.startActivity(intent)
                            }

                            item.title.contains("শেয়ার") || item.title.equals(
                                "Share",
                                ignoreCase = true
                            ) -> {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_SUBJECT,
                                        context.getString(R.string.share_subject)
                                    )
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        context.getString(R.string.share_message)
                                    )
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share using"
                                    )
                                )
                            }
                        }
                    },
                    onLogoClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { MyLogo() },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Search button
                        IconButton(onClick = { /* TODO: Implement search navigation or dialog */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        // Share button
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_subject))
                                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share using"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        // Settings button
                        IconButton(onClick = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            ) {
                // Tab Row always visible
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabText(
                        text = "Category",
                        selected = selectedTab == MainTab.CATEGORY,
                        onClick = { viewModel.selectTab(MainTab.CATEGORY) }
                    )
                    Spacer(Modifier.width(32.dp))
                    TabText(
                        text = "Chapters",
                        selected = selectedTab == MainTab.CHAPTERS,
                        onClick = { viewModel.selectTab(MainTab.CHAPTERS) }
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                // Main Content Area
                if (selectedTab == MainTab.CATEGORY) {
                    val filteredChapters = remember(selectedCategory, allChapters) {
                        allChapters.filter { it.category == selectedCategory?.name }
                    }
                    Row(Modifier.fillMaxSize()) {
                        CategoryGridPane(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategoryClick = { viewModel.selectCategory(it) },
                            modifier = Modifier.width(200.dp).fillMaxHeight()
                        )
                        ChapterListPane(
                            chapters = filteredChapters,
                            showCategoryTitle = selectedCategory != null,
                            categoryTitle = selectedCategory?.name,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onChapterClick = { chapter ->
                                coroutineScope.launch {
                                    val firstDetailId = viewModel.getFirstDuaDetailIdForChapter(chapter.chap_id)
                                    if (firstDetailId != null) {
                                        val intent = Intent(context, WebViewActivity::class.java).apply {
                                            putExtra("dua_id", firstDetailId)
                                            putExtra("chap_id", chapter.chap_id)
                                            putExtra("chapter_name", chapter.chapname ?: "")
                                            putExtra("title", chapter.chapname)
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "No dua details found for this chapter", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                } else {
                    ChapterListPane(
                        chapters = allChapters,
                        showCategoryTitle = false,
                        categoryTitle = null,
                        modifier = Modifier.fillMaxSize(),
                        onChapterClick = { chapter ->
                            coroutineScope.launch {
                                val firstDetailId = viewModel.getFirstDuaDetailIdForChapter(chapter.chap_id)
                                if (firstDetailId != null) {
                                    val intent = Intent(context, WebViewActivity::class.java).apply {
                                        putExtra("dua_id", firstDetailId)
                                        putExtra("chap_id", chapter.chap_id)
                                        putExtra("chapter_name", chapter.chapname ?: "")
                                        putExtra("title", chapter.chapname)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "No dua details found for this chapter", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TabText(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    )
}