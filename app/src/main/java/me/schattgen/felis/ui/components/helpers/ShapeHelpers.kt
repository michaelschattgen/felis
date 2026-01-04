package me.schattgen.felis.ui.components.helpers

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ShapeHelpers {
    @Composable
    fun getGroupedShape(
        isTop: Boolean,
        isBottom: Boolean,
        isStart: Boolean,
        isEnd: Boolean,
        outerRadius: Dp = 28.dp,
        innerRadius: Dp = 4.dp
    ): CornerBasedShape {
        return RoundedCornerShape(
            topStart = if (isTop && isStart) outerRadius else innerRadius,
            topEnd = if (isTop && isEnd) outerRadius else innerRadius,
            bottomStart = if (isBottom && isStart) outerRadius else innerRadius,
            bottomEnd = if (isBottom && isEnd) outerRadius else innerRadius
        )
    }
}