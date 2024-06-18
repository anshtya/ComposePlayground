package com.example.composeplayground.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val collapsedHeight = 64.dp
private val appbarHeight = 152.dp
val collapseHeight = appbarHeight - collapsedHeight

@Composable
fun CustomCollapsingTopAppBar() {

    val collapseHeightPx = with(LocalDensity.current) { collapseHeight.toPx() }
    var collapseOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val collapseOffsetHeightDp = with(LocalDensity.current) { collapseOffsetHeightPx.toDp() }
    val height = appbarHeight + collapseOffsetHeightDp

    val enterAlwaysNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val previousOffset = collapseOffsetHeightPx
                val newOffset = collapseOffsetHeightPx + delta
                collapseOffsetHeightPx = newOffset.coerceIn(-collapseHeightPx, 0f)
                return if (previousOffset != collapseOffsetHeightPx) {
                    // We are in the middle of top app bar collapse
                    available
                } else {
                    Offset.Zero
                }
            }
        }
    }

    val exitAlwaysNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // if scrolling down, don't consume anything
                if (delta > 0f) return Offset.Zero

                val previousOffset = collapseOffsetHeightPx
                val newOffset = collapseOffsetHeightPx + delta
                collapseOffsetHeightPx = newOffset.coerceIn(-collapseHeightPx, 0f)
                return if (previousOffset != collapseOffsetHeightPx) {
                    // We are in the middle of top app bar collapse
                    available
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // change height of top app bar when scrolling all the way down and
                // child has finished scrolling
                if (consumed.y >= 0f && available.y > 0f) {
                    val prevOffset = collapseOffsetHeightPx
                    val newOffset = collapseOffsetHeightPx + available.y
                    collapseOffsetHeightPx = newOffset.coerceIn(-collapseHeightPx, 0f)
                    return Offset(x = 0f, y = (collapseOffsetHeightPx - prevOffset))
                }

                return Offset.Zero
            }
        }
    }

    Column(Modifier.nestedScroll(enterAlwaysNestedScrollConnection)) {
        CustomTopAppBar(Modifier.height(height))
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = (1..50).map { it }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxWidth()
                ) {
                    Text(it.toString(), Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@Composable
private fun CustomTopAppBar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Green)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(collapsedHeight)
                .align(Alignment.TopCenter)
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }

            Text(
                text = "Title",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}