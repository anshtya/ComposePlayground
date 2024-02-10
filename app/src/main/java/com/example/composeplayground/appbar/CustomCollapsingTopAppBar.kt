package com.example.composeplayground.appbar

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

private val collapsedHeight = 64.dp
private val expandedHeight = 152.dp

// Height needed to be collapsed
private val collapseHeight = expandedHeight - collapsedHeight

@Composable
fun CustomCollapsingTopAppBarExample() {

    val appBarCollapseHeightPx = with(LocalDensity.current) {
        collapseHeight.roundToPx().toFloat()
    }

    val appBarCollapseOffsetHeightPx = remember { mutableFloatStateOf(0f) }
    val appBarCollapseOffsetHeightDp = with(LocalDensity.current) {
        appBarCollapseOffsetHeightPx.floatValue.toDp()
    }
    val appBarHeight = expandedHeight + appBarCollapseOffsetHeightDp

    val isCollapsed by remember {
        derivedStateOf {
            appBarCollapseHeightPx == appBarCollapseOffsetHeightPx.floatValue.absoluteValue
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                val delta = available.y
                val newOffset = appBarCollapseOffsetHeightPx.floatValue + delta
                appBarCollapseOffsetHeightPx.floatValue =
                    newOffset.coerceIn(-appBarCollapseHeightPx, 0f)
                // here's the catch: let's pretend we consumed 0 in any case, since we want
                // LazyColumn to scroll anyway for good UX
                // We're basically watching scroll without taking it
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        CustomCollapsingTopAppBar(
            isCollapsed = isCollapsed,
            modifier = Modifier.height(appBarHeight)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = (1..50).map { it }
            ) {
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
private fun CustomCollapsingTopAppBar(
    isCollapsed: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Green,
        modifier = modifier
    ) {
        Box(Modifier.fillMaxSize()) {
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

                AnimatedVisibility(isCollapsed) {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCollapsingAppBarPreview() {
    CustomCollapsingTopAppBarExample()
}