package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CultivationRepository
import com.example.data.db.AppDatabase
import com.example.data.db.CaveAbodeEntity
import com.example.data.db.CultivationLogEntity
import com.example.data.db.InventoryItemEntity
import com.example.data.db.LearnedSkillEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.AlchemyCatalog
import com.example.model.AlchemyRecipe
import com.example.model.BodyRealmCatalog
import com.example.model.CompanionCatalog
import com.example.model.CompanionInfo
import com.example.model.CraftingCatalog
import com.example.model.CraftingRecipe
import com.example.model.ElementType
import com.example.model.Item
import com.example.model.ItemCatalog
import com.example.model.ItemType
import com.example.model.MapCatalog
import com.example.model.MarketCatalog
import com.example.model.MarketItem
import com.example.model.RealmCatalog
import com.example.model.SectCatalog
import com.example.model.SectRank
import com.example.model.SectTask
import com.example.model.SkillCatalog
import com.example.model.SkillType
import com.example.model.SpiritualRoot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class PlayerStats(
    val maxHp: Long = 200L,
    val attack: Long = 30L,
    val defense: Long = 15L,
    val cultivationRatePerSec: Long = 2L,
    val spiritQiRatePerSec: Long = 2L,
    val dodgeRate: Double = 0.05,
    val critRate: Double = 0.05
)

data class OfflineReward(
    val secondsPassed: Long,
    val expEarned: Long,
    val qiEarned: Long,
    val foodEarned: Long,
    val woodEarned: Long,
    val ironEarned: Long,
    val stoneEarned: Long
)

data class TribulationState(
    val isShowing: Boolean = false,
    val isMajorRealm: Boolean = false,
    val successRate: Int = 0,
    val pillsUsed: Int = 0,
    val maxPillsAvailable: Int = 0,
    val pillItem: Item? = null,
    val isResolving: Boolean = false,
    val resultSuccess: Boolean? = null,
    val resultMessage: String = "",
    val reqBodyMet: Boolean = true,
    val reqBodyMessage: String = "",
    val targetRealmName: String = "",
    val targetRealmStage: Int = 1
)

data class CombatTurnLog(
    val attacker: String,
    val defender: String,
    val damage: Long,
    val isCrit: Boolean,
    val isDodge: Boolean,
    val message: String
)

data class ActiveAdventureState(
    val mapId: Int = 0,
    val currentStep: Int = 1,
    val maxSteps: Int = 10,
    val inCombat: Boolean = false,
    val enemyName: String = "",
    val enemyTitle: String = "守关妖王",
    val enemyCurrentHp: Long = 0L,
    val enemyMaxHp: Long = 0L,
    val enemyAtk: Long = 0L,
    val enemyDef: Long = 0L,
    val playerCurrentHp: Long = 0L,
    val playerMaxHp: Long = 0L,
    val combatLogs: List<CombatTurnLog> = emptyList(),
    val currentRound: Int = 1,
    val combatFinished: Boolean = false,
    val combatVictory: Boolean = false,
    val eventMessage: String = "",
    val lootStones: Int = 0,
    val lootExp: Long = 0L,
    val lootHerbs: Map<String, Int> = emptyMap(),
    val lootOres: Map<String, Int> = emptyMap(),
    val isAutoExploring: Boolean = false,
    val battleSpeed: Float = 1.0f,
    val resumedFromStep: Int = 1
)

class CultivationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CultivationRepository

    val playerProfile: StateFlow<PlayerProfileEntity?>
    val caveAbode: StateFlow<CaveAbodeEntity?>
    val inventory: StateFlow<List<InventoryItemEntity>>
    val learnedSkills: StateFlow<List<LearnedSkillEntity>>
    val cultivationLogs: StateFlow<List<CultivationLogEntity>>

    val playerStats: StateFlow<PlayerStats>
    val reqExp: StateFlow<Long>
    val reqBodyExp: StateFlow<Long>

    private val _offlineReward = MutableStateFlow<OfflineReward?>(null)
    val offlineReward: StateFlow<OfflineReward?> = _offlineReward.asStateFlow()

    private val _tribulationState = MutableStateFlow(TribulationState())
    val tribulationState: StateFlow<TribulationState> = _tribulationState.asStateFlow()

    private val _activeAdventure = MutableStateFlow(ActiveAdventureState())
    val activeAdventure: StateFlow<ActiveAdventureState> = _activeAdventure.asStateFlow()

    private val _sweepResultState = MutableStateFlow<com.example.model.SweepRewardResult?>(null)
    val sweepResultState: StateFlow<com.example.model.SweepRewardResult?> = _sweepResultState.asStateFlow()

    private var autoExploreJob: Job? = null
    private var combatJob: Job? = null
    private var gameLoopJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CultivationRepository(db.cultivationDao())

        playerProfile = repository.playerProfile.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
        caveAbode = repository.caveAbode.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
        inventory = repository.inventoryItems.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        learnedSkills = repository.learnedSkills.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        cultivationLogs = repository.recentLogs.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

        // Compute Required Exp for realm & body
        reqExp = playerProfile.map { p ->
            if (p == null) 100L
            else {
                val realm = RealmCatalog.getRealm(p.realmId)
                realm.baseCultivationPerStage * p.realmStage
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 100L)

        reqBodyExp = playerProfile.map { p ->
            if (p == null) 80L
            else {
                val bRealm = BodyRealmCatalog.getBodyRealm(p.bodyRealmId)
                bRealm.baseCultivationPerStage * p.bodyRealmStage
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 80L)

        // Calculate Overall Player Stats
        playerStats = combine(playerProfile, learnedSkills) { profile, skills ->
            calculateStats(profile, skills)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, PlayerStats())

        viewModelScope.launch {
            repository.ensureInitialized()
            checkOfflineGains()
            startGameLoop()
        }
    }

    private fun calculateStats(
        p: PlayerProfileEntity?,
        skills: List<LearnedSkillEntity>
    ): PlayerStats {
        if (p == null) return PlayerStats()

        val realm = RealmCatalog.getRealm(p.realmId)
        val bodyRealm = BodyRealmCatalog.getBodyRealm(p.bodyRealmId)
        val sect = SectCatalog.getSect(p.currentSectId)

        // 1. Base from realm & body
        var hp = (realm.baseHp * p.realmStage) + (bodyRealm.hpBonus * p.bodyRealmStage) + p.totalPillHp
        var atk = (realm.baseAttack * p.realmStage) + (bodyRealm.atkBonus * p.bodyRealmStage) + p.totalPillAtk
        var def = (realm.baseDefense * p.realmStage) + (bodyRealm.defBonus * p.bodyRealmStage) + p.totalPillDef
        var cultRate = realm.baseCultivationRate
        var qiRate = 2L + (p.spiritArrayLevel * 3L)

        // 2. Equipment
        p.equippedWeaponId?.let { ItemCatalog.getItem(it) }?.let {
            atk += it.atkBonus
            hp += it.hpBonus
            def += it.defBonus
        }
        p.equippedArmorId?.let { ItemCatalog.getItem(it) }?.let {
            atk += it.atkBonus
            hp += it.hpBonus
            def += it.defBonus
        }
        p.equippedRingId?.let { ItemCatalog.getItem(it) }?.let {
            atk += it.atkBonus
            hp += it.hpBonus
            def += it.defBonus
        }

        // 3. Learned Skills
        for (ls in skills) {
            val skill = SkillCatalog.allSkills.find { it.id == ls.skillId } ?: continue
            when (skill.type) {
                SkillType.CULTIVATION -> cultRate += skill.bonusValue
                SkillType.ATTACK -> atk += skill.bonusValue
                SkillType.DEFENSE -> def += skill.bonusValue
                SkillType.HP -> hp += skill.bonusValue
                SkillType.QI_SPEED -> qiRate += skill.bonusValue
            }
        }

        // 4. Five Spiritual Roots multiplier
        val rootAvg = (p.rootMetalLevel + p.rootWoodLevel + p.rootWaterLevel + p.rootFireLevel + p.rootEarthLevel) / 5.0
        val rootMult = 1.0 + (rootAvg * 0.02)
        atk = (atk * rootMult).toLong()
        def = (def * rootMult).toLong()
        hp = (hp * rootMult).toLong()

        // 5. Training Room 5x Speed Multiplier!
        if (p.trainingRoomSeconds > 0) {
            cultRate *= 5
        }

        // 6. Daoist Companion Dual Cultivation Passive
        p.companionId?.let { compId ->
            val comp = CompanionCatalog.getCompanion(compId)
            cultRate = (cultRate * (1.0 + comp.dualCultivationBonusExpRate * 0.2)).toLong()
            qiRate = (qiRate * (1.0 + comp.dualCultivationBonusQiRate * 0.2)).toLong()
        }

        // 7. Celestial Realm Bonus
        if (p.isAscended) {
            cultRate = (cultRate * 2.5).toLong()
            qiRate = (qiRate * 2.5).toLong()
        }

        return PlayerStats(
            maxHp = maxOf(200L, hp),
            attack = maxOf(30L, atk),
            defense = maxOf(15L, def),
            cultivationRatePerSec = maxOf(1L, cultRate),
            spiritQiRatePerSec = maxOf(1L, qiRate),
            dodgeRate = minOf(0.50, 0.05 + (p.realmId * 0.01)),
            critRate = minOf(0.60, 0.05 + (p.realmId * 0.015))
        )
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var tickCount = 0
            while (isActive) {
                delay(1000L)
                tickCount++

                val p = repository.getProfileDirect()
                val cave = repository.getCaveAbodeDirect()
                val stats = calculateStats(p, repository.getLearnedSkillsDirect())

                // 1. Gain Cultivation Exp & Spirit Qi
                var newExp = p.currentExp + stats.cultivationRatePerSec
                var newQi = minOf(p.spiritQiMax, p.spiritQi + stats.spiritQiRatePerSec)

                // Epiphany chance (灵光一闪 / 顿悟) 1% chance every second
                if (Random.nextDouble() < 0.01) {
                    val burstExp = stats.cultivationRatePerSec * Random.nextLong(30, 90)
                    newExp += burstExp
                    repository.addLog("✨ 心意通达，触发天人合一顿悟，瞬增 $burstExp 点修为！", "ENCOUNTER")
                }

                // 2. Cave Abode Resource Production
                // 1 food servant produces 2 food, but each servant eats 1 food
                val netFood = (cave.foodServants * 2L) - cave.totalServants
                var newFood = maxOf(0L, cave.food + netFood)
                var newWood = cave.wood + (cave.woodServants * 2L)
                var newIron = cave.iron + (cave.ironServants * 1L)
                var newStone = cave.stone + (cave.stoneServants * 1L)

                // 3. Herb Garden Growth
                var gardenProgress = cave.plantProgressSeconds + 1
                if (gardenProgress >= cave.plantTargetSeconds) {
                    // Auto-harvest herb to inventory
                    gardenProgress = 0
                    val harvestedItem = cave.currentPlantSeedId
                    val yieldCount = 2 + cave.herbGardenLevel
                    repository.addItem(harvestedItem, yieldCount)
                    val herbName = ItemCatalog.getItem(harvestedItem).name
                    repository.addLog("🌿 洞府药园灵气充足，收获了 $yieldCount 份【$herbName】！", "ALCHEMY")
                }

                // 4. Training Room Countdown
                val newTrainingRoomSec = maxOf(0, p.trainingRoomSeconds - 1)

                // 5. Auto-Sweep (出窍神游) if active
                if (p.isAutoSweeping && p.autoSweepMapId > 0 && tickCount % 5 == 0) {
                    val map = MapCatalog.getMap(p.autoSweepMapId)
                    val dropStones = Random.nextInt(map.stoneRewardMin, map.stoneRewardMax + 1)
                    val dropExp = map.expReward
                    newExp += dropExp
                    var newStones = p.spiritStones + dropStones

                    // Random herb/ore loot
                    if (map.possibleHerbs.isNotEmpty() && Random.nextBoolean()) {
                        val dropHerb = map.possibleHerbs.random()
                        repository.addItem(dropHerb, 1)
                    }
                    if (map.possibleOres.isNotEmpty() && Random.nextBoolean()) {
                        val dropOre = map.possibleOres.random()
                        repository.addItem(dropOre, 1)
                    }

                    repository.saveProfile(
                        p.copy(
                            currentExp = newExp,
                            spiritQi = newQi,
                            spiritStones = newStones,
                            trainingRoomSeconds = newTrainingRoomSec
                        )
                    )
                } else {
                    repository.saveProfile(
                        p.copy(
                            currentExp = newExp,
                            spiritQi = newQi,
                            trainingRoomSeconds = newTrainingRoomSec
                        )
                    )
                }

                repository.saveCaveAbode(
                    cave.copy(
                        food = newFood,
                        wood = newWood,
                        iron = newIron,
                        stone = newStone,
                        plantProgressSeconds = gardenProgress
                    )
                )
            }
        }
    }

    private suspend fun checkOfflineGains() {
        val p = repository.getProfileDirect()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (p.lastSweepResetDate != todayStr) {
            repository.saveProfile(
                p.copy(
                    dailySweepUsed = 0,
                    extraSweepPurchasedToday = 0,
                    lastSweepResetDate = todayStr
                )
            )
        }

        val now = System.currentTimeMillis()
        val lastSave = p.lastSaveTimestamp
        val seconds = minOf(86400L, maxOf(0L, (now - lastSave) / 1000L))

        if (seconds >= 30) {
            val stats = calculateStats(p, repository.getLearnedSkillsDirect())
            val cave = repository.getCaveAbodeDirect()

            val expEarned = stats.cultivationRatePerSec * seconds
            val qiEarned = minOf(p.spiritQiMax - p.spiritQi, stats.spiritQiRatePerSec * seconds)
            val woodEarned = cave.woodServants * seconds / 2
            val ironEarned = cave.ironServants * seconds / 4
            val stoneEarned = cave.stoneServants * seconds / 4

            _offlineReward.value = OfflineReward(
                secondsPassed = seconds,
                expEarned = expEarned,
                qiEarned = maxOf(0L, qiEarned),
                foodEarned = 0L,
                woodEarned = maxOf(0L, woodEarned),
                ironEarned = maxOf(0L, ironEarned),
                stoneEarned = maxOf(0L, stoneEarned)
            )
        }
    }

    fun claimOfflineReward() {
        val reward = _offlineReward.value ?: return
        viewModelScope.launch {
            val p = repository.getProfileDirect()
            val cave = repository.getCaveAbodeDirect()

            repository.saveProfile(
                p.copy(
                    currentExp = p.currentExp + reward.expEarned,
                    spiritQi = minOf(p.spiritQiMax, p.spiritQi + reward.qiEarned)
                )
            )
            repository.saveCaveAbode(
                cave.copy(
                    wood = cave.wood + reward.woodEarned,
                    iron = cave.iron + reward.ironEarned,
                    stone = cave.stone + reward.stoneEarned
                )
            )
            repository.addLog("道友离线神游归来，收获了 ${reward.expEarned} 修为与 ${reward.qiEarned} 灵气！", "BREAKTHROUGH")
            _offlineReward.value = null
        }
    }

    // ==================== TRIBULATION & BREAKTHROUGH ====================

    fun openTribulationDialog() {
        viewModelScope.launch {
            val p = repository.getProfileDirect()
            val realm = RealmCatalog.getRealm(p.realmId)
            val needed = realm.baseCultivationPerStage * p.realmStage
            if (p.currentExp < needed) return@launch

            val isMajor = p.realmStage >= realm.maxStage
            val baseRate = realm.tribulationChance

            val nextRealmId = if (p.realmStage >= realm.maxStage) p.realmId + 1 else p.realmId
            val nextStage = if (p.realmStage >= realm.maxStage) 1 else p.realmStage + 1
            val targetRealmInfo = RealmCatalog.getRealm(nextRealmId)

            val (bodyMet, bodyMsg) = RealmCatalog.checkBodyRequirement(
                targetRealmId = nextRealmId,
                targetRealmStage = nextStage,
                currentBodyRealmId = p.bodyRealmId,
                currentBodyRealmStage = p.bodyRealmStage
            )

            // Match pill for current realm
            val pillId = when (p.realmId) {
                1 -> "p_juqi"
                2 -> "p_zhuji"
                3 -> "p_jindan"
                4 -> "p_yuanying"
                5 -> "p_huashen"
                6 -> "p_chuqiao"
                else -> "p_dacheng"
            }
            val pillItem = ItemCatalog.getItem(pillId)
            val ownedPillCount = repository.getInventoryDirect().find { it.itemId == pillId }?.count ?: 0

            _tribulationState.value = TribulationState(
                isShowing = true,
                isMajorRealm = isMajor,
                successRate = baseRate,
                pillsUsed = 0,
                maxPillsAvailable = ownedPillCount,
                pillItem = pillItem,
                isResolving = false,
                resultSuccess = null,
                resultMessage = "",
                reqBodyMet = bodyMet,
                reqBodyMessage = bodyMsg,
                targetRealmName = targetRealmInfo.name,
                targetRealmStage = nextStage
            )
        }
    }

    fun adjustTribulationPills(count: Int) {
        val current = _tribulationState.value
        val pill = current.pillItem ?: return
        val p = playerProfile.value ?: return
        val realm = RealmCatalog.getRealm(p.realmId)

        val clamped = count.coerceIn(0, current.maxPillsAvailable)
        val rate = minOf(100, realm.tribulationChance + (clamped * pill.tribulationRateBonus))

        _tribulationState.value = current.copy(
            pillsUsed = clamped,
            successRate = rate
        )
    }

    fun executeTribulation() {
        val state = _tribulationState.value
        if (state.isResolving || !state.reqBodyMet) return

        _tribulationState.value = state.copy(isResolving = true)

        viewModelScope.launch {
            delay(1200L) // Dramatic lightning animation delay
            val p = repository.getProfileDirect()
            val realm = RealmCatalog.getRealm(p.realmId)
            val neededExp = realm.baseCultivationPerStage * p.realmStage

            // Deduct pills
            if (state.pillsUsed > 0 && state.pillItem != null) {
                repository.removeItem(state.pillItem.id, state.pillsUsed)
            }

            val roll = Random.nextInt(1, 101)
            val success = roll <= state.successRate

            if (success) {
                // Success
                val newExp = maxOf(0L, p.currentExp - neededExp)
                val newRealmId: Int
                val newStage: Int

                if (p.realmStage >= realm.maxStage) {
                    newRealmId = p.realmId + 1
                    newStage = 1
                } else {
                    newRealmId = p.realmId
                    newStage = p.realmStage + 1
                }

                val nextRealmName = RealmCatalog.getRealm(newRealmId).name
                val msg = "天劫雷霆散去，紫气东来三万里！成功突破至【$nextRealmName ${newStage}阶】！"

                repository.saveProfile(
                    p.copy(
                        realmId = newRealmId,
                        realmStage = newStage,
                        currentExp = newExp,
                        reputation = p.reputation + (newRealmId * 50L)
                    )
                )
                repository.addLog(msg, "TRIBULATION")

                _tribulationState.value = state.copy(
                    isResolving = false,
                    resultSuccess = true,
                    resultMessage = msg
                )
            } else {
                // Failure
                val penaltyExp = (neededExp * 0.2).toLong()
                val newExp = maxOf(0L, p.currentExp - penaltyExp)
                val msg = "劫雷反噬，心魔趁虚而入！突破失败，损失了 $penaltyExp 点修为。"

                repository.saveProfile(p.copy(currentExp = newExp))
                repository.addLog("渡劫受挫，雷火攻心，修为有所损耗。", "TRIBULATION")

                _tribulationState.value = state.copy(
                    isResolving = false,
                    resultSuccess = false,
                    resultMessage = msg
                )
            }
        }
    }

    fun closeTribulationDialog() {
        _tribulationState.value = TribulationState()
    }

    // ==================== BODY CULTIVATION ====================

    fun breakthroughBodyRealm(onResult: (Boolean, String) -> Unit = { _, _ -> }): Boolean {
        val p = playerProfile.value ?: return false
        val bRealm = BodyRealmCatalog.getBodyRealm(p.bodyRealmId)
        val needed = bRealm.baseCultivationPerStage * p.bodyRealmStage

        if (p.currentExp < needed) {
            onResult(false, "修为不足，无法淬体！")
            return false
        }

        val newBId = if (p.bodyRealmStage >= bRealm.maxStage) p.bodyRealmId + 1 else p.bodyRealmId
        val newStage = if (p.bodyRealmStage >= bRealm.maxStage) 1 else p.bodyRealmStage + 1

        val (cultMet, cultMsg) = BodyRealmCatalog.checkCultivationRequirement(
            targetBodyId = newBId,
            targetBodyStage = newStage,
            currentRealmId = p.realmId,
            currentRealmStage = p.realmStage
        )

        if (!cultMet) {
            viewModelScope.launch {
                repository.addLog("淬体受阻：$cultMsg，真元底蕴不足以支撑肉身蜕变！", "BREAKTHROUGH")
            }
            onResult(false, "真元不足以支撑肉身蜕变：$cultMsg")
            return false
        }

        viewModelScope.launch {
            val newExp = p.currentExp - needed
            val bName = BodyRealmCatalog.getBodyRealm(newBId).name
            repository.saveProfile(
                p.copy(
                    bodyRealmId = newBId,
                    bodyRealmStage = newStage,
                    currentExp = newExp
                )
            )
            val successMsg = "肉身淬炼圆满，脱胎换骨，晋升至【$bName ${newStage}阶】！"
            repository.addLog(successMsg, "BREAKTHROUGH")
            onResult(true, successMsg)
        }
        return true
    }

    // ==================== SPIRITUAL ROOTS & ARRAY ====================

    fun upgradeSpiritualRoot(element: ElementType): Boolean {
        val p = playerProfile.value ?: return false
        val currentLvl = when (element) {
            ElementType.METAL -> p.rootMetalLevel
            ElementType.WOOD -> p.rootWoodLevel
            ElementType.WATER -> p.rootWaterLevel
            ElementType.FIRE -> p.rootFireLevel
            ElementType.EARTH -> p.rootEarthLevel
        }
        val cost = SpiritualRoot(element, currentLvl).getUpgradeCost()
        if (p.spiritQi < cost) return false

        viewModelScope.launch {
            val newQi = p.spiritQi - cost
            val newProfile = when (element) {
                ElementType.METAL -> p.copy(rootMetalLevel = currentLvl + 1, spiritQi = newQi)
                ElementType.WOOD -> p.copy(rootWoodLevel = currentLvl + 1, spiritQi = newQi)
                ElementType.WATER -> p.copy(rootWaterLevel = currentLvl + 1, spiritQi = newQi)
                ElementType.FIRE -> p.copy(rootFireLevel = currentLvl + 1, spiritQi = newQi)
                ElementType.EARTH -> p.copy(rootEarthLevel = currentLvl + 1, spiritQi = newQi)
            }
            repository.saveProfile(newProfile)
            repository.addLog("注入五行灵气，${element.displayName}升至 ${currentLvl + 1} 阶！", "BREAKTHROUGH")
        }
        return true
    }

    fun upgradeSpiritArray(): Boolean {
        val p = playerProfile.value ?: return false
        val cave = caveAbode.value ?: return false
        val reqWood = (p.spiritArrayLevel * 200L)
        val reqStone = (p.spiritArrayLevel * 100L)
        val reqExp = (p.spiritArrayLevel * 300L)

        if (cave.wood < reqWood || cave.stone < reqStone || p.currentExp < reqExp) return false

        viewModelScope.launch {
            repository.saveCaveAbode(
                cave.copy(
                    wood = cave.wood - reqWood,
                    stone = cave.stone - reqStone
                )
            )
            val newLvl = p.spiritArrayLevel + 1
            repository.saveProfile(
                p.copy(
                    spiritArrayLevel = newLvl,
                    spiritQiMax = p.spiritQiMax + 1000L,
                    currentExp = p.currentExp - reqExp
                )
            )
            repository.addLog("洞府聚灵阵升至 $newLvl 阶，灵气上限与吸收速率大幅提升！", "BREAKTHROUGH")
        }
        return true
    }

    // ==================== CAVE ABODE SERVANTS & GARDEN ====================

    fun recruitServant(): Boolean {
        val cave = caveAbode.value ?: return false
        val costFood = 100L + (cave.totalServants * 50L)
        if (cave.food < costFood) return false

        viewModelScope.launch {
            repository.saveCaveAbode(
                cave.copy(
                    food = cave.food - costFood,
                    totalServants = cave.totalServants + 1,
                    foodServants = cave.foodServants + 1
                )
            )
            repository.addLog("洞府招募了一名仙仆道童，辛勤耕耘灵田！", "SECT")
        }
        return true
    }

    fun adjustServant(type: String, delta: Int) {
        val cave = caveAbode.value ?: return
        val currentAssigned = cave.foodServants + cave.woodServants + cave.ironServants + cave.stoneServants

        viewModelScope.launch {
            var fs = cave.foodServants
            var ws = cave.woodServants
            var is_ = cave.ironServants
            var ss = cave.stoneServants

            when (type) {
                "FOOD" -> fs = maxOf(0, fs + delta)
                "WOOD" -> ws = maxOf(0, ws + delta)
                "IRON" -> is_ = maxOf(0, is_ + delta)
                "STONE" -> ss = maxOf(0, ss + delta)
            }

            if (fs + ws + is_ + ss <= cave.totalServants) {
                repository.saveCaveAbode(
                    cave.copy(
                        foodServants = fs,
                        woodServants = ws,
                        ironServants = is_,
                        stoneServants = ss
                    )
                )
            }
        }
    }

    fun changeGardenPlant(seedItemId: String) {
        val cave = caveAbode.value ?: return
        viewModelScope.launch {
            repository.saveCaveAbode(
                cave.copy(
                    currentPlantSeedId = seedItemId,
                    plantProgressSeconds = 0
                )
            )
            repository.addLog("药园已更替种植仙草【${ItemCatalog.getItem(seedItemId).name}】！", "ALCHEMY")
        }
    }

    fun fertilizeGarden(): Boolean {
        val p = playerProfile.value ?: return false
        val cave = caveAbode.value ?: return false
        val costQi = 200L
        if (p.spiritQi < costQi) return false

        viewModelScope.launch {
            repository.saveProfile(p.copy(spiritQi = p.spiritQi - costQi))
            val newProgress = minOf(cave.plantTargetSeconds, cave.plantProgressSeconds + 60)
            repository.saveCaveAbode(cave.copy(plantProgressSeconds = newProgress))
            repository.addLog("降下甘霖灵雨，药园仙草生长进度加速 60 秒！", "ALCHEMY")
        }
        return true
    }

    // ==================== SECT SYSTEM (门派系统) ====================

    fun joinSect(sectId: Int): Boolean {
        val p = playerProfile.value ?: return false
        val sect = SectCatalog.getSect(sectId)
        if (p.realmId < sect.reqRealmId) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    currentSectId = sectId,
                    currentSectRank = SectRank.OUTER.name,
                    sectContribution = 0
                )
            )
            repository.addLog("恭喜道友正式拜入【${sect.name}】，成为外门弟子！", "SECT")
        }
        return true
    }

    fun leaveSect(): Boolean {
        val p = playerProfile.value ?: return false
        val sect = SectCatalog.getSect(p.currentSectId)
        if (p.reputation < sect.quitCostReputation) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    currentSectId = 1, // 回到逍遥派
                    currentSectRank = SectRank.OUTER.name,
                    sectContribution = 0,
                    reputation = p.reputation - sect.quitCostReputation
                )
            )
            repository.addLog("道友已叛出【${sect.name}】，消耗 ${sect.quitCostReputation} 点声望，已重归散修逍遥派。", "SECT")
        }
        return true
    }

    fun promoteSectRank(): Boolean {
        val p = playerProfile.value ?: return false
        val currentRank = SectRank.valueOf(p.currentSectRank)
        val nextRank = currentRank.next() ?: return false

        if (p.sectContribution < nextRank.reqContribution) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    currentSectRank = nextRank.name,
                    sectContribution = p.sectContribution - nextRank.reqContribution,
                    reputation = p.reputation + 200L
                )
            )
            repository.addLog("道法精湛，晋升为宗门【${nextRank.title}】！", "SECT")
        }
        return true
    }

    fun claimDailySalary(): Boolean {
        val p = playerProfile.value ?: return false
        val now = System.currentTimeMillis()
        val oneDayMs = 60 * 1000L // 60秒即可再次领取日常俸禄，方便挂机流畅体验！
        if (now - p.lastSalaryClaimTimestamp < oneDayMs) return false

        val rank = SectRank.valueOf(p.currentSectRank)
        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones + rank.salaryStones,
                    sectContribution = p.sectContribution + rank.salaryContribution,
                    lastSalaryClaimTimestamp = now
                )
            )
            repository.addLog("领取宗门俸禄：获得 ${rank.salaryStones} 灵石与 ${rank.salaryContribution} 门派贡献！", "SECT")
        }
        return true
    }

    fun enterTrainingRoom(): Boolean {
        val p = playerProfile.value ?: return false
        val costContrib = 150
        if (p.sectContribution < costContrib) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    sectContribution = p.sectContribution - costContrib,
                    trainingRoomSeconds = p.trainingRoomSeconds + 600 // +10分钟 5倍修炼加速
                )
            )
            repository.addLog("消耗 150 贡献踏入宗门练功房，开启【5倍修炼加速】10分钟！", "SECT")
        }
        return true
    }

    fun requestMasterGuidance(): Boolean {
        val p = playerProfile.value ?: return false
        val now = System.currentTimeMillis()
        if (now - p.lastGuidanceTimestamp < 60 * 1000L) return false

        val stats = playerStats.value
        val gainExp = stats.cultivationRatePerSec * 120L // 2分钟修为
        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    currentExp = p.currentExp + gainExp,
                    lastGuidanceTimestamp = now
                )
            )
            repository.addLog("向宗门掌门求教指点，心领神会，顿悟获得 $gainExp 点修为！", "SECT")
        }
        return true
    }

    fun completeSectTask(task: SectTask): Boolean {
        val p = playerProfile.value ?: return false
        val currentRank = SectRank.valueOf(p.currentSectRank)
        if (currentRank.ordinal < task.reqRank.ordinal) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    sectContribution = p.sectContribution + task.rewardContribution,
                    spiritStones = p.spiritStones + task.rewardStones,
                    currentExp = p.currentExp + task.rewardExp
                )
            )
            repository.addLog("完成宗门任务【${task.title}】，获得 ${task.rewardContribution} 贡献与 ${task.rewardStones} 灵石！", "SECT")
        }
        return true
    }

    fun learnSkill(skillId: String): Boolean {
        val p = playerProfile.value ?: return false
        val skill = SkillCatalog.allSkills.find { it.id == skillId } ?: return false
        val alreadyLearned = learnedSkills.value.any { it.skillId == skillId }
        if (alreadyLearned || p.sectContribution < skill.costContribution) return false

        viewModelScope.launch {
            repository.saveProfile(p.copy(sectContribution = p.sectContribution - skill.costContribution))
            repository.learnSkill(skillId)
            repository.addLog("藏经阁参悟绝学【${skill.name}】，实力大增！", "SECT")
        }
        return true
    }

    // ==================== DAOIST COMPANIONS & DUAL CULTIVATION ====================

    fun meetCompanion(companionId: String): Boolean {
        val p = playerProfile.value ?: return false
        val comp = CompanionCatalog.getCompanion(companionId)
        if (p.realmId < comp.reqRealmId) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    companionId = companionId,
                    companionAffection = maxOf(10, p.companionAffection)
                )
            )
            repository.addLog("于仙缘探索中结识【${comp.name}】（${comp.title}），引为知己道友！", "COMPANION")
        }
        return true
    }

    fun giftCompanion(companionId: String): Boolean {
        val p = playerProfile.value ?: return false
        val costStones = 500L
        if (p.spiritStones < costStones) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones - costStones,
                    companionAffection = p.companionAffection + 20
                )
            )
            val comp = CompanionCatalog.getCompanion(companionId)
            repository.addLog("向道友【${comp.name}】赠送天山灵茶，亲密度提升至 ${p.companionAffection + 20}！", "COMPANION")
        }
        return true
    }

    fun dualCultivate(companionId: String): Boolean {
        val p = playerProfile.value ?: return false
        val now = System.currentTimeMillis()
        if (now - p.lastDualCultivationTimestamp < 60 * 1000L) return false

        val comp = CompanionCatalog.getCompanion(companionId)
        val stats = playerStats.value
        val bonusExp = (stats.cultivationRatePerSec * 180L * (1.0 + comp.dualCultivationBonusExpRate)).toLong()
        val bonusQi = (stats.spiritQiRatePerSec * 180L * (1.0 + comp.dualCultivationBonusQiRate)).toLong()

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    currentExp = p.currentExp + bonusExp,
                    spiritQi = minOf(p.spiritQiMax, p.spiritQi + bonusQi),
                    companionAffection = p.companionAffection + 10,
                    lastDualCultivationTimestamp = now
                )
            )
            repository.addLog("与道侣【${comp.name}】双修打坐，阴阳交泰，获得 $bonusExp 修为与 $bonusQi 灵气反哺！", "COMPANION")
        }
        return true
    }

    // ==================== MARKET / GHOST MARKET (仙坊鬼市) ====================

    fun buyMarketItem(item: MarketItem): Boolean {
        val p = playerProfile.value ?: return false
        if (p.spiritStones < item.priceStones || p.realmId < item.reqRealmId) return false

        viewModelScope.launch {
            repository.saveProfile(p.copy(spiritStones = p.spiritStones - item.priceStones))
            repository.addItem(item.itemId, 1)
            val it = ItemCatalog.getItem(item.itemId)
            repository.addLog("于仙坊中花费 ${item.priceStones} 灵石购得【${it.name}】！", "SECT")
        }
        return true
    }

    // ==================== ADVENTURE, COMBAT & SWEEP ====================

    fun startAdventure(mapId: Int) {
        val p = playerProfile.value ?: return
        val map = MapCatalog.getMap(mapId)
        if (p.realmId < map.reqRealmId) return

        val stats = playerStats.value
        val checkpoints = com.example.model.AdventureHelper.parseCheckpoints(p.mapCheckpointsStr)
        val resumeStep = checkpoints[mapId] ?: 1

        _activeAdventure.value = ActiveAdventureState(
            mapId = mapId,
            currentStep = resumeStep,
            maxSteps = map.stepCount,
            inCombat = false,
            enemyName = map.enemyName,
            enemyTitle = if (map.isImmortalMap) "仙界真灵守护神" else "秘境守关妖王",
            enemyCurrentHp = map.enemyHp,
            enemyMaxHp = map.enemyHp,
            enemyAtk = map.enemyAtk,
            enemyDef = map.enemyDef,
            playerCurrentHp = stats.maxHp,
            playerMaxHp = stats.maxHp,
            combatLogs = emptyList(),
            currentRound = 1,
            combatFinished = false,
            combatVictory = false,
            eventMessage = if (resumeStep > 1) "道友御剑重临【${map.name}】，从上次驻足的【第 $resumeStep 步】继续探索！"
            else "道友步入【${map.name}】，四周灵气氤氲，暗藏杀机！",
            isAutoExploring = false,
            battleSpeed = 1.0f,
            resumedFromStep = resumeStep
        )
    }

    fun stepAdventure() {
        val current = _activeAdventure.value
        if (current.mapId == 0 || current.combatFinished || current.inCombat) return

        val map = MapCatalog.getMap(current.mapId)
        val nextStep = current.currentStep + 1

        if (nextStep >= current.maxSteps) {
            // Encounter Boss!
            _activeAdventure.value = current.copy(
                currentStep = current.maxSteps,
                inCombat = true,
                eventMessage = "⚠️ 深入秘境尽头！遭遇守关妖王【${map.enemyName}】，大战一触即发！"
            )
            // Save checkpoint to boss step
            saveMapCheckpoint(current.mapId, current.maxSteps)
            executeCombat()
        } else {
            // Random Exploration Event
            val rand = Random.nextInt(1, 101)
            when {
                rand <= 35 -> {
                    // Small monster encounter / spirit herbs
                    val foundHerb = map.possibleHerbs.randomOrNull() ?: "mat_herb_1"
                    _activeAdventure.value = current.copy(
                        currentStep = nextStep,
                        eventMessage = "步入深山幽谷，偶遇灵芝仙草，采撷得【${ItemCatalog.getItem(foundHerb).name}】！"
                    )
                    viewModelScope.launch {
                        repository.addItem(foundHerb, 1)
                        saveMapCheckpoint(current.mapId, nextStep)
                    }
                }
                rand <= 70 -> {
                    // Ore vein discovery
                    val foundOre = map.possibleOres.randomOrNull() ?: "mat_ore_1"
                    _activeAdventure.value = current.copy(
                        currentStep = nextStep,
                        eventMessage = "劈开上古灵石矿脉，采掘得珍稀【${ItemCatalog.getItem(foundOre).name}】！"
                    )
                    viewModelScope.launch {
                        repository.addItem(foundOre, 1)
                        saveMapCheckpoint(current.mapId, nextStep)
                    }
                }
                else -> {
                    // Meditation & Spirit stones
                    val expGain = map.expReward / 4
                    val stonesGain = Random.nextInt(15, 45)
                    _activeAdventure.value = current.copy(
                        currentStep = nextStep,
                        eventMessage = "寻得一处洞天福地打坐吐纳，增进 $expGain 点修为，拾得 $stonesGain 灵石！"
                    )
                    viewModelScope.launch {
                        val p = repository.getProfileDirect()
                        repository.saveProfile(
                            p.copy(
                                currentExp = p.currentExp + expGain,
                                spiritStones = p.spiritStones + stonesGain
                            )
                        )
                        saveMapCheckpoint(current.mapId, nextStep)
                    }
                }
            }
        }
    }

    private fun saveMapCheckpoint(mapId: Int, step: Int) {
        viewModelScope.launch {
            val p = repository.getProfileDirect()
            val currentMap = com.example.model.AdventureHelper.parseCheckpoints(p.mapCheckpointsStr).toMutableMap()
            currentMap[mapId] = step
            val newStr = com.example.model.AdventureHelper.serializeCheckpoints(currentMap)
            repository.saveProfile(p.copy(mapCheckpointsStr = newStr))
        }
    }

    private fun clearMapCheckpoint(mapId: Int) {
        viewModelScope.launch {
            val p = repository.getProfileDirect()
            val currentMap = com.example.model.AdventureHelper.parseCheckpoints(p.mapCheckpointsStr).toMutableMap()
            currentMap.remove(mapId)
            val newStr = com.example.model.AdventureHelper.serializeCheckpoints(currentMap)
            repository.saveProfile(p.copy(mapCheckpointsStr = newStr))
        }
    }

    fun resetMapCheckpoint(mapId: Int) {
        clearMapCheckpoint(mapId)
        if (_activeAdventure.value.mapId == mapId) {
            _activeAdventure.value = _activeAdventure.value.copy(currentStep = 1, resumedFromStep = 1)
        }
    }

    fun retreatAdventure() {
        val current = _activeAdventure.value
        if (current.mapId > 0 && !current.combatVictory) {
            val map = MapCatalog.getMap(current.mapId)
            saveMapCheckpoint(current.mapId, current.currentStep)
            viewModelScope.launch {
                repository.addLog("从【${map.name}】暂退回洞府，当前探索进度（第 ${current.currentStep} 步）已保存。", "ADVENTURE")
            }
        }
        combatJob?.cancel()
        autoExploreJob?.cancel()
        _activeAdventure.value = ActiveAdventureState()
    }

    fun toggleAutoExplore() {
        val current = _activeAdventure.value
        if (current.mapId == 0 || current.combatFinished) return

        val newAuto = !current.isAutoExploring
        _activeAdventure.value = current.copy(isAutoExploring = newAuto)

        if (newAuto) {
            autoExploreJob?.cancel()
            autoExploreJob = viewModelScope.launch {
                while (isActive && _activeAdventure.value.isAutoExploring && !_activeAdventure.value.combatFinished) {
                    val state = _activeAdventure.value
                    if (!state.inCombat) {
                        stepAdventure()
                        delay((800L / state.battleSpeed).toLong())
                    } else {
                        break
                    }
                }
            }
        } else {
            autoExploreJob?.cancel()
        }
    }

    fun setBattleSpeed(speed: Float) {
        _activeAdventure.value = _activeAdventure.value.copy(battleSpeed = speed)
    }

    fun skipCombat() {
        combatJob?.cancel()
        executeCombat(skipAnimation = true)
    }

    fun executeCombat(skipAnimation: Boolean = false) {
        combatJob?.cancel()
        combatJob = viewModelScope.launch {
            val state = _activeAdventure.value
            val map = MapCatalog.getMap(state.mapId)
            val stats = playerStats.value
            val skills = learnedSkills.value

            var pHp = if (state.playerCurrentHp > 0) state.playerCurrentHp else stats.maxHp
            var eHp = if (state.enemyCurrentHp > 0) state.enemyCurrentHp else map.enemyHp
            val logs = state.combatLogs.toMutableList()

            var round = state.currentRound

            while (pHp > 0 && eHp > 0 && round <= 30) {
                // Round Header
                logs.add(
                    CombatTurnLog(
                        attacker = "天道",
                        defender = "天地",
                        damage = 0L,
                        isCrit = false,
                        isDodge = false,
                        message = "───【第 $round 回合 交锋】───"
                    )
                )

                // 1. Player Turn
                // Check if any learned skill triggers
                val triggeredSkill = skills.mapNotNull { ls -> SkillCatalog.allSkills.find { it.id == ls.skillId } }
                    .filter { it.type == SkillType.ATTACK || it.type == SkillType.HP }
                    .randomOrNull()

                val isCrit = Random.nextDouble() < stats.critRate
                var baseDmg = maxOf(1L, stats.attack - (map.enemyDef / 2))
                if (triggeredSkill != null && Random.nextDouble() < 0.35) {
                    baseDmg += (triggeredSkill.bonusValue * 1.5).toLong()
                    logs.add(
                        CombatTurnLog(
                            attacker = "道友",
                            defender = map.enemyName,
                            damage = baseDmg,
                            isCrit = true,
                            isDodge = false,
                            message = "⚡ 道友运转无上神通【${triggeredSkill.name}】，剑气纵横三万里！"
                        )
                    )
                }

                if (isCrit) {
                    baseDmg = (baseDmg * 1.8).toLong()
                    logs.add(
                        CombatTurnLog(
                            attacker = "道友",
                            defender = map.enemyName,
                            damage = baseDmg,
                            isCrit = true,
                            isDodge = false,
                            message = "💥【暴击】道友极道剑意破防，重创${map.enemyName}，造成 $baseDmg 点巨额伤害！"
                        )
                    )
                } else {
                    logs.add(
                        CombatTurnLog(
                            attacker = "道友",
                            defender = map.enemyName,
                            damage = baseDmg,
                            isCrit = false,
                            isDodge = false,
                            message = "道友掐诀御剑斩落，对${map.enemyName}造成 $baseDmg 点伤害。"
                        )
                    )
                }
                eHp = maxOf(0L, eHp - baseDmg)

                if (eHp <= 0) break

                // 2. Enemy Turn
                val isPlayerDodge = Random.nextDouble() < stats.dodgeRate
                if (isPlayerDodge) {
                    logs.add(
                        CombatTurnLog(
                            attacker = map.enemyName,
                            defender = "道友",
                            damage = 0L,
                            isCrit = false,
                            isDodge = true,
                            message = "💨 道友施展玄妙身法，身化残影，巧妙避开了${map.enemyName}的凶悍扑杀！"
                        )
                    )
                } else {
                    var eDmg = maxOf(1L, map.enemyAtk - (stats.defense / 2))
                    if (Random.nextDouble() < 0.15) {
                        eDmg = (eDmg * 1.5).toLong()
                        logs.add(
                            CombatTurnLog(
                                attacker = map.enemyName,
                                defender = "道友",
                                damage = eDmg,
                                isCrit = true,
                                isDodge = false,
                                message = "⚠️ ${map.enemyName}狂暴嘶吼，释放本命妖火，对道友造成 $eDmg 点重创！"
                            )
                        )
                    } else {
                        logs.add(
                            CombatTurnLog(
                                attacker = map.enemyName,
                                defender = "道友",
                                damage = eDmg,
                                isCrit = false,
                                isDodge = false,
                                message = "${map.enemyName}施展凶煞妖法，对道友造成 $eDmg 点伤害。"
                            )
                        )
                    }
                    pHp = maxOf(0L, pHp - eDmg)
                }

                round++
                _activeAdventure.value = _activeAdventure.value.copy(
                    playerCurrentHp = pHp,
                    enemyCurrentHp = eHp,
                    combatLogs = logs.toList(),
                    currentRound = round
                )

                if (!skipAnimation) {
                    val delayMs = (400L / _activeAdventure.value.battleSpeed).toLong()
                    delay(delayMs)
                }
            }

            val victory = eHp <= 0
            val p = repository.getProfileDirect()

            if (victory) {
                // Clear map checkpoint
                clearMapCheckpoint(state.mapId)

                // Add to cleared maps
                val clearedSet = com.example.model.AdventureHelper.parseClearedMaps(p.clearedMapsStr).toMutableSet()
                val isFirstClear = !clearedSet.contains(state.mapId)
                clearedSet.add(state.mapId)
                val newClearedStr = com.example.model.AdventureHelper.serializeClearedMaps(clearedSet)

                val dropStones = Random.nextInt(map.stoneRewardMin, map.stoneRewardMax + 1)
                val dropExp = map.expReward
                val herbsGained = mutableMapOf<String, Int>()
                val oresGained = mutableMapOf<String, Int>()

                if (map.possibleHerbs.isNotEmpty()) {
                    val h = map.possibleHerbs.random()
                    val count = Random.nextInt(1, 3)
                    herbsGained[h] = count
                    repository.addItem(h, count)
                }
                if (map.possibleOres.isNotEmpty()) {
                    val o = map.possibleOres.random()
                    val count = Random.nextInt(1, 3)
                    oresGained[o] = count
                    repository.addItem(o, count)
                }

                val newMaxMap = maxOf(p.maxClearedMapId, state.mapId + 1)
                repository.saveProfile(
                    p.copy(
                        currentExp = p.currentExp + dropExp,
                        spiritStones = p.spiritStones + dropStones,
                        maxClearedMapId = newMaxMap,
                        reputation = p.reputation + 60L,
                        clearedMapsStr = newClearedStr
                    )
                )

                _activeAdventure.value = _activeAdventure.value.copy(
                    playerCurrentHp = pHp,
                    enemyCurrentHp = 0L,
                    combatLogs = logs.toList(),
                    combatFinished = true,
                    combatVictory = true,
                    lootStones = dropStones,
                    lootExp = dropExp,
                    lootHerbs = herbsGained,
                    lootOres = oresGained,
                    eventMessage = if (isFirstClear) "🎉 首破秘境！斩杀妖王【${map.enemyName}】，通关成功！解锁【一键神游扫荡】！"
                    else "🎉 斩杀妖王【${map.enemyName}】，斩妖除魔，圆满凯旋！"
                )
                repository.addLog("在【${map.name}】斩杀守关妖王，获得 $dropStones 灵石与 $dropExp 修为！", "COMBAT")
            } else {
                // Defeat: Save checkpoint at current step!
                saveMapCheckpoint(state.mapId, state.currentStep)
                _activeAdventure.value = _activeAdventure.value.copy(
                    playerCurrentHp = 0L,
                    enemyCurrentHp = eHp,
                    combatLogs = logs.toList(),
                    combatFinished = true,
                    combatVictory = false,
                    eventMessage = "💀 道法不敌，被妖王重伤击退！已为您保留当前探索进度（第 ${state.currentStep} 步），修整归来可直接继续挑战！"
                )
                repository.addLog("在【${map.name}】挑战妖王惜败，进度已暂存至第 ${state.currentStep} 步。", "COMBAT")
            }
        }
    }

    fun finishCombat() {
        combatJob?.cancel()
        autoExploreJob?.cancel()
        _activeAdventure.value = ActiveAdventureState()
    }

    // ==================== SWEEP (神游扫荡大阵) ====================

    fun sweepMap(mapId: Int, times: Int): Boolean {
        val p = playerProfile.value ?: return false
        val map = MapCatalog.getMap(mapId)
        val clearedSet = com.example.model.AdventureHelper.parseClearedMaps(p.clearedMapsStr)
        if (!clearedSet.contains(mapId) && p.maxClearedMapId < mapId) return false

        val maxSweeps = com.example.model.AdventureHelper.calculateMaxDailySweeps(p.realmId, p.extraSweepPurchasedToday)
        val remaining = maxSweeps - p.dailySweepUsed
        if (remaining < times || times <= 0) return false

        var totalStones = 0
        var totalExp = 0L
        val herbs = mutableMapOf<String, Int>()
        val ores = mutableMapOf<String, Int>()

        for (i in 1..times) {
            val stones = Random.nextInt(map.stoneRewardMin, map.stoneRewardMax + 1)
            totalStones += stones
            totalExp += map.expReward

            if (map.possibleHerbs.isNotEmpty()) {
                val herbId = map.possibleHerbs.random()
                val count = Random.nextInt(1, 3)
                herbs[herbId] = (herbs[herbId] ?: 0) + count
            }
            if (map.possibleOres.isNotEmpty()) {
                val oreId = map.possibleOres.random()
                val count = Random.nextInt(1, 3)
                ores[oreId] = (ores[oreId] ?: 0) + count
            }
        }

        viewModelScope.launch {
            // Apply rewards to profile & inventory
            for ((h, c) in herbs) {
                repository.addItem(h, c)
            }
            for ((o, c) in ores) {
                repository.addItem(o, c)
            }

            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones + totalStones,
                    currentExp = p.currentExp + totalExp,
                    dailySweepUsed = p.dailySweepUsed + times,
                    reputation = p.reputation + (times * 10L)
                )
            )

            val result = com.example.model.SweepRewardResult(
                mapId = mapId,
                mapName = map.name,
                times = times,
                totalStones = totalStones,
                totalExp = totalExp,
                herbsGained = herbs,
                oresGained = ores
            )
            _sweepResultState.value = result
            repository.addLog("运转神游大阵扫荡【${map.name}】$times 次，获得 $totalStones 灵石与 $totalExp 修为！", "ADVENTURE")
        }
        return true
    }

    fun buyExtraDailySweeps(): Boolean {
        val p = playerProfile.value ?: return false
        val costStones = 200L
        if (p.spiritStones < costStones || p.extraSweepPurchasedToday >= 3) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones - costStones,
                    extraSweepPurchasedToday = p.extraSweepPurchasedToday + 1
                )
            )
            repository.addLog("消耗 200 灵石补充神游令，今日神游扫荡上限 +10 次！", "ADVENTURE")
        }
        return true
    }

    fun closeSweepResultDialog() {
        _sweepResultState.value = null
    }

    fun toggleAutoSweep(mapId: Int): Boolean {
        val p = playerProfile.value ?: return false
        if (p.realmId < 6) return false // Need 出窍期

        val newSweepState = !p.isAutoSweeping
        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    isAutoSweeping = newSweepState,
                    autoSweepMapId = if (newSweepState) mapId else 0
                )
            )
            repository.addLog(
                if (newSweepState) "神识分化，开启在【${MapCatalog.getMap(mapId).name}】出窍神游挂机！"
                else "收回出窍神识，停止自动神游。",
                "ADVENTURE"
            )
        }
        return newSweepState
    }

    // ==================== ALCHEMY & ARTIFACT CRAFTING ====================

    fun craftAlchemy(recipe: AlchemyRecipe): Boolean {
        val p = playerProfile.value ?: return false
        val cave = caveAbode.value ?: return false
        val inv = inventory.value

        if (p.spiritStones < recipe.costStones || cave.wood < recipe.reqWood) return false
        for ((herbId, count) in recipe.reqHerbs) {
            val owned = inv.find { it.itemId == herbId }?.count ?: 0
            if (owned < count) return false
        }

        viewModelScope.launch {
            for ((herbId, count) in recipe.reqHerbs) {
                repository.removeItem(herbId, count)
            }
            repository.saveCaveAbode(cave.copy(wood = cave.wood - recipe.reqWood))
            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones - recipe.costStones,
                    alchemyExp = p.alchemyExp + recipe.expGain
                )
            )
            repository.addItem(recipe.resultItemId, recipe.resultCount)
            val resItem = ItemCatalog.getItem(recipe.resultItemId)
            repository.addLog("开炉炼丹，成功炼制出 ${recipe.resultCount} 颗【${resItem.name}】！", "ALCHEMY")
        }
        return true
    }

    fun craftArtifact(recipe: CraftingRecipe): Boolean {
        val p = playerProfile.value ?: return false
        val cave = caveAbode.value ?: return false
        val inv = inventory.value

        if (p.spiritStones < recipe.costStones || cave.iron < recipe.reqIron) return false
        for ((oreId, count) in recipe.reqOres) {
            val owned = inv.find { it.itemId == oreId }?.count ?: 0
            if (owned < count) return false
        }

        viewModelScope.launch {
            for ((oreId, count) in recipe.reqOres) {
                repository.removeItem(oreId, count)
            }
            repository.saveCaveAbode(cave.copy(iron = cave.iron - recipe.reqIron))
            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones - recipe.costStones,
                    smithExp = p.smithExp + recipe.expGain
                )
            )
            repository.addItem(recipe.resultItemId, 1)
            val resItem = ItemCatalog.getItem(recipe.resultItemId)
            repository.addLog("紫霄神鼎出世，成功锻造无上法宝【${resItem.name}】！", "ALCHEMY")
        }
        return true
    }

    // ==================== INVENTORY USAGE & GEAR ====================

    fun useItem(item: Item): String {
        val p = playerProfile.value ?: return "玩家不存在"
        return when (item.type) {
            ItemType.PILL_EXP -> {
                viewModelScope.launch {
                    repository.removeItem(item.id, 1)
                    repository.saveProfile(p.copy(currentExp = p.currentExp + item.expGain))
                    repository.addLog("吞服【${item.name}】，增加 ${item.expGain} 点修为！", "BREAKTHROUGH")
                }
                "吞服【${item.name}】，修为 +${item.expGain}"
            }
            ItemType.PILL_STAT -> {
                viewModelScope.launch {
                    repository.removeItem(item.id, 1)
                    repository.saveProfile(
                        p.copy(
                            totalPillHp = p.totalPillHp + item.hpBonus,
                            totalPillAtk = p.totalPillAtk + item.atkBonus,
                            totalPillDef = p.totalPillDef + item.defBonus
                        )
                    )
                    repository.addLog("服下灵丹【${item.name}】，永久增加基础属性！", "BREAKTHROUGH")
                }
                "服下【${item.name}】，永久提升属性！"
            }
            ItemType.SEED -> {
                changeGardenPlant(item.id.replace("seed_", "mat_"))
                "已播种在洞府药园"
            }
            else -> "该物品无需直接吞服"
        }
    }

    fun equipItem(item: Item): String {
        val p = playerProfile.value ?: return "玩家不存在"
        viewModelScope.launch {
            val newProfile = when (item.type) {
                ItemType.EQUIP_WEAPON -> p.copy(equippedWeaponId = item.id)
                ItemType.EQUIP_ARMOR -> p.copy(equippedArmorId = item.id)
                ItemType.EQUIP_RING -> p.copy(equippedRingId = item.id)
                else -> p
            }
            repository.saveProfile(newProfile)
            repository.addLog("佩戴法宝【${item.name}】，威能大增！", "SECT")
        }
        return "已佩戴【${item.name}】"
    }

    fun unequipItem(slot: String) {
        val p = playerProfile.value ?: return
        viewModelScope.launch {
            val newProfile = when (slot) {
                "WEAPON" -> p.copy(equippedWeaponId = null)
                "ARMOR" -> p.copy(equippedArmorId = null)
                "RING" -> p.copy(equippedRingId = null)
                else -> p
            }
            repository.saveProfile(newProfile)
        }
    }

    fun sellItem(item: Item): String {
        val p = playerProfile.value ?: return "玩家不存在"
        val count = inventory.value.find { it.itemId == item.id }?.count ?: 0
        if (count <= 0) return "数量不足"

        val gainStones = (item.priceStones * 0.7).toLong()
        viewModelScope.launch {
            repository.removeItem(item.id, 1)
            repository.saveProfile(p.copy(spiritStones = p.spiritStones + gainStones))
            repository.addLog("典当【${item.name}】，换取 $gainStones 灵石。", "SECT")
        }
        return "典当成功，获得 $gainStones 灵石"
    }

    // ==================== ASCENSION (飞升仙界) ====================

    fun ascend(pathway: String): Boolean {
        val p = playerProfile.value ?: return false
        val stats = playerStats.value

        val reqRealmOk = p.realmId >= 9 // 大乘
        val reqHpOk = stats.maxHp >= 50000
        val reqAtkOk = stats.attack >= 10000

        if (!reqRealmOk || (!reqHpOk && !reqAtkOk)) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    isAscended = true,
                    ascensionMethod = pathway,
                    realmId = maxOf(11, p.realmId), // 升至游仙
                    celestialCrystals = p.celestialCrystals + 50L,
                    spiritStones = p.spiritStones + 50000L
                )
            )
            repository.addLog("🎉 白日飞升！叩开南天仙门，羽化登仙，位列三十三天仙班！", "ASCENSION")
        }
        return true
    }

    fun exchangeCrystals(): Boolean {
        val p = playerProfile.value ?: return false
        if (p.spiritStones < 10000) return false

        viewModelScope.launch {
            repository.saveProfile(
                p.copy(
                    spiritStones = p.spiritStones - 10000,
                    celestialCrystals = p.celestialCrystals + 10
                )
            )
            repository.addLog("凝聚凡尘灵石，换得 10 枚至尊仙晶！", "ASCENSION")
        }
        return true
    }

    companion object {
        fun provideFactory(context: android.content.Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as Application
                    return CultivationViewModel(app) as T
                }
            }
    }
}
