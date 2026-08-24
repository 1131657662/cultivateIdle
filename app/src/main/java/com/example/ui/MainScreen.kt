package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ItemCatalog
import com.example.ui.components.ImmortalSnackbarHost
import com.example.ui.components.OfflineRewardDialog
import com.example.ui.components.RealmGuideDialog
import com.example.ui.components.SweepResultDialog
import com.example.ui.components.TopBarHeader
import com.example.ui.components.TribulationDialog
import com.example.ui.screens.AdventureScreen
import com.example.ui.screens.AlchemyCraftingScreen
import com.example.ui.screens.AscensionScreen
import com.example.ui.screens.CaveAbodeScreen
import com.example.ui.screens.CultivationScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.SectScreen
import com.example.ui.theme.HdBackground
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdCrimson
import com.example.ui.theme.HdPurpleContainer
import com.example.ui.theme.HdPurpleOnContainer
import com.example.ui.theme.HdPurplePrimary
import com.example.ui.theme.HdSurface
import com.example.ui.theme.HdSurfaceVariant
import com.example.ui.theme.HdTextMuted
import com.example.ui.theme.HdTextPrimary
import com.example.ui.theme.HdTextSecondary
import com.example.ui.theme.HdTextWhite
import com.example.viewmodel.CultivationViewModel
import kotlinx.coroutines.launch

sealed class NavTab(val title: String, val icon: ImageVector, val tag: String) {
    data object Cultivation : NavTab("修真", Icons.Default.LocalFireDepartment, "tab_cultivation")
    data object Cave : NavTab("洞府", Icons.Default.Home, "tab_cave")
    data object Sect : NavTab("门派", Icons.Default.AccountBalance, "tab_sect")
    data object Adventure : NavTab("历练仙坊", Icons.Default.Explore, "tab_adventure")
    data object Alchemy : NavTab("炼丹炼器", Icons.Default.Build, "tab_alchemy")
    data object Inventory : NavTab("百宝道侣", Icons.Default.Backpack, "tab_inventory")
    data object Ascension : NavTab("九重仙界", Icons.Default.FlightTakeoff, "tab_ascension")
}

@Composable
fun MainScreen(viewModel: CultivationViewModel) {
    val profile by viewModel.playerProfile.collectAsStateWithLifecycle()
    val caveAbode by viewModel.caveAbode.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val learnedSkills by viewModel.learnedSkills.collectAsStateWithLifecycle()
    val logs by viewModel.cultivationLogs.collectAsStateWithLifecycle()
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val reqExp by viewModel.reqExp.collectAsStateWithLifecycle()
    val reqBodyExp by viewModel.reqBodyExp.collectAsStateWithLifecycle()
    val tribulationState by viewModel.tribulationState.collectAsStateWithLifecycle()
    val offlineReward by viewModel.offlineReward.collectAsStateWithLifecycle()
    val activeAdventure by viewModel.activeAdventure.collectAsStateWithLifecycle()
    val sweepResult by viewModel.sweepResultState.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var realmGuideInitialTab by remember { mutableStateOf<Int?>(null) }
    val tabs = listOf(
        NavTab.Cultivation,
        NavTab.Cave,
        NavTab.Sect,
        NavTab.Adventure,
        NavTab.Alchemy,
        NavTab.Inventory,
        NavTab.Ascension
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = HdBackground,
        snackbarHost = {},
        topBar = {
            TopBarHeader(
                profile = profile,
                stats = stats,
                onOpenRealmGuide = { tab -> realmGuideInitialTab = tab }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = HdSurfaceVariant,
                contentColor = HdPurplePrimary,
                tonalElevation = 3.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    val isBreakthroughReady = index == 0 && profile != null && profile!!.currentExp >= reqExp
                    val isAscensionReady = index == 6 && profile != null && !profile!!.isAscended && profile!!.realmId >= 9

                    NavigationBarItem(
                        modifier = Modifier.testTag(tab.tag),
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            if (isBreakthroughReady || isAscensionReady) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = if (isBreakthroughReady) HdPurplePrimary else HdCrimson)
                                    }
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) HdPurplePrimary else HdTextSecondary
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) HdPurplePrimary else HdTextSecondary
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) HdPurplePrimary else HdTextMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HdPurplePrimary,
                            selectedTextColor = HdPurplePrimary,
                            unselectedIconColor = HdTextSecondary,
                            unselectedTextColor = HdTextMuted,
                            indicatorColor = HdPurpleContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> CultivationScreen(
                    profile = profile,
                    caveAbode = caveAbode,
                    stats = stats,
                    logs = logs,
                    reqExp = reqExp,
                    reqBodyExp = reqBodyExp,
                    onOpenTribulation = { viewModel.openTribulationDialog() },
                    onBodyBreakthrough = {
                        viewModel.breakthroughBodyRealm { success, message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    },
                    onUpgradeRoot = { element ->
                        val success = viewModel.upgradeSpiritualRoot(element)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "${element.displayName}升阶成功，道法增幅提升！" else "灵气不足以提升灵根")
                        }
                    },
                    onUpgradeArray = {
                        val success = viewModel.upgradeSpiritArray()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "聚灵阵升阶成功！" else "材料或修为不足以升级聚灵阵")
                        }
                    },
                    onOpenRealmGuide = { tab -> realmGuideInitialTab = tab }
                )
                1 -> CaveAbodeScreen(
                    caveAbode = caveAbode,
                    profile = profile,
                    onRecruitServant = {
                        val success = viewModel.recruitServant()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "成功招募一名仙仆道童！" else "灵谷不足，无法招募仙仆")
                        }
                    },
                    onAdjustServant = { type, delta ->
                        viewModel.adjustServant(type, delta)
                    },
                    onChangeGardenPlant = { seedItemId ->
                        viewModel.changeGardenPlant(seedItemId)
                        scope.launch {
                            snackbarHostState.showSnackbar("药园已更替种植【${ItemCatalog.getItem(seedItemId).name}】")
                        }
                    },
                    onFertilizeGarden = {
                        val success = viewModel.fertilizeGarden()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "降下甘霖灵雨，药园仙草生长加速60秒！" else "灵气不足 (需200灵气)")
                        }
                    },
                    onUpgradeSpiritArray = {
                        val success = viewModel.upgradeSpiritArray()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "聚灵阵升阶成功！" else "材料或修为不足以升级聚灵阵")
                        }
                    }
                )
                2 -> SectScreen(
                    profile = profile,
                    learnedSkills = learnedSkills,
                    onJoinSect = { sectId ->
                        val success = viewModel.joinSect(sectId)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "已正式拜入宗门！" else "境界不足，无法拜入")
                        }
                    },
                    onPromoteRank = {
                        val success = viewModel.promoteSectRank()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "宗门职位晋升成功，受宗门敬仰！" else "宗门贡献不足以晋升")
                        }
                    },
                    onClaimSalary = {
                        val success = viewModel.claimDailySalary()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "成功领取今日宗门俸禄！" else "今日俸禄已领过，稍候再来")
                        }
                    },
                    onEnterTrainingRoom = {
                        val success = viewModel.enterTrainingRoom()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "开启 10 分钟 5倍练功房加速！" else "宗门贡献不足 (需 150)")
                        }
                    },
                    onRequestMasterGuidance = {
                        val success = viewModel.requestMasterGuidance()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "掌门传道点拨，修为顿悟瞬增！" else "掌门正在闭关冥想，稍候再来请教")
                        }
                    },
                    onCompleteTask = { task ->
                        val success = viewModel.completeSectTask(task)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "完成宗门任务【${task.title}】，获得丰厚贡献与灵石！" else "职位不足以执行此任务")
                        }
                    },
                    onLearnSkill = { skillId ->
                        val success = viewModel.learnSkill(skillId)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "成功参悟此门无上心法！" else "贡献不足或已参悟")
                        }
                    },
                    onLeaveSect = {
                        val success = viewModel.leaveSect()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "已叛出宗门，重归散修逍遥派！" else "声望不足以叛门")
                        }
                    }
                )
                3 -> AdventureScreen(
                    profile = profile,
                    stats = stats,
                    adventureState = activeAdventure,
                    onStartAdventure = { mapId ->
                        viewModel.startAdventure(mapId)
                    },
                    onStepAdventure = {
                        viewModel.stepAdventure()
                    },
                    onExecuteCombatTurn = {
                        viewModel.executeCombat()
                    },
                    onSkipCombat = {
                        viewModel.skipCombat()
                    },
                    onToggleAutoExplore = {
                        viewModel.toggleAutoExplore()
                    },
                    onSetBattleSpeed = { speed ->
                        viewModel.setBattleSpeed(speed)
                    },
                    onRetreat = {
                        viewModel.retreatAdventure()
                    },
                    onResetCheckpoint = { mapId ->
                        viewModel.resetMapCheckpoint(mapId)
                        scope.launch { snackbarHostState.showSnackbar("已重置该秘境探索进度") }
                    },
                    onFinishCombat = {
                        viewModel.finishCombat()
                    },
                    onSweepMap = { mapId, times ->
                        val success = viewModel.sweepMap(mapId, times)
                        if (!success) {
                            scope.launch { snackbarHostState.showSnackbar("扫荡失败：今日神游次数不足或尚未通关") }
                        }
                    },
                    onBuyExtraSweeps = {
                        val success = viewModel.buyExtraDailySweeps()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "消耗 200 灵石，补充 10 次神游扫荡令！" else "灵石不足200或今日购买次数已达上限")
                        }
                    },
                    onToggleAutoSweep = { mapId ->
                        val isSweeping = viewModel.toggleAutoSweep(mapId)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (isSweeping) "神识出窍，已开启自动神游挂机！" else "已停止神识神游")
                        }
                    },
                    onBuyMarketItem = { marketItem ->
                        val success = viewModel.buyMarketItem(marketItem)
                        scope.launch {
                            val it = ItemCatalog.getItem(marketItem.itemId)
                            snackbarHostState.showSnackbar(if (success) "成功购买【${it.name}】！" else "灵石不足或境界未达")
                        }
                    }
                )
                4 -> AlchemyCraftingScreen(
                    profile = profile,
                    caveAbode = caveAbode,
                    inventory = inventory,
                    onCraftAlchemy = { recipe ->
                        val success = viewModel.craftAlchemy(recipe)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "三昧真火开炉，成功炼出【${recipe.name}】！" else "药材、木材或灵石不足")
                        }
                    },
                    onCraftArtifact = { recipe ->
                        val success = viewModel.craftArtifact(recipe)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "紫霄神鼎出世，成功锻造【${recipe.name}】！" else "矿石、陨铁或灵石不足")
                        }
                    }
                )
                5 -> InventoryScreen(
                    profile = profile,
                    inventory = inventory,
                    onUseItem = { invItem ->
                        val item = ItemCatalog.getItem(invItem.itemId)
                        val msg = viewModel.useItem(item)
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    },
                    onEquipItem = { invItem ->
                        val item = ItemCatalog.getItem(invItem.itemId)
                        val msg = viewModel.equipItem(item)
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    },
                    onUnequipItem = { slot ->
                        viewModel.unequipItem(slot)
                        scope.launch { snackbarHostState.showSnackbar("已卸下法宝") }
                    },
                    onSellItem = { invItem ->
                        val item = ItemCatalog.getItem(invItem.itemId)
                        val msg = viewModel.sellItem(item)
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    },
                    onMeetCompanion = { compId ->
                        val success = viewModel.meetCompanion(compId)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "成功结识道友，相伴漫漫仙途！" else "境界不足以结交")
                        }
                    },
                    onGiftCompanion = { compId ->
                        val success = viewModel.giftCompanion(compId)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "赠礼成功，道友亲密度大增！" else "灵石不足500")
                        }
                    },
                    onDualCultivate = { compId ->
                        val success = viewModel.dualCultivate(compId)
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "道侣双修圆满，获得巨额修为与灵气反哺！" else "今日双修已毕，心神需稍作休整")
                        }
                    }
                )
                6 -> AscensionScreen(
                    profile = profile,
                    stats = stats,
                    onAscend = { pathway ->
                        val success = viewModel.ascend(pathway)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (success) "🎉 恭喜道友白日飞升，羽化登仙，位列三十三天仙班！"
                                else "道行未臻大乘极境，无法叩关飞升"
                            )
                        }
                    },
                    onExchangeCrystals = {
                        val success = viewModel.exchangeCrystals()
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) "凝练完成，灵石已转化为极品仙晶！" else "灵石不足10000")
                        }
                    }
                )
            }

            // Immortal Floating Toast Notification (Non-intrusive, ethereal aesthetic)
            ImmortalSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .zIndex(100f)
            )
        }

        // Tribulation Dialog
        TribulationDialog(
            profile = profile,
            tribulationState = tribulationState,
            onPillCountChange = { count -> viewModel.adjustTribulationPills(count) },
            onExecuteTribulation = { viewModel.executeTribulation() },
            onDismiss = { viewModel.closeTribulationDialog() }
        )

        // Offline Gains Dialog
        OfflineRewardDialog(
            reward = offlineReward,
            onClaim = { viewModel.claimOfflineReward() }
        )

        // Sweep Result Settlement Dialog
        SweepResultDialog(
            result = sweepResult,
            onDismiss = { viewModel.closeSweepResultDialog() }
        )

        // Realm & Body Encyclopedia Dialog
        realmGuideInitialTab?.let { initialTab ->
            RealmGuideDialog(
                initialTab = initialTab,
                profile = profile,
                stats = stats,
                onDismiss = { realmGuideInitialTab = null }
            )
        }
    }
}
