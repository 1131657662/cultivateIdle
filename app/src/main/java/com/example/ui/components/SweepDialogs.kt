package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.PlayerProfileEntity
import com.example.model.AdventureHelper
import com.example.model.AdventureMap
import com.example.model.ItemCatalog
import com.example.model.SweepRewardResult
import com.example.ui.theme.HdAzure
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

@Composable
fun SweepDialog(
    map: AdventureMap,
    profile: PlayerProfileEntity,
    onConfirmSweep: (times: Int) -> Unit,
    onBuyExtraSweeps: () -> Unit,
    onDismiss: () -> Unit
) {
    val maxDaily = AdventureHelper.calculateMaxDailySweeps(profile.realmId, profile.extraSweepPurchasedToday)
    val remaining = maxOf(0, maxDaily - profile.dailySweepUsed)
    val canBuyMore = profile.extraSweepPurchasedToday < 3
    val canAffordBuy = profile.spiritStones >= 200

    var selectedTimes by remember { mutableIntStateOf(minOf(10, maxOf(1, remaining))) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = HdSurface),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HdGoldPrimary.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(HdGoldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = HdGoldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "神识出窍 · 秘境扫荡", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = HdTextPrimary)
                            Text(text = "【${map.name}】", fontSize = 12.sp, color = HdPurplePrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "关闭", tint = HdTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Daily sweep allowance status card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "今日神游扫荡令", fontSize = 12.sp, color = HdTextSecondary)
                            Text(
                                text = "$remaining / $maxDaily 次",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remaining > 0) HdJade else HdCrimson
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Buy extra sweeps row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "补充神游次数 (今日还可购 ${3 - profile.extraSweepPurchasedToday}/3 次)",
                                fontSize = 10.sp,
                                color = HdTextMuted
                            )
                            if (canBuyMore) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(if (canAffordBuy) HdGoldPrimary.copy(alpha = 0.2f) else HdBorder)
                                        .clickable(enabled = canAffordBuy) { onBuyExtraSweeps() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = if (canAffordBuy) HdGoldPrimary else HdTextMuted, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "+10次 (200灵石)", fontSize = 10.sp, color = if (canAffordBuy) HdGoldPrimary else HdTextMuted, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Choose sweep count buttons
                Text(
                    text = "选择扫荡次数",
                    fontSize = 12.sp,
                    color = HdTextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sweepOptions = listOf(1, 5, 10, remaining)
                    val optionLabels = listOf("1次", "5次", "10次", "全部")

                    sweepOptions.forEachIndexed { idx, count ->
                        val isValid = count in 1..remaining
                        val isSelected = selectedTimes == count && isValid

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) HdPurplePrimary else if (isValid) HdSurfaceVariant else HdBorder)
                                .border(1.dp, if (isSelected) HdPurplePrimary else HdBorder, RoundedCornerShape(10.dp))
                                .clickable(enabled = isValid) { selectedTimes = count }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optionLabels[idx],
                                color = if (isSelected) HdTextWhite else if (isValid) HdTextPrimary else HdTextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Expected Reward Estimate
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "预计神游收益 (${selectedTimes}次):", fontSize = 11.sp, color = HdTextSecondary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "💎 灵石收获:", fontSize = 11.sp, color = HdTextMuted)
                            Text(text = "约 ${(map.stoneRewardMin + map.stoneRewardMax) / 2 * selectedTimes} 灵石", fontSize = 11.sp, color = HdGoldPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "✨ 修为增进:", fontSize = 11.sp, color = HdTextMuted)
                            Text(text = "+${map.expReward * selectedTimes} 点修为", fontSize = 11.sp, color = HdJade, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🌿 产出灵药/灵矿:", fontSize = 11.sp, color = HdTextMuted)
                            Text(text = "大量本秘境专属材料", fontSize = 11.sp, color = HdAzure, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons
                Button(
                    onClick = {
                        if (selectedTimes in 1..remaining) {
                            onConfirmSweep(selectedTimes)
                            onDismiss()
                        }
                    },
                    enabled = remaining > 0 && selectedTimes > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("confirm_sweep_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HdPurplePrimary,
                        contentColor = HdTextWhite
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (remaining > 0) "开启神识扫荡 ($selectedTimes 次)" else "今日扫荡次数已耗尽", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SweepResultDialog(
    result: SweepRewardResult?,
    onDismiss: () -> Unit
) {
    val res = result ?: return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = HdSurface),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, HdGoldPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crest
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(HdGoldPrimary.copy(alpha = 0.18f))
                        .border(1.5.dp, HdGoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = HdGoldPrimary, modifier = Modifier.size(30.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "神游太虚 · 扫荡大捷",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HdGoldPrimary
                )

                Text(
                    text = "于【${res.mapName}】神识出窍 连续扫荡 ${res.times} 次",
                    fontSize = 12.sp,
                    color = HdTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "💎 灵石入账", fontSize = 13.sp, color = HdTextPrimary, fontWeight = FontWeight.Medium)
                            Text(text = "+${res.totalStones} 灵石", fontSize = 14.sp, color = HdGoldPrimary, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "✨ 修为精进", fontSize = 13.sp, color = HdTextPrimary, fontWeight = FontWeight.Medium)
                            Text(text = "+${res.totalExp} 点修为", fontSize = 14.sp, color = HdJade, fontWeight = FontWeight.Bold)
                        }

                        if (res.herbsGained.isNotEmpty() || res.oresGained.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "🎒 缴获战利天材地宝:", fontSize = 12.sp, color = HdTextSecondary, fontWeight = FontWeight.SemiBold)

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                res.herbsGained.forEach { (herbId, count) ->
                                    val item = ItemCatalog.getItem(herbId)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(HdJade.copy(alpha = 0.15f))
                                            .border(1.dp, HdJade.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "${item.name} ×$count", fontSize = 11.sp, color = HdJade, fontWeight = FontWeight.Medium)
                                    }
                                }

                                res.oresGained.forEach { (oreId, count) ->
                                    val item = ItemCatalog.getItem(oreId)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(HdAzure.copy(alpha = 0.15f))
                                            .border(1.dp, HdAzure.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "${item.name} ×$count", fontSize = 11.sp, color = HdAzure, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("dismiss_sweep_result_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HdPurplePrimary,
                        contentColor = HdTextWhite
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(text = "收纳归戒 · 凯旋", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
