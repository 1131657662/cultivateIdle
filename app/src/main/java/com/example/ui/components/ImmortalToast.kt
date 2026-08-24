package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdCrimson
import com.example.ui.theme.HdGoldPrimary
import com.example.ui.theme.HdJade
import com.example.ui.theme.HdPurpleContainer
import com.example.ui.theme.HdPurpleOnContainer
import com.example.ui.theme.HdPurplePrimary
import com.example.ui.theme.HdSurface
import com.example.ui.theme.HdTextMuted
import com.example.ui.theme.HdTextPrimary
import com.example.ui.theme.HdTextSecondary
import com.example.ui.theme.HdTextWhite

/**
 * 仙家灵符法谕浮动通知组件
 * 替代原生底部黑色突兀 Snackbar，浮动在屏幕上方，绝不挤压或破坏界面结构。
 */
@Composable
fun ImmortalSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { snackbarData ->
        ImmortalToastCard(message = snackbarData.visuals.message)
    }
}

@Composable
fun ImmortalToastCard(message: String) {
    val isWarningOrFailure = message.contains("不足") ||
            message.contains("无法") ||
            message.contains("失败") ||
            message.contains("未达") ||
            message.contains("上限") ||
            message.contains("稍候")

    val isMilestone = message.contains("飞升") ||
            message.contains("突破") ||
            message.contains("登仙") ||
            message.contains("无上") ||
            message.contains("顿悟") ||
            message.contains("🎉")

    val isGain = message.contains("成功") ||
            message.contains("获得") ||
            message.contains("升阶") ||
            message.contains("领取") ||
            message.contains("参悟") ||
            message.contains("炼出") ||
            message.contains("锻造") ||
            message.contains("结识") ||
            message.contains("圆满") ||
            message.contains("更替")

    val accentColor = when {
        isMilestone -> HdGoldPrimary
        isWarningOrFailure -> HdCrimson
        isGain -> HdJade
        else -> HdPurplePrimary
    }

    val iconVector: ImageVector = when {
        isMilestone -> Icons.Default.AutoAwesome
        isWarningOrFailure -> Icons.Default.WarningAmber
        isGain -> Icons.Default.CheckCircle
        else -> Icons.Default.Spa
    }

    val tagText = when {
        isMilestone -> "仙缘机运"
        isWarningOrFailure -> "神识传讯"
        isGain -> "造化所钟"
        else -> "仙道法谕"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 220.dp, max = 380.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(100.dp),
                    spotColor = accentColor.copy(alpha = 0.35f),
                    ambientColor = accentColor.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(100.dp),
            color = HdSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.12f),
                                HdSurface,
                                accentColor.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Ethereal glowing icon badge
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge text
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 1.5.dp)
                ) {
                    Text(
                        text = tagText,
                        color = accentColor,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Core notification content
                Text(
                    text = message,
                    color = HdTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
