package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.PlayerProfileEntity
import com.example.model.AdventureHelper
import com.example.model.AdventureMap
import com.example.model.ItemCatalog
import com.example.model.MapCatalog
import com.example.model.MarketCatalog
import com.example.model.MarketItem
import com.example.model.RealmCatalog
import com.example.ui.components.SweepDialog
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
import com.example.viewmodel.ActiveAdventureState
import com.example.viewmodel.PlayerStats

@Composable
fun AdventureScreen(
    profile: PlayerProfileEntity?,
    stats: PlayerStats,
    adventureState: ActiveAdventureState,
    onStartAdventure: (Int) -> Unit,
    onStepAdventure: () -> Unit,
    onExecuteCombatTurn: () -> Unit,
    onSkipCombat: () -> Unit,
    onToggleAutoExplore: () -> Unit,
    onSetBattleSpeed: (Float) -> Unit,
    onRetreat: () -> Unit,
    onResetCheckpoint: (Int) -> Unit,
    onFinishCombat: () -> Unit,
    onSweepMap: (mapId: Int, times: Int) -> Unit,
    onBuyExtraSweeps: () -> Unit,
    onToggleAutoSweep: (Int) -> Unit,
    onBuyMarketItem: (MarketItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return

    // If currently exploring or fighting inside a map, switch completely to the dedicated combat screen!
    if (adventureState.mapId > 0) {
        AdventureCombatScreen(
            profile = p,
            stats = stats,
            adventureState = adventureState,
            onStepAdventure = onStepAdventure,
            onExecuteCombatTurn = onExecuteCombatTurn,
            onSkipCombat = onSkipCombat,
            onToggleAutoExplore = onToggleAutoExplore,
            onSetBattleSpeed = onSetBattleSpeed,
            onRetreat = onRetreat,
            onResetCheckpoint = onResetCheckpoint,
            onFinishCombat = onFinishCombat,
            modifier = modifier
        )
        return
    }

    var mainTab by remember { mutableIntStateOf(0) } // 0: 秘境历练, 1: 仙坊鬼市
    var marketFloorTab by remember { mutableIntStateOf(1) } // 1: 凡人坊市, 2: 鬼市地摊, 3: 仙界奇珍

    var selectedMapForSweep by remember { mutableStateOf<AdventureMap?>(null) }

    val maxDailySweeps = AdventureHelper.calculateMaxDailySweeps(p.realmId, p.extraSweepPurchasedToday)
    val remainingSweeps = maxOf(0, maxDailySweeps - p.dailySweepUsed)
    val checkpoints = AdventureHelper.parseCheckpoints(p.mapCheckpointsStr)
    val clearedMaps = AdventureHelper.parseClearedMaps(p.clearedMapsStr)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Main Category Tab Switch (历练 / 仙坊)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (mainTab == 0) HdPurplePrimary else HdSurface)
                        .border(1.dp, if (mainTab == 0) HdPurplePrimary else HdBorder, RoundedCornerShape(12.dp))
                        .clickable { mainTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = if (mainTab == 0) HdTextWhite else HdTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "秘境历练 · 斩妖除魔",
                            color = if (mainTab == 0) HdTextWhite else HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (mainTab == 1) HdPurplePrimary else HdSurface)
                        .border(1.dp, if (mainTab == 1) HdPurplePrimary else HdBorder, RoundedCornerShape(12.dp))
                        .clickable { mainTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = if (mainTab == 1) HdTextWhite else HdTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "仙坊鬼市 · 藏宝阁",
                            color = if (mainTab == 1) HdTextWhite else HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (mainTab == 0) {
            // ================= 秘境历练 =================
            // Hero Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_adventure_map),
                            contentDescription = "九州秘境",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            HdSurface.copy(alpha = 0.6f),
                                            HdSurface
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "九州秘境 · 寻幽夺宝",
                                    color = HdTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "诛杀万界妖王 · 夺天地灵脉造化",
                                    color = HdTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Top Daily Sweep Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HdGoldPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(HdGoldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = HdGoldPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "今日神游令: $remainingSweeps / $maxDailySweeps 次",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (remainingSweeps > 0) HdJade else HdCrimson
                                )
                                Text(
                                    text = "通关秘境即可一键出窍神游扫荡",
                                    fontSize = 10.sp,
                                    color = HdTextSecondary
                                )
                            }
                        }

                        if (p.extraSweepPurchasedToday < 3) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdGoldPrimary.copy(alpha = 0.18f))
                                    .clickable { onBuyExtraSweeps() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = HdGoldPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "+10次", fontSize = 10.sp, color = HdGoldPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            items(MapCatalog.maps) { map ->
                val isUnlocked = p.realmId >= map.reqRealmId
                val isSweeping = p.isAutoSweeping && p.autoSweepMapId == map.id
                val reqRealmName = RealmCatalog.getRealm(map.reqRealmId).name
                val checkpointStep = checkpoints[map.id]
                val hasCheckpoint = checkpointStep != null && checkpointStep > 1
                val isCleared = clearedMaps.contains(map.id) || p.maxClearedMapId > map.id

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSweeping) HdCrimson else if (hasCheckpoint) HdGoldPrimary.copy(alpha = 0.8f) else HdBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Title & Status Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = map.name,
                                    color = if (isUnlocked) HdTextPrimary else HdTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (map.isImmortalMap) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(HdGoldPrimary.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "仙界秘境", color = HdGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (isCleared) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(HdJade.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "已通关", color = HdJade, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (hasCheckpoint) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(HdGoldPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "📍 进度: 第 $checkpointStep 步", color = HdGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = if (isUnlocked) "探索 ${map.stepCount} 步" else "需【$reqRealmName】",
                                    color = if (isUnlocked) HdTextSecondary else HdTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = map.description, color = HdTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "妖王: ${map.enemyName} · 掉落: 灵石 ${map.stoneRewardMin}~${map.stoneRewardMax}, 修为 +${map.expReward}",
                            color = HdJade,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Primary Explore Button
                            Button(
                                onClick = { onStartAdventure(map.id) },
                                enabled = isUnlocked,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(36.dp)
                                    .testTag("start_adventure_${map.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isUnlocked) HdPurplePrimary else HdBorder,
                                    contentColor = if (isUnlocked) HdTextWhite else HdTextMuted
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = if (!isUnlocked) "境界未达" else if (hasCheckpoint) "继续历练 (第 $checkpointStep 步)" else "御剑历练",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Sweep Button (if unlocked & cleared)
                            if (isCleared) {
                                OutlinedButton(
                                    onClick = { selectedMapForSweep = map },
                                    enabled = isUnlocked && remainingSweeps > 0,
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("sweep_map_${map.id}"),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = HdGoldPrimary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "神游扫荡", color = HdGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Reset checkpoint button if has checkpoint
                            if (hasCheckpoint) {
                                OutlinedButton(
                                    onClick = { onResetCheckpoint(map.id) },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "重置进度", modifier = Modifier.size(13.dp), tint = HdTextMuted)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ================= 仙坊鬼市 =================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloorPill("一层 · 凡人坊市", 1, marketFloorTab == 1) { marketFloorTab = 1 }
                    FloorPill("二层 · 鬼市地摊", 2, marketFloorTab == 2) { marketFloorTab = 2 }
                    FloorPill("三层 · 仙界奇珍", 3, marketFloorTab == 3) { marketFloorTab = 3 }
                }
            }

            val floorItems = MarketCatalog.items.filter { it.floor == marketFloorTab }

            items(floorItems) { mItem ->
                val item = ItemCatalog.getItem(mItem.itemId)
                val canAfford = p.spiritStones >= mItem.priceStones
                val isRealmOk = p.realmId >= mItem.reqRealmId
                val reqRealmName = RealmCatalog.getRealm(mItem.reqRealmId).name

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = HdTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(HdPurpleContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = item.type.title, color = HdPurpleOnContainer, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = item.description, color = HdTextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "需求境界: 【$reqRealmName】",
                                color = if (isRealmOk) HdTextMuted else HdCrimson,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = { onBuyMarketItem(mItem) },
                            enabled = canAfford && isRealmOk,
                            modifier = Modifier.height(34.dp).testTag("buy_market_${mItem.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford && isRealmOk) HdGoldPrimary else HdBorder,
                                contentColor = if (canAfford && isRealmOk) HdTextWhite else HdTextMuted
                            ),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(text = "${mItem.priceStones} 灵石", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Sweep Dialog Modal
    selectedMapForSweep?.let { map ->
        SweepDialog(
            map = map,
            profile = p,
            onConfirmSweep = { times ->
                onSweepMap(map.id, times)
                selectedMapForSweep = null
            },
            onBuyExtraSweeps = onBuyExtraSweeps,
            onDismiss = { selectedMapForSweep = null }
        )
    }
}

@Composable
private fun FloorPill(title: String, floor: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) HdPurplePrimary else HdSurface)
            .border(1.dp, if (isSelected) HdPurplePrimary else HdBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) HdTextWhite else HdTextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}
