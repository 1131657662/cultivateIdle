package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.db.PlayerProfileEntity
import com.example.model.RealmCatalog
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBackground
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdCrimson
import com.example.ui.theme.HdGoldPrimary
import com.example.ui.theme.HdJade
import com.example.ui.theme.HdPurpleContainer
import com.example.ui.theme.HdPurpleOnContainer
import com.example.ui.theme.HdPurplePrimary
import com.example.ui.theme.HdSurface
import com.example.ui.theme.HdSurfaceVariant
import com.example.ui.theme.HdTextMuted
import com.example.ui.theme.HdTextPrimary
import com.example.ui.theme.HdTextSecondary
import com.example.ui.theme.HdTextWhite
import com.example.viewmodel.TribulationState

@Composable
fun TribulationDialog(
    profile: PlayerProfileEntity?,
    tribulationState: TribulationState,
    onPillCountChange: (Int) -> Unit,
    onExecuteTribulation: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!tribulationState.isShowing || profile == null) return

    val currentRealm = RealmCatalog.getRealm(profile.realmId)
    val nextStageName = if (profile.realmStage >= currentRealm.maxStage) {
        val nextR = RealmCatalog.getRealm(profile.realmId + 1)
        "${nextR.name} 1阶"
    } else {
        "${currentRealm.name} ${profile.realmStage + 1}阶"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "lightning_glow")
    val lightningAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Dialog(
        onDismissRequest = {
            if (!tribulationState.isResolving) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, HdBorder, RoundedCornerShape(24.dp))
                .testTag("tribulation_dialog"),
            color = HdSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "天劫",
                            tint = HdPurplePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (tribulationState.isMajorRealm) "九重天劫 · 破大境界" else "凝聚真元 · 破境突破",
                            color = HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (!tribulationState.isResolving) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("close_tribulation_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = HdTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Visual / Lightning Art
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, HdBorder, RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_tribulation),
                        contentDescription = "天劫雷云",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC1A102F))
                                )
                            )
                    )

                    // Target realm badge
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${currentRealm.name} ${profile.realmStage}阶  ➔  突破至: $nextStageName",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Result Display or Breakthrough Config
                if (tribulationState.resultSuccess != null) {
                    // Result Screen
                    val isSuccess = tribulationState.resultSuccess == true
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSuccess) HdJade.copy(alpha = 0.12f) else HdCrimson.copy(alpha = 0.12f))
                            .border(1.dp, if (isSuccess) HdJade else HdCrimson, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isSuccess) "🎉 渡劫圆满，道心通明！" else "⚡ 劫雷反噬，突破受挫！",
                                color = if (isSuccess) HdJade else HdCrimson,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tribulationState.resultMessage,
                                color = HdTextPrimary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("dismiss_result_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HdPurplePrimary,
                            contentColor = HdTextWhite
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = "感悟天道 巩固修为",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else if (tribulationState.isResolving) {
                    // Dramatic Resolving Lightning Animation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = HdPurplePrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "九天雷霆轰鸣凝聚，天劫正在降临...",
                            color = HdPurplePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.alpha(lightningAlpha)
                        )
                        Text(
                            text = "护体罡气抵御心魔中...",
                            color = HdTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    // Mutual Body Requirement Check Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (tribulationState.reqBodyMet) HdJade.copy(alpha = 0.1f) else HdCrimson.copy(alpha = 0.12f))
                            .border(1.dp, if (tribulationState.reqBodyMet) HdJade.copy(alpha = 0.4f) else HdCrimson, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (tribulationState.reqBodyMet) Icons.Default.Shield else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (tribulationState.reqBodyMet) HdJade else HdCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (tribulationState.reqBodyMet) "肉身承载达标" else "肉身承载不足 (无法引动天劫)",
                                        color = if (tribulationState.reqBodyMet) HdJade else HdCrimson,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = tribulationState.reqBodyMessage,
                                        color = HdTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Success Rate & Pill Boost Config
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(HdSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "成功率",
                                tint = if (tribulationState.successRate >= 90) HdJade else if (tribulationState.successRate >= 60) HdPurplePrimary else HdCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "渡劫成功率",
                                color = HdTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "${tribulationState.successRate}%",
                            color = if (tribulationState.successRate >= 90) HdJade else if (tribulationState.successRate >= 60) HdPurplePrimary else HdCrimson,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pill selection for rate boost
                    tribulationState.pillItem?.let { pill ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(HdPurpleContainer.copy(alpha = 0.5f))
                                .border(1.dp, HdBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "辅助灵丹: ${pill.name}",
                                        color = HdPurpleOnContainer,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "每颗提升 +${pill.tribulationRateBonus}% 成功率 (拥有: ${tribulationState.maxPillsAvailable}颗)",
                                        color = HdTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (tribulationState.pillsUsed > 0) {
                                                onPillCountChange(tribulationState.pillsUsed - 1)
                                            }
                                        },
                                        enabled = tribulationState.pillsUsed > 0,
                                        modifier = Modifier.size(32.dp).testTag("decrease_pill_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "减少",
                                            tint = if (tribulationState.pillsUsed > 0) HdTextPrimary else HdTextMuted
                                        )
                                    }

                                    Text(
                                        text = "${tribulationState.pillsUsed}",
                                        color = HdPurplePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            if (tribulationState.pillsUsed < tribulationState.maxPillsAvailable && tribulationState.successRate < 100) {
                                                onPillCountChange(tribulationState.pillsUsed + 1)
                                            }
                                        },
                                        enabled = tribulationState.pillsUsed < tribulationState.maxPillsAvailable && tribulationState.successRate < 100,
                                        modifier = Modifier.size(32.dp).testTag("increase_pill_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "增加",
                                            tint = if (tribulationState.pillsUsed < tribulationState.maxPillsAvailable && tribulationState.successRate < 100) HdTextPrimary else HdTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Warning note
                    if (!tribulationState.reqBodyMet) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "提示",
                                tint = HdCrimson,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "肉身承载未达标，强行渡劫将爆体而亡！请先前往淬炼肉身。",
                                color = HdCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    } else if (tribulationState.successRate < 100) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "提示",
                                tint = HdCrimson,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "渡劫若失败将损失20%修为，建议将成功率提升至100%再行突破！",
                                color = HdTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Action Button
                    Button(
                        onClick = onExecuteTribulation,
                        enabled = tribulationState.reqBodyMet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("execute_tribulation_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tribulationState.reqBodyMet) HdPurplePrimary else HdBorder,
                            contentColor = if (tribulationState.reqBodyMet) HdTextWhite else HdTextMuted
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (tribulationState.reqBodyMet) HdTextWhite else HdTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!tribulationState.reqBodyMet) "肉身承载不足 (需先淬体)"
                            else if (tribulationState.isMajorRealm) "引动九天雷劫 破境飞升"
                            else "凝练精气 突破境界",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
