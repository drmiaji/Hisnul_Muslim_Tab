package com.drmiaji.hisnulmuslimtab.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drmiaji.hisnulmuslimtab.data.entities.DuaName
import androidx.compose.foundation.lazy.items

@Composable
fun ChapterListPane(
    chapters: List<DuaName>,
    showCategoryTitle: Boolean,
    categoryTitle: String?,
    modifier: Modifier = Modifier,
    onChapterClick: (DuaName) -> Unit
) {
    Column(modifier) {
        if (showCategoryTitle && !categoryTitle.isNullOrBlank()) {
            Text(
                text = categoryTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(chapters) { chapter -> // chapter: DuaName
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChapterClick(chapter) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chapter.chap_id.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(18.dp))
                        Column {
                            Text(
                                text = chapter.chapname.orEmpty(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (!chapter.category.isNullOrBlank()) {
                                Text(
                                    text = chapter.category,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun openWebViewForChapter(context: Context, duaName: DuaName) {
    val intent = Intent(context, WebViewActivity::class.java).apply {
        putExtra("dua_id", duaName.chap_id) // <--- this is the fix!
        putExtra("chapter_name", duaName.chapname ?: "")
        putExtra("title", duaName.chapname)
    }
    context.startActivity(intent)
}