package com.drmiaji.hisnulmuslimtab.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drmiaji.hisnulmuslimtab.models.DrawerItem

@Composable
fun DrawerContent(
    onMenuItemClick: (DrawerItem) -> Unit,
    onLogoClick: () -> Unit
) {
    // Use your actual MyLogo, groupedMenuItems, DrawerCardItem implementations!
    Column(Modifier.fillMaxSize()) {
        MyLogo(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onLogoClick() }
        )
        groupedMenuItems.forEach { group ->
            group.items.forEach { item ->
                DrawerCardItem(
                    item = item,
                    onClick = { onMenuItemClick(item) }
                )
            }
        }
    }
}