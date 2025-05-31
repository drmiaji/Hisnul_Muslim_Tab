package com.drmiaji.hisnulmuslimtab.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drmiaji.hisnulmuslimtab.data.entities.Category
import com.drmiaji.hisnulmuslimtab.ui.theme.FontManager
import com.drmiaji.hisnulmuslimtab.utils.toBengaliNumberString

@Composable
fun CategoryGridPane(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val sortedCategories = categories.sortedBy { it.id }
        items(sortedCategories) { category ->
            Card(
                onClick = { onCategoryClick(category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (category == selectedCategory)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = "${category.id.toBengaliNumberString()}. ${category.name.orEmpty()}",
                        fontFamily = FontManager.getSolaimanLipiFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                }
            }
        }
    }
}