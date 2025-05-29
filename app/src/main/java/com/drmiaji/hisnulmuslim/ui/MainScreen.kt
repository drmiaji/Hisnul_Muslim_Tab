package com.drmiaji.hisnulmuslim.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.drmiaji.hisnulmuslim.R
import com.drmiaji.hisnulmuslim.activity.About
import com.drmiaji.hisnulmuslim.activity.SettingsActivity
import com.drmiaji.hisnulmuslim.models.DrawerItem
import com.drmiaji.hisnulmuslim.ui.theme.FontManager
import com.drmiaji.hisnulmuslim.ui.theme.topBarColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToContents: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onLogoClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    // Logo and header section (fixed at top)
                    MyLogo(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    )

                    HorizontalDivider()

                    // State for tracking selected item
                    var selectedItem by remember { mutableStateOf<DrawerItem?>(null) }

                    // Scrollable content section
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        groupedMenuItems.forEach { group ->
                            item {
                                Text(
                                    text = group.groupTitle,
                                    modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 4.dp),
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
                                                    // Open in external browser
                                                    val browserIntent = Intent(Intent.ACTION_VIEW,
                                                        item.linkUrl.toUri())
                                                    context.startActivity(browserIntent)
                                                } else {
                                                    // Open in WebViewActivity
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

                            item {
                                Spacer(modifier = Modifier.height(8.dp)) // space between groups
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontFamily = FontManager.getSolaimanLipiFontFamily(),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    ) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("About") },
                                    onClick = { context.startActivity(Intent(context,
                                        About::class.java)); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = { context.startActivity(Intent(context,
                                        SettingsActivity::class.java)); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Privacy Policy") },
                                    onClick = {
                                        val url = "https://drmiaji.github.io/Tajweed/privacy_policy.html\n"
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                        context.startActivity(intent)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    },
                    colors = topBarColors()
                )
            }
        ) { innerPadding ->
            val colors = MaterialTheme.colorScheme
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colors.secondaryContainer,
                                colors.secondary
                            )
                        )
                    )
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Add category/chapter selection
                TwoColumnSelection(
                    onCategoryClick = { // Navigate to category grid
                        val intent = Intent(context, ChapterListActivity::class.java)
                        context.startActivity(intent)
                    },
                    onChapterClick = { // Navigate to all chapter list
                        val intent = Intent(context, ChapterListActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}