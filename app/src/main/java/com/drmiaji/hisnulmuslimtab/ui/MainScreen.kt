package com.drmiaji.hisnulmuslimtab.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.drmiaji.hisnulmuslimtab.R
import com.drmiaji.hisnulmuslimtab.activity.SettingsActivity
import com.drmiaji.hisnulmuslimtab.ui.theme.FontManager
import com.drmiaji.hisnulmuslimtab.viewmodel.MainTab
import com.drmiaji.hisnulmuslimtab.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.CompositionLocalProvider
import com.drmiaji.hisnulmuslimtab.activity.About
import com.drmiaji.hisnulmuslimtab.models.DrawerItem
import com.drmiaji.hisnulmuslimtab.ui.theme.topBarColors

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
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val isDarkTheme = isSystemInDarkTheme()

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
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    // Logo/header
                    MyLogo(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                    HorizontalDivider()
                    var selectedItem by remember { mutableStateOf<DrawerItem?>(null) }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        groupedMenuItems.forEach { group ->
                            item {
                                Text(
                                    text = group.groupTitle,
                                    modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 6.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontManager.getSolaimanLipiFontFamily()
                                    )
                                )
                            }
                            itemsIndexed(group.items) { _, item ->
                                DrawerCardItem(
                                    item = item,
                                    selected = item == selectedItem,
                                    onClick = {
                                        selectedItem = item
                                        when {
                                            item.activityClass != null -> {
                                                context.startActivity(Intent(context, item.activityClass))
                                            }
                                            item.linkUrl != null -> {
                                                if (item.linkUrl.contains("facebook.com")) {
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, item.linkUrl.toUri())
                                                    context.startActivity(browserIntent)
                                                } else {
                                                    val intent = Intent(context, WebViewActivity::class.java).apply {
                                                        putExtra("title", item.title)
                                                        putExtra("url", item.linkUrl)
                                                    }
                                                    context.startActivity(intent)
                                                }
                                            }
                                        }
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(10.dp)) }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontFamily = FontManager.getSolaimanLipiFontFamily(),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Overflow menu with About, Settings, Privacy
                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                var showSearchDialog by remember { mutableStateOf(false) }

                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = {
                                        context.startActivity(Intent(context, SettingsActivity::class.java))
                                        showMenu = false
                                    },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("শেয়ার") },
                                    onClick = {
                                        showMenu = false
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
                                    },
                                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Privacy Policy") },
                                    onClick = {
                                        val url = "https://drmiaji.github.io/Tajweed/privacy_policy.html"
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                        context.startActivity(intent)
                                        showMenu = false
                                    },
                                    leadingIcon = { Icon(Icons.Default.PrivacyTip, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("About") },
                                    onClick = {
                                        context.startActivity(Intent(context, About::class.java))
                                        showMenu = false
                                    },
                                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                                )
                            }
                        }
                    },
                    colors = topBarColors()
                )
                if (showSearchDialog) {
                    var query by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showSearchDialog = false },
                        title = { Text("Search") },
                        text = {
                            TextField(
                                value = query,
                                onValueChange = { query = it },
                                label = { Text("Enter search term") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                // Implement your search logic here
                                // For example: viewModel.search(query)
                                showSearchDialog = false
                            }) {
                                Text("Search")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSearchDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
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
                        text = "ক্যাটাগরি/শ্রেণী",
                        selected = selectedTab == MainTab.CATEGORY,
                        onClick = { viewModel.selectTab(MainTab.CATEGORY) }
                    )
                    Spacer(Modifier.width(32.dp))
                    TabText(
                        text = "বিষয় ভিত্তিক",
                        selected = selectedTab == MainTab.CHAPTERS,
                        onClick = { viewModel.selectTab(MainTab.CHAPTERS) }
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search chapters...") },
                    singleLine = true
                )

                // Main Content Area
                if (selectedTab == MainTab.CATEGORY) {
                    val filteredChapters = remember(selectedCategory, allChapters) {
                        allChapters.filter { it.category == selectedCategory?.name }
                            .filter { searchQuery.isBlank() || it.chapname?.contains(searchQuery, ignoreCase = true) == true }
                    }
                    // Add this BEFORE the Row, after you have categories and searchQuery
                    val filteredCategories = remember(categories, searchQuery) {
                        if (searchQuery.isBlank()) categories
                        else categories.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }
                    }
                    Row(Modifier.fillMaxSize()) {
                        CategoryGridPane(
                            categories = filteredCategories,
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
                }
                if (selectedTab == MainTab.CHAPTERS) {
                    val filteredChapters = remember(allChapters, searchQuery) {
                        allChapters.filter { searchQuery.isBlank() || it.chapname?.contains(searchQuery, ignoreCase = true) == true }
                    }
                    ChapterListPane(
                        chapters = filteredChapters,
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
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 24.dp),
        style = TextStyle(
            fontFamily = FontManager.getSolaimanLipiFontFamily(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 20.sp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    )
}