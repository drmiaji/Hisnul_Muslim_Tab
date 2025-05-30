package com.drmiaji.hisnulmuslimtab.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.drmiaji.hisnulmuslimtab.viewmodel.MainTab
import com.drmiaji.hisnulmuslimtab.viewmodel.MainViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val categories by viewModel.categories.collectAsState()
    val allChapters by viewModel.allChapters.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        // TopBar
        TopAppBar(
            title = { Text("Hisnul Muslim", fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = { /* handle drawer open if needed */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            }
        )

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
            // Move filteredChapters outside the Row
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
                    openWebViewForChapter(context, chapter)
                }
            )
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