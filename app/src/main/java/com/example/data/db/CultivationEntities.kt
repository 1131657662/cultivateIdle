package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val daoistName: String = "太虚散修",
    val realmId: Int = 1,              // 练气
    val realmStage: Int = 1,           // 1阶
    val bodyRealmId: Int = 1,          // 凡人之躯
    val bodyRealmStage: Int = 1,       // 1阶
    val currentExp: Long = 0L,         // 当前修为
    val spiritQi: Long = 0L,           // 当前灵气
    val spiritQiMax: Long = 500L,      // 灵气上限 (聚灵阵)
    val spiritArrayLevel: Int = 1,     // 聚灵阵等级
    val spiritStones: Long = 1000L,    // 灵石
    val celestialCrystals: Long = 0L,  // 仙晶 (仙界货币)
    val reputation: Long = 100L,       // 修真界声望
    val isAscended: Boolean = false,   // 是否已飞升仙界
    val ascensionMethod: String = "",  // 飞升方式
    val currentSectId: Int = 1,        // 逍遥派
    val currentSectRank: String = "OUTER", // 外门弟子
    val sectContribution: Int = 50,    // 宗门贡献
    val trainingRoomSeconds: Int = 0,  // 宗门练功房5倍加速剩余秒数
    val lastSalaryClaimTimestamp: Long = 0L,   // 上次领取宗门俸禄时间
    val lastGuidanceTimestamp: Long = 0L,      // 上次请教掌门时间
    val companionId: String? = null,           // 当前结交/双修的仙友道侣
    val companionAffection: Int = 0,           // 仙友亲密度
    val lastDualCultivationTimestamp: Long = 0L,// 上次道侣双修时间
    val alchemyLevel: Int = 1,         // 炼丹等级
    val alchemyExp: Int = 0,           // 炼丹经验
    val smithLevel: Int = 1,           // 炼器等级
    val smithExp: Int = 0,             // 炼器经验
    val equippedWeaponId: String? = "eq_wood_sword",
    val equippedArmorId: String? = "eq_linen_robe",
    val equippedRingId: String? = null,
    val lastSaveTimestamp: Long = System.currentTimeMillis(),
    val rootMetalLevel: Int = 1,
    val rootWoodLevel: Int = 1,
    val rootWaterLevel: Int = 1,
    val rootFireLevel: Int = 1,
    val rootEarthLevel: Int = 1,
    val totalPillHp: Long = 0L,
    val totalPillAtk: Long = 0L,
    val totalPillDef: Long = 0L,
    val maxClearedMapId: Int = 1,
    val autoSweepMapId: Int = 0,        // 出窍神游地图
    val isAutoSweeping: Boolean = false,
    val dailySweepUsed: Int = 0,        // 今日已使用神游/扫荡次数
    val extraSweepPurchasedToday: Int = 0, // 今日已购买额外次数
    val lastSweepResetDate: String = "",   // 上次重置日期 YYYY-MM-DD
    val mapCheckpointsStr: String = "",    // 记录各秘境失败/撤退暂存进度: "mapId:step,mapId:step"
    val clearedMapsStr: String = "1"       // 记录已通关可扫荡的秘境: "1,2"
)

@Entity(tableName = "cave_abode")
data class CaveAbodeEntity(
    @PrimaryKey val id: Int = 1,
    val food: Long = 300L,
    val wood: Long = 300L,
    val iron: Long = 150L,
    val stone: Long = 150L,
    val totalServants: Int = 5,
    val foodServants: Int = 2,
    val woodServants: Int = 1,
    val ironServants: Int = 1,
    val stoneServants: Int = 1,
    val herbGardenLevel: Int = 1,
    val currentPlantSeedId: String = "mat_herb_1", // 正在种植的药材
    val plantProgressSeconds: Int = 0,             // 成长秒数
    val plantTargetSeconds: Int = 120              // 成熟需要秒数 (2分钟)
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val itemId: String,
    val count: Int
)

@Entity(tableName = "learned_skills")
data class LearnedSkillEntity(
    @PrimaryKey val skillId: String,
    val level: Int = 1
)

@Entity(tableName = "cultivation_logs")
data class CultivationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    val type: String // BREAKTHROUGH, TRIBULATION, SECT, COMBAT, ADVENTURE, ALCHEMY, ASCENSION, ENCOUNTER, COMPANION
)
