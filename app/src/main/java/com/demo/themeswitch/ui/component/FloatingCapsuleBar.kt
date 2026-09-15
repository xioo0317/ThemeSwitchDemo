package com.demo.themeswitch.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon as MaterialIcon
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.blur.Backdrop
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop

/** 悬浮胶囊底栏的单个标签 */
data class FloatingTab(
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val label: String,
)

/**
 * KSU 风格的悬浮胶囊底栏：胶囊浮起 + 选中指示块，
 * 开启液态玻璃时通过 miuix-blur 对背后内容实时模糊。
 */
@Composable
fun FloatingCapsuleBar(
    items: List<FloatingTab>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    accentColor: Color,
    contentColor: Color,
    containerColor: Color,
    blurEnabled: Boolean,
    backdrop: Backdrop?,
    modifier: Modifier = Modifier,
) {
    val pillShape = CircleShape
    val useBlur = blurEnabled && backdrop != null
    val barColor = if (useBlur) containerColor.copy(alpha = 0.4f) else containerColor

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .height(64.dp)
            .dropShadow(
                shape = pillShape,
                shadow = Shadow(radius = 10.dp, color = Color.Black, alpha = 0.15f),
            )
            .then(
                if (useBlur) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop!!,
                        shape = { pillShape },
                        effects = {
                            blur(4.dp.toPx(), 4.dp.toPx())
                        },
                        onDrawSurface = { drawRect(barColor) },
                    )
                } else {
                    Modifier.background(barColor, pillShape)
                },
            )
            .clip(pillShape),
    ) {
        val tabWidth = maxWidth / items.size
        val indicatorX by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = tween(durationMillis = 250),
            label = "floatingBarIndicator",
        )

        // 选中指示胶囊
        Box(
            modifier = Modifier
                .offset(x = indicatorX)
                .width(tabWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .background(accentColor.copy(alpha = 0.15f), pillShape),
        )

        // 标签行
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEachIndexed { index, tab ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSelected(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    MaterialIcon(
                        imageVector = if (selected) tab.filledIcon else tab.outlinedIcon,
                        contentDescription = tab.label,
                        tint = if (selected) accentColor else contentColor,
                        modifier = Modifier.size(22.dp),
                    )
                    MaterialText(
                        text = tab.label,
                        fontSize = 10.sp,
                        color = if (selected) accentColor else contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
