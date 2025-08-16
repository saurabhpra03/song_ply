package com.song.ply.presentation.ui.view.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.song.ply.presentation.ui.theme.Dimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTab(
    modifier: Modifier,
    selectedTab: MutableState<Int>) {
    val tabs = listOf("All", "Playlist")

    PrimaryTabRow(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        divider = {
            HorizontalDivider(
                thickness = Dimen.tabHorizontalDivider,
                color = MaterialTheme.colorScheme.primary
            )
        },
        indicator = {},
        selectedTabIndex = selectedTab.value
    ) {
        tabs.forEachIndexed { index, destination ->
            Tab(
                modifier = Modifier
                    .padding(
                        start = if (index == 0) Dimen.dimen20 else Dimen.dimen10,
                        end = if (index == 0) Dimen.dimen10 else Dimen.dimen20,
                        bottom = Dimen.dimen10
                    )
                    .height(Dimen.dimen50),
                selected = selectedTab.value == index,
                onClick = {
                    selectedTab.value = index
                },
                content = {
                    Text(
                        text = destination,
                        style = MaterialTheme.typography.headlineMedium
                            .copy(color = if (selectedTab.value == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary)
                    )
                }
            )
        }
    }
}