package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PlayerProfileEntity
import com.example.model.ItemCatalog
import com.example.model.MapCatalog
import com.example.model.RealmCatalog
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBackground
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdConsoleBackground
import com.example.ui.theme.HdConsoleContent
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
fun AdventureCombatScreen(
    profile: PlayerProfileEntity?,
    stats: PlayerStats,
    adventureState: ActiveAdventureState,
    onStepAdventure: () -> Unit,
    onExecuteCombatTurn: () -> Unit,
    onSkipCombat: () -> Unit,
    onToggleAutoExplore: () -> Unit,
    onSetBattleSpeed: (Float) -> Unit,
    onRetreat: () -> Unit,
    onResetCheckpoint: (mapId: Int) -> Unit,
    onFinishCombat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return
    val map = MapCatalog.getMap(adventureState.mapId)
    val realmName = RealmCatalog.getRealm(p.realmId).name

    var showRetreatDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val logListState = rememberLazyListState()

    // Auto-scroll combat logs
    LaunchedEffect(adventureState.combatLogs.size) {
        if (adventureState.combatLogs.isNotEmpty()) {
            logListState.animateScrollToItem(adventureState.combatLogs.size - 1)
        }
    }

    val pProgress = if (adventureState.playerMaxHp > 0) {
        (adventureState.playerCurrentHp.toFloat() / adventureState.playerMaxHp.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val eProgress = if (adventureState.enemyMaxHp > 0) {
        (adventureState.enemyCurrentHp.toFloat() / adventureState.enemyMaxHp.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val stepProgress = (adventureState.currentStep.toFloat() / adventureState.maxSteps.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // ================= TOP BAR =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (adventureState.combatFinished) onFinishCombat()
                        else showRetreatDialog = true
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HdSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = HdTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = map.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = HdTextPrimary
                        )
                        if (map.isImmortalMap) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdGoldPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("仙界", color = HdGoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        text = if (adventureState.inCombat) "⚔️ 遭遇战 · 守关妖王" else "📍 秘境探险中",
                        fontSize = 11.sp,
                        color = if (adventureState.inCombat) HdCrimson else HdJade
                    )
                }
            }

            // Battle speed multiplier switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (adventureState.battleSpeed == 1.0f) HdPurplePrimary else HdSurfaceVariant)
                        .clickable { onSetBattleSpeed(1.0f) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("1.0x", fontSize = 11.sp, color = if (adventureState.battleSpeed == 1.0f) HdTextWhite else HdTextSecondary, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (adventureState.battleSpeed == 2.0f) HdPurplePrimary else HdSurfaceVariant)
                        .clickable { onSetBattleSpeed(2.0f) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("2.0x", fontSize = 11.sp, color = if (adventureState.battleSpeed == 2.0f) HdTextWhite else HdTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ================= EXPLORATION STEP TRACKER =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HdSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = HdPurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "探索进度: 第 ${adventureState.currentStep} / ${adventureState.maxSteps} 步",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = HdTextPrimary
                        )
                    }

                    if (adventureState.resumedFromStep > 1) {
                        Text(
                            text = "📍 继上次存档",
                            fontSize = 10.sp,
                            color = HdGoldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { stepProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = HdPurplePrimary,
                    trackColor = HdSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ================= BATTLE ARENA (VS CARDS) =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Player Card (Left)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdJade.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HdJade.copy(alpha = 0.15f))
                            .border(1.dp, HdJade, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = HdJade, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = p.daoistName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HdTextPrimary)
                    Text(text = realmName, fontSize = 10.sp, color = HdTextSecondary)

                    Spacer(modifier = Modifier.height(6.dp))

                    // HP Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("气血", fontSize = 10.sp, color = HdTextMuted)
                        Text("${adventureState.playerCurrentHp}/${adventureState.playerMaxHp}", fontSize = 10.sp, color = HdJade, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { pProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HdJade,
                        trackColor = HdSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("攻: ${stats.attack}", fontSize = 9.sp, color = HdTextSecondary)
                        Text("防: ${stats.defense}", fontSize = 9.sp, color = HdTextSecondary)
                    }
                }
            }

            // VS Emblem (Center)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(HdGoldPrimary.copy(alpha = 0.2f))
                    .border(1.dp, HdGoldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "VS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = HdGoldPrimary)
            }

            // Enemy Card (Right)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdCrimson.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HdCrimson.copy(alpha = 0.15f))
                            .border(1.dp, HdCrimson, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = HdCrimson, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = adventureState.enemyName.ifEmpty { "秘境妖兽" }, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HdTextPrimary)
                    Text(text = adventureState.enemyTitle, fontSize = 10.sp, color = HdCrimson)

                    Spacer(modifier = Modifier.height(6.dp))

                    // HP Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("妖力", fontSize = 10.sp, color = HdTextMuted)
                        Text("${adventureState.enemyCurrentHp}/${adventureState.enemyMaxHp}", fontSize = 10.sp, color = HdCrimson, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { eProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HdCrimson,
                        trackColor = HdSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("攻: ${adventureState.enemyAtk}", fontSize = 9.sp, color = HdTextSecondary)
                        Text("防: ${adventureState.enemyDef}", fontSize = 9.sp, color = HdTextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ================= COMBAT LOGS TERMINAL =================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = HdConsoleBackground),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Terminal Header / Event Message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (adventureState.inCombat) HdCrimson else HdJade)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = adventureState.eventMessage,
                        color = HdGoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Waterfall Combat Log
                LazyColumn(
                    state = logListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(adventureState.combatLogs) { log ->
                        val textColor = when {
                            log.attacker == "天道" -> HdPurplePrimary
                            log.isCrit -> HdGoldPrimary
                            log.isDodge -> HdAzure
                            log.attacker == "道友" -> HdJade
                            else -> HdConsoleContent
                        }
                        Text(
                            text = log.message,
                            color = textColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ================= BOTTOM ACTION CONTROL PANEL =================
        if (adventureState.combatFinished) {
            // Victory or Defeat Settlement Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (adventureState.combatVictory) HdJade.copy(alpha = 0.15f) else HdCrimson.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (adventureState.combatVictory) HdJade else HdCrimson
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (adventureState.combatVictory) {
                        Text(
                            text = "🎉 斩妖除魔 · 秘境通关大捷！",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = HdJade
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "获得灵石 +${adventureState.lootStones} · 修为 +${adventureState.lootExp}",
                            fontSize = 12.sp,
                            color = HdTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onFinishCombat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("finish_combat_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = HdJade, contentColor = HdTextWhite),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text("清点战利品 · 凯旋回山", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else {
                        Text(
                            text = "💀 道法不敌 · 败走回山",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = HdCrimson
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "虽败犹荣！已为您保留当前探索进度（第 ${adventureState.currentStep} 步），修整归来可直接继续挑战！",
                            fontSize = 11.sp,
                            color = HdTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onFinishCombat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("finish_combat_defeat_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = HdPurplePrimary, contentColor = HdTextWhite),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text("返回秘境列表 (保留进度)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        } else if (adventureState.inCombat) {
            // In Combat actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSkipCombat,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("skip_combat_button"),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = HdGoldPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("跳过战斗", fontSize = 12.sp, color = HdGoldPrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onExecuteCombatTurn,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("attack_turn_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = HdCrimson, contentColor = HdTextWhite),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("极速决战", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Exploration actions (Step by step)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Auto explore button
                    OutlinedButton(
                        onClick = onToggleAutoExplore,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("auto_explore_button"),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(
                            imageVector = if (adventureState.isAutoExploring) Icons.Default.Dangerous else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (adventureState.isAutoExploring) HdCrimson else HdPurplePrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (adventureState.isAutoExploring) "停止自动" else "自动寻幽",
                            fontSize = 12.sp,
                            color = if (adventureState.isAutoExploring) HdCrimson else HdPurplePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Next Step button
                    Button(
                        onClick = onStepAdventure,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .testTag("step_adventure_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = HdPurplePrimary, contentColor = HdTextWhite),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("御剑探步 (下一步)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Secondary actions: Retreat / Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showRetreatDialog = true }) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(14.dp), tint = HdTextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("暂离秘境 (进度已自动保存)", fontSize = 11.sp, color = HdTextSecondary)
                    }

                    TextButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = HdTextMuted)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("重头探索", fontSize = 11.sp, color = HdTextMuted)
                    }
                }
            }
        }
    }

    // Retreat Confirmation Dialog
    if (showRetreatDialog) {
        AlertDialog(
            onDismissRequest = { showRetreatDialog = false },
            title = { Text("暂退秘境回山") },
            text = {
                Text(
                    "道友当前在【${map.name}】已探索至【第 ${adventureState.currentStep} 步】。\n\n退出后，您的探索进度将完好保存于玉简中。下次再次进入本秘境时，可直接从此处继续历练，无需从头开始！"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRetreatDialog = false
                        onRetreat()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HdPurplePrimary)
                ) {
                    Text("确认暂离 (保存进度)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRetreatDialog = false }) {
                    Text("继续探寻")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("重头探索秘境") },
            text = {
                Text("是否放弃当前【第 ${adventureState.currentStep} 步】的探索存档，从第 1 步重新开始？")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetCheckpoint(adventureState.mapId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HdCrimson)
                ) {
                    Text("确认重置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
