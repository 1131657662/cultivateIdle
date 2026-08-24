package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CaveAbodeEntity
import com.example.data.db.CultivationLogEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.BodyRealmCatalog
import com.example.model.CompanionCatalog
import com.example.model.ElementType
import com.example.model.RealmCatalog
import com.example.model.SectCatalog
import com.example.model.SectRank
import com.example.model.SpiritualRoot
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBackground
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdBorderStrong
import com.example.ui.theme.HdConsoleAlert
import com.example.ui.theme.HdConsoleBackground
import com.example.ui.theme.HdConsoleContent
import com.example.ui.theme.HdConsoleHeader
import com.example.ui.theme.HdConsoleMuted
import com.example.ui.theme.HdCrimson
import com.example.ui.theme.HdCrimsonLight
import com.example.ui.theme.HdGoldPrimary
import com.example.ui.theme.HdJade
import com.example.ui.theme.HdPurpleContainer
import com.example.ui.theme.HdPurpleOnContainer
import com.example.ui.theme.HdPurplePrimary
import com.example.ui.theme.HdSurface
import com.example.ui.theme.HdSurfaceElevated
import com.example.ui.theme.HdSurfaceVariant
import com.example.ui.theme.HdTextMuted
import com.example.ui.theme.HdTextPrimary
import com.example.ui.theme.HdTextSecondary
import com.example.ui.theme.HdTextWhite
import com.example.viewmodel.PlayerStats

@Composable
fun CultivationScreen(
    profile: PlayerProfileEntity?,
    caveAbode: CaveAbodeEntity?,
    stats: PlayerStats,
    logs: List<CultivationLogEntity>,
    reqExp: Long,
    reqBodyExp: Long,
    onOpenTribulation: () -> Unit,
    onBodyBreakthrough: () -> Unit,
    onUpgradeRoot: (ElementType) -> Unit,
    onUpgradeArray: () -> Unit,
    onOpenRealmGuide: ((tab: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return
    val currentRealm = RealmCatalog.getRealm(p.realmId)
    val bodyRealm = BodyRealmCatalog.getBodyRealm(p.bodyRealmId)
    val sect = SectCatalog.getSect(p.currentSectId)
    val sectRank = try { SectRank.valueOf(p.currentSectRank).title } catch (e: Exception) { "外门弟子" }

    var showStatsDetails by remember { mutableStateOf(false) }
    var selectedRootTab by remember { mutableStateOf(ElementType.METAL) }

    val expProgress = if (reqExp > 0) (p.currentExp.toFloat() / reqExp.toFloat()).coerceIn(0f, 1f) else 1f
    val isExpFull = p.currentExp >= reqExp

    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. High Density Main Cultivation & Breakthrough Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorderStrong)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Speed Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Training Room Booster Badge
                        if (p.trainingRoomSeconds > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdCrimson.copy(alpha = 0.15f))
                                    .border(1.dp, HdCrimson, RoundedCornerShape(100.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = HdCrimson,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "练功房 5倍加速中 (${p.trainingRoomSeconds}s)",
                                        color = HdCrimson,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdPurpleContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${sect.name} · $sectRank",
                                    color = HdPurpleOnContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Cultivation Rate Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(HdJade.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = HdJade,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "+${stats.cultivationRatePerSec}/s",
                                    color = HdJade,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cultivation Realm Centerpiece Circle (Clickable to view Realm Encyclopedia)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(148.dp)
                            .clip(CircleShape)
                            .clickable { onOpenRealmGuide?.invoke(0) }
                            .testTag("cultivation_realm_centerpiece")
                    ) {
                        // Outer Qi Aura Halo
                        Box(
                            modifier = Modifier
                                .size((140 * pulseScale).dp)
                                .clip(CircleShape)
                                .background(
                                    if (isExpFull) HdCrimson.copy(alpha = 0.12f)
                                    else HdPurplePrimary.copy(alpha = 0.08f)
                                )
                        )

                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(136.dp),
                            color = HdBorder,
                            strokeWidth = 6.dp,
                            trackColor = Color.Transparent
                        )
                        CircularProgressIndicator(
                            progress = { expProgress },
                            modifier = Modifier.size(136.dp),
                            color = if (isExpFull) HdCrimson else HdPurplePrimary,
                            strokeWidth = 6.dp,
                            trackColor = Color.Transparent
                        )

                        // Core Daoist Seal
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.scale(if (isExpFull) pulseScale else 1.0f)
                        ) {
                            Text(
                                text = currentRealm.name,
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${p.realmStage} 阶",
                                color = HdPurplePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${(expProgress * 100).toInt()}%",
                                color = HdTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Realm Encyclopedia Quick Link
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { onOpenRealmGuide?.invoke(0) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .testTag("view_all_realms_link"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = HdPurplePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "点击查看境界全鉴 (16大境界) >",
                            color = HdPurplePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Exp Progress Numerical bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "当前修为",
                            color = HdTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${p.currentExp} / $reqExp",
                            color = if (isExpFull) HdJade else HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { expProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isExpFull) HdJade else HdPurplePrimary,
                        trackColor = HdSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakthrough / Tribulation Button
                    Button(
                        onClick = onOpenTribulation,
                        enabled = isExpFull,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("breakthrough_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExpFull) HdPurplePrimary else HdBorder,
                            contentColor = if (isExpFull) HdTextWhite else HdTextMuted
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (isExpFull) HdTextWhite else HdTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExpFull) "引动天地灵气 渡劫破境" else "潜心吐纳 修为蓄力中 (${(expProgress * 100).toInt()}%)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. Body Refining (肉身淬体) & Daoist Companion (道侣仙缘)
        item {
            val nextBodyId = if (p.bodyRealmStage >= bodyRealm.maxStage) p.bodyRealmId + 1 else p.bodyRealmId
            val nextBodyStage = if (p.bodyRealmStage >= bodyRealm.maxStage) 1 else p.bodyRealmStage + 1
            val (cultMet, cultMsg) = BodyRealmCatalog.checkCultivationRequirement(
                targetBodyId = nextBodyId,
                targetBodyStage = nextBodyStage,
                currentRealmId = p.realmId,
                currentRealmStage = p.realmStage
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenRealmGuide?.invoke(1) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "肉身",
                                tint = HdCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "肉身淬体 · 混元金身",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(HdCrimson.copy(alpha = 0.12f))
                                .clickable { onOpenRealmGuide?.invoke(1) }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .testTag("body_realm_badge")
                        ) {
                            Text(
                                text = "${bodyRealm.name} ${p.bodyRealmStage}阶 >",
                                color = HdCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "肉身极境加成: 气血 +${bodyRealm.hpBonus * p.bodyRealmStage}, 攻击 +${bodyRealm.atkBonus * p.bodyRealmStage}, 防御 +${bodyRealm.defBonus * p.bodyRealmStage}",
                        color = HdTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cultivation requirement badge for body breakthrough
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (cultMet) HdJade.copy(alpha = 0.08f) else HdCrimson.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (cultMet) Icons.Default.LocalFireDepartment else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (cultMet) HdJade else HdCrimson,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (cultMet) "真元境界达标: $cultMsg" else "真元境界不足: $cultMsg",
                                color = if (cultMet) HdJade else HdCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val bodyProgress = if (reqBodyExp > 0) (p.currentExp.toFloat() / reqBodyExp.toFloat()).coerceIn(0f, 1f) else 1f
                    val isBodyFull = p.currentExp >= reqBodyExp

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "消耗修为淬炼", color = HdTextMuted, fontSize = 11.sp)
                        Text(text = "${p.currentExp} / $reqBodyExp", color = HdTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { bodyProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HdCrimson,
                        trackColor = HdSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onBodyBreakthrough,
                        enabled = isBodyFull && cultMet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("body_breakthrough_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBodyFull && cultMet) HdCrimson else HdBorder,
                            contentColor = if (isBodyFull && cultMet) HdTextWhite else HdTextMuted
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = if (!cultMet) "真元不足 ($cultMsg)"
                            else if (isBodyFull) "淬炼筋骨 突破肉身"
                            else "修为不足以淬体",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3. Five Spiritual Roots (五行灵根) & Spirit Array (聚灵阵)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = "灵根",
                                tint = HdJade,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "五行灵根 · 聚灵大阵",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "灵气: ${p.spiritQi}/${p.spiritQiMax}",
                            color = HdJade,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Elements Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ElementType.entries.forEach { elem ->
                            val isSelected = selectedRootTab == elem
                            val lvl = when (elem) {
                                ElementType.METAL -> p.rootMetalLevel
                                ElementType.WOOD -> p.rootWoodLevel
                                ElementType.WATER -> p.rootWaterLevel
                                ElementType.FIRE -> p.rootFireLevel
                                ElementType.EARTH -> p.rootEarthLevel
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) HdPurpleContainer else HdSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) HdPurplePrimary else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedRootTab = elem }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = elem.displayName.substring(0, 1),
                                        color = if (isSelected) HdPurpleOnContainer else HdTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${lvl}阶",
                                        color = if (isSelected) HdPurplePrimary else HdTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected Root Detail Card
                    val curLvl = when (selectedRootTab) {
                        ElementType.METAL -> p.rootMetalLevel
                        ElementType.WOOD -> p.rootWoodLevel
                        ElementType.WATER -> p.rootWaterLevel
                        ElementType.FIRE -> p.rootFireLevel
                        ElementType.EARTH -> p.rootEarthLevel
                    }
                    val rootObj = SpiritualRoot(selectedRootTab, curLvl)
                    val upgradeCost = rootObj.getUpgradeCost()
                    val canUpgradeRoot = p.spiritQi >= upgradeCost

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(HdSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${selectedRootTab.displayName}【${rootObj.getTierName()}】",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "对应功法威能加成 +${((rootObj.getStatBonus() - 1.0) * 100).toInt()}%",
                                color = HdTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { onUpgradeRoot(selectedRootTab) },
                            enabled = canUpgradeRoot,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canUpgradeRoot) HdJade else HdBorder,
                                contentColor = if (canUpgradeRoot) HdTextWhite else HdTextMuted
                            ),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("upgrade_root_button")
                        ) {
                            Text(
                                text = if (canUpgradeRoot) "升阶 (-$upgradeCost 灵气)" else "缺灵气 ($upgradeCost)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Spirit Array Upgrade Row
                    val cave = caveAbode ?: CaveAbodeEntity()
                    val reqWood = (p.spiritArrayLevel * 200L)
                    val reqStone = (p.spiritArrayLevel * 100L)
                    val canUpgradeArray = cave.wood >= reqWood && cave.stone >= reqStone && p.currentExp >= (p.spiritArrayLevel * 300L)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(HdSurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "聚灵阵【${p.spiritArrayLevel}阶】",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "需消耗: ${reqWood}木材, ${reqStone}玄石",
                                color = HdTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onUpgradeArray,
                            enabled = canUpgradeArray,
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("upgrade_array_button")
                        ) {
                            Text(
                                text = if (canUpgradeArray) "聚灵阵升阶" else "材料不足",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Detailed Player Attributes Accordion
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStatsDetails = !showStatsDetails }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = HdPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "道友真身全属性明细",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Icon(
                            imageVector = if (showStatsDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = HdTextMuted
                        )
                    }

                    if (showStatsDetails) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                StatDetailItem("气血上限", "${stats.maxHp}")
                                StatDetailItem("真元攻击", "${stats.attack}")
                                StatDetailItem("护体防御", "${stats.defense}")
                                StatDetailItem("修真声望", "${p.reputation}")
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                StatDetailItem("暴击几率", "${(stats.critRate * 100).toInt()}%")
                                StatDetailItem("闪避几率", "${(stats.dodgeRate * 100).toInt()}%")
                                StatDetailItem("丹药加成气血", "+${p.totalPillHp}")
                                StatDetailItem("丹药加成攻防", "攻+${p.totalPillAtk} 防+${p.totalPillDef}")
                            }
                        }
                    }
                }
            }
        }

        // 5. Cultivation Logs Console
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HdConsoleBackground),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorderStrong)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "机缘日志",
                                tint = HdConsoleHeader,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "天道机缘 · 修仙感悟",
                                color = HdConsoleHeader,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "最新50条",
                            color = HdConsoleMuted,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (logs.isEmpty()) {
                            Text(
                                text = "心无杂念，万象归一，打坐冥想中...",
                                color = HdConsoleMuted,
                                fontSize = 11.sp
                            )
                        } else {
                            logs.take(6).forEach { log ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "• ",
                                        color = when (log.type) {
                                            "TRIBULATION" -> HdConsoleAlert
                                            "BREAKTHROUGH" -> HdJade
                                            "ENCOUNTER" -> HdGoldPrimary
                                            "COMPANION" -> HdCrimsonLight
                                            else -> HdConsoleContent
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = log.content,
                                        color = HdConsoleContent,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = HdTextSecondary, fontSize = 12.sp)
        Text(text = value, color = HdTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
