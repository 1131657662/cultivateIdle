package com.example.model

/**
 * Cultivation Realm Definitions (境界体系 - 练气至仙尊)
 */
data class RealmInfo(
    val id: Int,
    val name: String,             // 练气, 筑基, 金丹, 元婴, 化神, 出窍, 分神, 合体, 洞虚, 大乘, 游仙, 真仙, 玄仙, 金仙, 仙君, 仙尊
    val maxStage: Int = 10,       // 1-10 阶 / 层
    val baseCultivationPerStage: Long, // 升级所需修为
    val baseHp: Long,
    val baseAttack: Long,
    val baseDefense: Long,
    val baseCultivationRate: Long, // 基础秒产修为
    val tribulationChance: Int,   // 基础渡劫成功率
    val isImmortalRealm: Boolean = false, // 是否属于仙界境界
    val description: String = "", // 境界玄妙描述
    val featureUnlocks: String = "", // 解锁玩法功能
    val reqBodyRealmId: Int = 1,  // 突破所需最低肉身境界
    val reqBodyRealmStage: Int = 1 // 突破所需最低肉身阶数
)

object RealmCatalog {
    val realms = listOf(
        RealmInfo(1, "练气", 10, 100L, 200, 30, 15, 2, 95, false, "初入仙途，纳天地灵气入体，开辟周天气海。", "吐纳修炼、洞府开辟、储物仙戒", 1, 1),
        RealmInfo(2, "筑基", 10, 400L, 800, 100, 60, 6, 90, false, "气海化液，筑就无上仙基，寿元大增。", "掌门传功、地阶功法、门派俸禄", 1, 5),
        RealmInfo(3, "金丹", 10, 1500L, 2500, 320, 200, 15, 85, false, "凝结九转金丹，一颗金丹吞入腹，始知我命不由天。", "聚灵法阵、灵药园、二阶丹药", 2, 1),
        RealmInfo(4, "元婴", 10, 6000L, 7000, 950, 650, 40, 80, false, "破丹成婴，神魂具现，即便肉身受损亦可神游。", "炼器大殿、本命法宝铸造、三阶秘境", 3, 1),
        RealmInfo(5, "化神", 10, 24000L, 18000, 2600, 1800, 90, 75, false, "神魂化虚，沟通天地法则，举手投足引动天威。", "四阶丹药炼制、地阶神兵、宗门护法", 4, 1),
        RealmInfo(6, "出窍", 10, 90000L, 45000, 6800, 4800, 220, 70, false, "神识出窍，万里瞬息，可化神游之身扫荡秘境诸界。", "神识出窍·秘境神游扫荡、百草园自动灵药", 5, 1),
        RealmInfo(7, "分神", 10, 350000L, 110000, 17000, 12000, 550, 65, false, "分神化念，一念万化，可同时参悟多门神通大道。", "道侣仙缘结缘、掌门亲传指派", 6, 1),
        RealmInfo(8, "合体", 10, 1200000L, 280000, 42000, 30000, 1400, 60, false, "神与身合，返璞归真，天人合一，肉身元神近乎无瑕。", "天阶至尊心法、八阶秘境横行", 7, 1),
        RealmInfo(9, "洞虚", 10, 4500000L, 700000, 105000, 75000, 3500, 55, false, "洞穿虚妄，照见真我，空间法则收放自如。", "仙坊鬼市三层·仙界奇珍阁", 8, 1),
        RealmInfo(10, "大乘", 10, 18000000L, 1800000, 260000, 190000, 9000, 50, false, "凡界绝顶，道法大成，渡过天劫即可白日飞升！", "叩关仙门·白日飞升九天仙界", 9, 1),
        // 仙界 (Celestial Realm)
        RealmInfo(11, "游仙", 10, 80000000L, 5000000, 750000, 550000, 25000, 45, true, "初登仙界，位列仙班，褪尽凡胎，逍遥云海。", "仙界秘境·九天仙殿·仙品灵矿", 10, 1),
        RealmInfo(12, "真仙", 10, 300000000L, 15000000, 2200000, 1600000, 75000, 40, true, "修得真如自性，长生久视，与日月同辉。", "真仙灵宝、仙界极品灵脉", 11, 1),
        RealmInfo(13, "玄仙", 10, 1200000000L, 45000000, 6500000, 4800000, 220000, 35, true, "领悟大道玄机，一念生世界，一念灭星辰。", "绝世仙法演化、玄仙至宝", 12, 1),
        RealmInfo(14, "金仙", 10, 5000000000L, 140000000, 20000000, 15000000, 650000, 30, true, "大罗金仙，万劫不磨，跳出三界外，不在五行中。", "无上金仙道果、超品仙丹", 12, 3),
        RealmInfo(15, "仙君", 10, 20000000000L, 450000000, 65000000, 48000000, 2000000, 25, true, "统御诸天万界，仙君之威，令众仙俯首称臣。", "执掌诸天星辰、仙界至高权柄", 12, 6),
        RealmInfo(16, "仙尊", 10, 80000000000L, 1500000000, 200000000, 150000000, 6000000, 20, true, "仙道之巅，执掌天道轮转，俯瞰万古长河。", "仙尊万劫独尊、天道造化之境", 12, 10)
    )

    fun getRealm(id: Int): RealmInfo = realms.find { it.id == id } ?: realms.first()

    /**
     * Check if player meets the body realm requirement for cultivating / breaking through to target Realm
     */
    fun checkBodyRequirement(
        targetRealmId: Int,
        targetRealmStage: Int,
        currentBodyRealmId: Int,
        currentBodyRealmStage: Int
    ): Pair<Boolean, String> {
        val targetRealm = getRealm(targetRealmId)
        val reqBId = targetRealm.reqBodyRealmId
        val reqBStage = targetRealm.reqBodyRealmStage
        val reqBodyInfo = BodyRealmCatalog.getBodyRealm(reqBId)

        // Compare current body level with required
        val isMet = if (currentBodyRealmId > reqBId) {
            true
        } else if (currentBodyRealmId == reqBId) {
            currentBodyRealmStage >= reqBStage
        } else {
            false
        }

        val currentBodyInfo = BodyRealmCatalog.getBodyRealm(currentBodyRealmId)
        val reqDesc = "需肉身达到【${reqBodyInfo.name} ${reqBStage}阶】"
        val currentDesc = "当前肉身: 【${currentBodyInfo.name} ${currentBodyRealmStage}阶】"

        return if (isMet) {
            Pair(true, "$reqDesc (已满足 · $currentDesc)")
        } else {
            Pair(false, "$reqDesc (不足 · $currentDesc)")
        }
    }
}

/**
 * Body Refining Realm (肉身境界 - 凡人之躯至混元不灭)
 */
data class BodyRealmInfo(
    val id: Int,
    val name: String,
    val maxStage: Int = 10,
    val baseCultivationPerStage: Long,
    val hpBonus: Long,
    val defBonus: Long,
    val atkBonus: Long,
    val description: String = "",
    val reqRealmId: Int = 1,      // 淬体所需最低修炼境界
    val reqRealmStage: Int = 1    // 淬体所需最低修炼阶数
)

object BodyRealmCatalog {
    val bodyRealms = listOf(
        BodyRealmInfo(1, "凡人之躯", 10, 80L, 150, 10, 15, "肉体凡胎，杂质充斥，气血未通。", 1, 1),
        BodyRealmInfo(2, "炼体", 10, 300L, 600, 45, 60, "引天地灵气淬炼皮肉，筋骨齐鸣，气力倍增。", 1, 5),
        BodyRealmInfo(3, "凝血", 10, 1200L, 2000, 150, 200, "气血如汞，凝聚成浆，生机旺盛，伤势愈合神速。", 2, 1),
        BodyRealmInfo(4, "易筋", 10, 4500L, 6000, 450, 600, "拓展周身经脉，韧如蛟筋，真元运转流畅无滞。", 3, 1),
        BodyRealmInfo(5, "锻骨", 10, 18000L, 18000, 1400, 1800, "玉骨天成，金石难伤，骨髓晶莹剔透，承载万斤巨力。", 4, 1),
        BodyRealmInfo(6, "洗髓", 10, 70000L, 50000, 4000, 5000, "洗涤周身骨髓，清澈无垢，百病不生，百毒不侵。", 5, 1),
        BodyRealmInfo(7, "金身", 10, 280000L, 140000, 11000, 14000, "肉身如佛门金刚，刀枪不入，水火不侵，万法难伤。", 6, 1),
        BodyRealmInfo(8, "神力", 10, 1000000L, 400000, 30000, 40000, "体内蕴藏远古神魔之力，力拔山河，气吞万里。", 7, 1),
        BodyRealmInfo(9, "神勇", 10, 3800000L, 1100000, 85000, 110000, "气血狼烟冲九霄，一拳碎星辰，神勇盖世。", 8, 1),
        BodyRealmInfo(10, "淬体", 10, 15000000L, 3000000, 240000, 300000, "肉身蜕变为半仙之体，千锤百炼，硬抗天雷。", 9, 1),
        BodyRealmInfo(11, "破虚", 10, 60000000L, 9000000, 700000, 900000, "肉身可横渡虚空，破碎空间壁垒，不惧星空罡风。", 10, 1),
        BodyRealmInfo(12, "混元不灭", 10, 250000000L, 30000000, 2200000, 3000000, "混元一气，万劫不灭，与天地同寿，不坠轮回。", 11, 1)
    )

    fun getBodyRealm(id: Int): BodyRealmInfo = bodyRealms.find { it.id == id } ?: bodyRealms.first()

    /**
     * Check if player meets the cultivation realm requirement for quenching to target Body Realm
     */
    fun checkCultivationRequirement(
        targetBodyId: Int,
        targetBodyStage: Int,
        currentRealmId: Int,
        currentRealmStage: Int
    ): Pair<Boolean, String> {
        val targetBody = getBodyRealm(targetBodyId)
        val reqRId = targetBody.reqRealmId
        val reqRStage = targetBody.reqRealmStage
        val reqRealmInfo = RealmCatalog.getRealm(reqRId)

        val isMet = if (currentRealmId > reqRId) {
            true
        } else if (currentRealmId == reqRId) {
            currentRealmStage >= reqRStage
        } else {
            false
        }

        val currentRealmInfo = RealmCatalog.getRealm(currentRealmId)
        val reqDesc = "需修为境界达到【${reqRealmInfo.name} ${reqRStage}阶】"
        val currentDesc = "当前修为: 【${currentRealmInfo.name} ${currentRealmStage}阶】"

        return if (isMet) {
            Pair(true, "$reqDesc (已满足 · $currentDesc)")
        } else {
            Pair(false, "$reqDesc (不足 · $currentDesc)")
        }
    }
}

/**
 * Spiritual Roots (五行灵根)
 */
enum class ElementType(val displayName: String, val colorHex: Long) {
    METAL("金灵根", 0xFFFFD700), // Gold
    WOOD("木灵根", 0xFF4CAF50),  // Green
    WATER("水灵根", 0xFF29B6F6), // Azure
    FIRE("火灵根", 0xFFFF5722),  // Red
    EARTH("土灵根", 0xFF8D6E63)  // Brown
}

data class SpiritualRoot(
    val element: ElementType,
    var level: Int = 1
) {
    fun getTierName(): String {
        return when (level) {
            in 1..10 -> "废品 ${level}阶"
            in 11..20 -> "凡品 ${level - 10}阶"
            in 21..30 -> "下品 ${level - 20}阶"
            in 31..40 -> "中品 ${level - 30}阶"
            in 41..50 -> "上品 ${level - 40}阶"
            in 51..60 -> "极品 ${level - 50}阶"
            in 61..70 -> "完美 ${level - 60}阶"
            in 71..80 -> "先天 ${level - 70}阶"
            in 81..90 -> "仙品 ${level - 80}阶"
            else -> "神品 ${level - 90}阶"
        }
    }

    fun getUpgradeCost(): Long = (level * level * 25L)
    fun getStatBonus(): Double = 1.0 + (level * 0.03)
}

/**
 * Sect Models (门派系统 - 职位/俸禄/任务/宗门)
 */
enum class SectRank(val title: String, val salaryStones: Int, val salaryContribution: Int, val reqContribution: Int) {
    OUTER("外门弟子", 100, 20, 0),
    INNER("内门弟子", 300, 50, 300),
    DEACON("执事", 800, 120, 1000),
    GUARDIAN("护法", 2000, 280, 3000),
    ELDER("长老", 5000, 600, 8000),
    GRAND_ELDER("大长老", 12000, 1500, 20000),
    MASTER("掌门", 30000, 4000, 50000);

    fun next(): SectRank? {
        val nextIdx = ordinal + 1
        return if (nextIdx < entries.size) entries[nextIdx] else null
    }
}

data class SectTask(
    val id: String,
    val title: String,
    val desc: String,
    val reqRank: SectRank,
    val durationSeconds: Int,
    val rewardContribution: Int,
    val rewardStones: Int,
    val rewardExp: Long
)

data class SectInfo(
    val id: Int,
    val name: String,
    val stars: Int, // 1 to 9 stars
    val description: String,
    val specialBonus: String,
    val reqRealmId: Int, // 拜入门派所需境界
    val quitCostReputation: Int = 100, // 叛门消耗声望
    val isImmortalSect: Boolean = false
)

object SectCatalog {
    val sects = listOf(
        SectInfo(1, "逍遥派", 1, "凡界散修初创门派，注重修身养性，道法自然。", "修炼速度 +5%", 1, 50),
        SectInfo(2, "天海阁", 1, "坐落于东海之滨，善用水灵气滋养身心。", "气血上限 +10%", 1, 80),
        SectInfo(3, "纯阳宫", 2, "道门正宗纯阳一脉，剑法凌厉，剑意通明。", "攻击力 +12%", 2, 120),
        SectInfo(4, "青云宗", 2, "名门大派，藏经阁收录百家心法秘籍。", "灵气聚集速度 +15%", 2, 180),
        SectInfo(5, "九幽谷", 3, "隐秘魔宗，以战养战，行事诡谲。", "暴击伤害 +20%", 3, 250),
        SectInfo(6, "太乙仙宗", 4, "上古太乙真仙遗泽，炼丹造诣名震天下。", "炼丹成功率 +15%", 4, 400),
        SectInfo(7, "蜀山剑派", 5, "剑气纵横三万里，一剑光寒十九洲。", "极道攻击 +25%", 5, 600),
        SectInfo(8, "昆仑圣境", 6, "万山之祖，天地灵气汇聚之祖庭。", "渡劫成功率 +8%", 6, 900),
        SectInfo(9, "蓬莱仙岛", 7, "海市蜃楼海外仙岛，多通灵仙禽神兽。", "全属性 +20%", 7, 1500),
        // 仙界宗门
        SectInfo(10, "九重天庭", 8, "仙界执掌万界秩序之天庭，统领众仙官。", "仙界修炼速度 +40%", 11, 3000, true),
        SectInfo(11, "兜率仙宫", 9, "太上道祖之道场，执掌九转金丹无上大道。", "仙丹炼制/渡劫飞升 +50%", 11, 5000, true)
    )

    fun getSect(id: Int): SectInfo = sects.find { it.id == id } ?: sects.first()
}

/**
 * Skill / Secret Manual (功法秘籍 - 攻决/心法/身法/体诀/阵诀)
 */
enum class SkillType(val title: String) {
    CULTIVATION("心法(修炼速度)"),
    ATTACK("功法(攻击)"),
    DEFENSE("心经(防御)"),
    HP("体诀(气血)"),
    QI_SPEED("阵诀(灵气速度)")
}

data class CultivationSkill(
    val id: String,
    val name: String,
    val type: SkillType,
    val star: Int,
    val bonusValue: Long,
    val costContribution: Int,
    val description: String,
    val sectId: Int
)

object SkillCatalog {
    val allSkills = listOf(
        CultivationSkill("s1", "逍遥吐纳诀", SkillType.CULTIVATION, 1, 5, 100, "提升每秒修为获取 +5", 1),
        CultivationSkill("s2", "逍遥游龙剑", SkillType.ATTACK, 1, 40, 150, "增加基础攻击力 +40", 1),
        CultivationSkill("s3", "天海定波心经", SkillType.DEFENSE, 1, 30, 120, "增加基础防御力 +30", 2),
        CultivationSkill("s4", "天海长生诀", SkillType.HP, 1, 300, 180, "增加气血上限 +300", 2),
        CultivationSkill("s5", "纯阳天罡功", SkillType.ATTACK, 2, 120, 350, "纯阳烈火罡气，攻击 +120", 3),
        CultivationSkill("s6", "纯阳无极功", SkillType.CULTIVATION, 2, 18, 400, "纯阳真气循环，修为 +18/s", 3),
        CultivationSkill("s7", "青云聚灵大阵诀", SkillType.QI_SPEED, 2, 15, 450, "灵气凝聚速度 +15/s", 4),
        CultivationSkill("s8", "青云万剑诀", SkillType.ATTACK, 3, 300, 800, "万剑归宗，攻击 +300", 4),
        CultivationSkill("s9", "太乙先天混元经", SkillType.CULTIVATION, 4, 80, 1800, "玄门正宗，修为 +80/s", 6),
        CultivationSkill("s10", "太乙金光护体罩", SkillType.DEFENSE, 4, 500, 1500, "金光护体，防御 +500", 6),
        CultivationSkill("s11", "蜀山御剑伏魔真诀", SkillType.ATTACK, 5, 1200, 3500, "蜀山极道剑气，攻击 +1200", 7),
        CultivationSkill("s12", "昆仑玄天太虚经", SkillType.HP, 6, 15000, 6000, "太虚生玄气，气血 +15000", 8),
        CultivationSkill("s13", "九天神雷御剑大乘诀", SkillType.ATTACK, 7, 4500, 15000, "引动九天劫雷，攻击 +4500", 9),
        CultivationSkill("s14", "天庭万道朝真经", SkillType.CULTIVATION, 8, 800, 30000, "仙界天庭秘典，修为 +800/s", 10),
        CultivationSkill("s15", "太上九转大道长生经", SkillType.HP, 9, 100000, 80000, "道祖无上妙法，气血 +100000", 11)
    )
}

/**
 * Items & Equipment (物品/丹药/法宝/图纸)
 */
enum class ItemType(val title: String) {
    PILL_TRIBULATION("渡劫丹药"),
    PILL_EXP("修为丹药"),
    PILL_STAT("属性丹药"),
    EQUIP_WEAPON("法宝/武器"),
    EQUIP_ARMOR("道袍/护甲"),
    EQUIP_RING("储物仙戒"),
    MATERIAL("炼丹炼器材料"),
    RECIPE("丹方/图纸"),
    SEED("仙草种子")
}

data class Item(
    val id: String,
    val name: String,
    val type: ItemType,
    val quality: Int, // 1:凡, 2:灵, 3:玄, 4:地, 5:天, 6:神, 7:仙, 8:鸿蒙
    val description: String,
    val priceStones: Int,
    val tribulationRateBonus: Int = 0,
    val expGain: Long = 0,
    val hpBonus: Long = 0,
    val atkBonus: Long = 0,
    val defBonus: Long = 0,
    val stackable: Boolean = true
)

object ItemCatalog {
    val items = listOf(
        // 渡劫丹药
        Item("p_zhuji", "筑基丹", ItemType.PILL_TRIBULATION, 2, "筑基期破境必备灵丹，提升渡劫成功率 +10%", 120, tribulationRateBonus = 10),
        Item("p_jindan", "金丹渡厄丹", ItemType.PILL_TRIBULATION, 3, "金丹期渡劫避雷圣药，提升渡劫成功率 +10%", 400, tribulationRateBonus = 10),
        Item("p_yuanying", "元婴凝神丹", ItemType.PILL_TRIBULATION, 4, "元婴期凝结元神丹药，提升渡劫成功率 +10%", 1200, tribulationRateBonus = 10),
        Item("p_huashen", "化神破障丹", ItemType.PILL_TRIBULATION, 5, "化神期破除心魔之障，提升渡劫成功率 +10%", 3500, tribulationRateBonus = 10),
        Item("p_chuqiao", "出窍通玄丹", ItemType.PILL_TRIBULATION, 6, "出窍神识出体必备，提升渡劫成功率 +10%", 9000, tribulationRateBonus = 10),
        Item("p_dacheng", "大乘渡劫神丹", ItemType.PILL_TRIBULATION, 7, "抗击九九天劫仙丹，提升大乘期渡劫成功率 +15%", 30000, tribulationRateBonus = 15),
        
        // 修为丹药
        Item("p_juqi", "聚气丹", ItemType.PILL_EXP, 1, "吞服可直接获得 1,000 点修为", 50, expGain = 1000),
        Item("p_lingxu", "灵虚丹", ItemType.PILL_EXP, 3, "吞服可直接获得 20,000 点修为", 700, expGain = 20000),
        Item("p_jiuzhuan", "九转大还丹", ItemType.PILL_EXP, 6, "极品仙丹，吞服可直接获得 500,000 点修为", 15000, expGain = 500000),
        
        // 属性丹药
        Item("p_xicui", "洗髓丹", ItemType.PILL_STAT, 2, "洗精伐髓，永久增加 500 气血", 350, hpBonus = 500),
        Item("p_huntian", "浑天丹", ItemType.PILL_STAT, 3, "强化灵力，永久增加 80 攻击", 600, atkBonus = 80),
        Item("p_chunyang", "纯阳丹", ItemType.PILL_STAT, 3, "金刚之躯，永久增加 60 防御", 600, defBonus = 60),

        // 法宝武器
        Item("eq_wood_sword", "桃木飞剑", ItemType.EQUIP_WEAPON, 1, "初入道途所用木剑，攻击 +50", 150, atkBonus = 50, stackable = false),
        Item("eq_cyan_sword", "青锋剑", ItemType.EQUIP_WEAPON, 2, "精钢淬火灵剑，攻击 +180", 600, atkBonus = 180, stackable = false),
        Item("eq_purple_sword", "紫霄神剑", ItemType.EQUIP_WEAPON, 4, "蕴含雷电真意的法宝，攻击 +800", 3800, atkBonus = 800, stackable = false),
        Item("eq_immortal_sword", "斩仙飞剑", ItemType.EQUIP_WEAPON, 6, "上古诛仙残剑所铸，攻击 +3500", 22000, atkBonus = 3500, stackable = false),
        Item("eq_zhuxian_sword", "诛仙帝剑", ItemType.EQUIP_WEAPON, 8, "仙界无上帝兵，极道攻伐 +12,000", 80000, atkBonus = 12000, stackable = false),

        // 防具道袍
        Item("eq_linen_robe", "青麻道袍", ItemType.EQUIP_ARMOR, 1, "普通道家法衣，防御 +30, 气血 +200", 120, defBonus = 30, hpBonus = 200, stackable = false),
        Item("eq_cloud_robe", "流云仙裳", ItemType.EQUIP_ARMOR, 3, "以天蚕丝与灵气织就，防御 +150, 气血 +1500", 1400, defBonus = 150, hpBonus = 1500, stackable = false),
        Item("eq_dragon_armor", "太虚天龙甲", ItemType.EQUIP_ARMOR, 6, "真龙鳞片所锻造的神甲，防御 +900, 气血 +12000", 28000, defBonus = 900, hpBonus = 12000, stackable = false),
        Item("eq_celestial_armor", "九霄真王袍", ItemType.EQUIP_ARMOR, 8, "天庭帝君霞帔，防御 +3500, 气血 +60,000", 90000, defBonus = 3500, hpBonus = 60000, stackable = false),

        // 须弥戒指
        Item("eq_storage_ring", "须弥戒", ItemType.EQUIP_RING, 2, "内含一丈乾坤空间，全属性 +5%", 900, atkBonus = 50, defBonus = 50, hpBonus = 500, stackable = false),
        Item("eq_qiankun_ring", "乾坤太极戒", ItemType.EQUIP_RING, 5, "纳须弥于芥子，全属性 +15%", 12000, atkBonus = 400, defBonus = 400, hpBonus = 4000, stackable = false),

        // 炼造材料
        Item("mat_herb_1", "千年灵芝", ItemType.MATERIAL, 2, "炼制筑基丹、聚气丹的常见仙草", 30),
        Item("mat_herb_2", "玄天天元果", ItemType.MATERIAL, 4, "生长在仙峰之巅的珍稀灵果", 160),
        Item("mat_herb_3", "九叶劫厄草", ItemType.MATERIAL, 6, "吸纳九天雷劫气息生长的奇草", 850),
        Item("mat_ore_1", "玄铁精金", ItemType.MATERIAL, 2, "铸造下品飞剑与道甲的上好灵铁", 40),
        Item("mat_ore_2", "天外陨铁", ItemType.MATERIAL, 4, "自域外坠落的星辰陨铁", 220),
        Item("mat_ore_3", "太乙庚金", ItemType.MATERIAL, 6, "极度锋锐无匹的无上神金", 1100),

        // 仙草种子 (药园种植)
        Item("seed_herb_1", "【种子】千年灵芝", ItemType.SEED, 2, "可播种于洞府药园，收获丰厚灵芝", 25),
        Item("seed_herb_2", "【种子】玄天元果", ItemType.SEED, 4, "可播种于洞府药园，收获珍稀天元果", 120),
        Item("seed_herb_3", "【种子】九叶劫厄草", ItemType.SEED, 6, "可播种于洞府药园，收获极品劫厄草", 600),

        // 丹方与图纸
        Item("rec_zhuji", "【丹方】筑基丹", ItemType.RECIPE, 2, "记载筑基丹炼制之法的玉简", 500),
        Item("rec_jindan", "【丹方】金丹渡厄丹", ItemType.RECIPE, 3, "记载金丹渡厄丹炼制之法", 1600),
        Item("rec_juqi", "【丹方】聚气丹", ItemType.RECIPE, 1, "记载聚气丹炼制之法", 200),
        Item("rec_huntian", "【丹方】浑天丹", ItemType.RECIPE, 3, "记载浑天丹炼制之法", 1400),
        Item("rec_xicui", "【丹方】洗髓丹", ItemType.RECIPE, 2, "记载洗髓丹炼制之法", 800)
    )

    fun getItem(id: String): Item = items.find { it.id == id } ?: items.first()
}

/**
 * Alchemy Recipes (炼丹配方)
 */
data class AlchemyRecipe(
    val id: String,
    val resultItemId: String,
    val resultCount: Int = 1,
    val name: String,
    val reqAlchemyLevel: Int, // 1 to 9
    val reqHerbs: Map<String, Int>, // Herb Item ID -> Count
    val reqWood: Int,
    val costStones: Int,
    val expGain: Int
)

object AlchemyCatalog {
    val recipes = listOf(
        AlchemyRecipe("r_juqi", "p_juqi", 3, "聚气丹", 1, mapOf("mat_herb_1" to 2), 50, 20, 10),
        AlchemyRecipe("r_zhuji", "p_zhuji", 1, "筑基丹", 2, mapOf("mat_herb_1" to 5), 150, 80, 30),
        AlchemyRecipe("r_xicui", "p_xicui", 1, "洗髓丹", 2, mapOf("mat_herb_1" to 4, "mat_herb_2" to 1), 200, 100, 40),
        AlchemyRecipe("r_huntian", "p_huntian", 1, "浑天丹", 3, mapOf("mat_herb_2" to 3), 300, 200, 60),
        AlchemyRecipe("r_jindan", "p_jindan", 1, "金丹渡厄丹", 3, mapOf("mat_herb_1" to 8, "mat_herb_2" to 3), 500, 300, 80),
        AlchemyRecipe("r_yuanying", "p_yuanying", 1, "元婴凝神丹", 4, mapOf("mat_herb_2" to 6, "mat_herb_3" to 1), 1000, 600, 150),
        AlchemyRecipe("r_dacheng", "p_dacheng", 1, "大乘渡劫神丹", 6, mapOf("mat_herb_2" to 15, "mat_herb_3" to 5), 3000, 2000, 400)
    )
}

/**
 * Artifact Crafting Recipes (炼器配方)
 */
data class CraftingRecipe(
    val id: String,
    val resultItemId: String,
    val name: String,
    val reqSmithLevel: Int,
    val reqOres: Map<String, Int>,
    val reqIron: Int,
    val costStones: Int,
    val expGain: Int
)

object CraftingCatalog {
    val recipes = listOf(
        CraftingRecipe("c_wood_sword", "eq_wood_sword", "桃木飞剑", 1, mapOf(), 100, 50, 15),
        CraftingRecipe("c_cyan_sword", "eq_cyan_sword", "青锋剑", 2, mapOf("mat_ore_1" to 3), 300, 150, 40),
        CraftingRecipe("c_cloud_robe", "eq_cloud_robe", "流云仙裳", 3, mapOf("mat_ore_1" to 5, "mat_ore_2" to 1), 600, 350, 80),
        CraftingRecipe("c_purple_sword", "eq_purple_sword", "紫霄神剑", 4, mapOf("mat_ore_2" to 4), 1500, 800, 160),
        CraftingRecipe("c_dragon_armor", "eq_dragon_armor", "太虚天龙甲", 6, mapOf("mat_ore_2" to 10, "mat_ore_3" to 3), 5000, 3000, 400),
        CraftingRecipe("c_immortal_sword", "eq_immortal_sword", "斩仙飞剑", 7, mapOf("mat_ore_3" to 8), 10000, 6000, 800)
    )
}

/**
 * Market / Ghost Market / Treasure Pavilion (仙坊 / 鬼市 / 藏宝阁)
 */
data class MarketItem(
    val id: String,
    val itemId: String,
    val floor: Int, // 1: 凡人坊市, 2: 鬼市地摊, 3: 仙界奇珍
    val priceStones: Int,
    val reqRealmId: Int,
    val description: String
)

object MarketCatalog {
    val items = listOf(
        // 一层 凡人坊市
        MarketItem("m1", "p_juqi", 1, 50, 1, "聚气丹，增进练气修为"),
        MarketItem("m2", "p_zhuji", 1, 150, 1, "筑基破境丹药"),
        MarketItem("m3", "seed_herb_1", 1, 30, 1, "千年灵芝种子，洞府种植用"),
        MarketItem("m4", "mat_ore_1", 1, 45, 1, "玄铁精金，炼器基础材料"),
        MarketItem("m5", "rec_juqi", 1, 200, 1, "【丹方】聚气丹玉简"),

        // 二层 鬼市地摊
        MarketItem("m6", "p_jindan", 2, 450, 3, "金丹渡厄丹"),
        MarketItem("m7", "p_huntian", 2, 650, 3, "浑天丹，永久+80攻击"),
        MarketItem("m8", "p_chunyang", 2, 650, 3, "纯阳丹，永久+60防御"),
        MarketItem("m9", "seed_herb_2", 2, 140, 3, "玄天元果种子，药园种植"),
        MarketItem("m10", "rec_zhuji", 2, 500, 2, "【丹方】筑基丹玉简"),
        MarketItem("m11", "rec_huntian", 2, 1500, 3, "【丹方】浑天丹玉简"),
        MarketItem("m12", "eq_qiankun_ring", 2, 12000, 4, "乾坤太极戒，全属性+15%"),

        // 三层 仙界奇珍
        MarketItem("m13", "p_jiuzhuan", 3, 16000, 6, "九转大还丹，+50万修为"),
        MarketItem("m14", "p_dacheng", 3, 32000, 8, "大乘渡劫神丹"),
        MarketItem("m15", "seed_herb_3", 3, 700, 6, "九叶劫厄草种子"),
        MarketItem("m16", "mat_ore_3", 3, 1200, 6, "太乙庚金，顶级炼器材料"),
        MarketItem("m17", "eq_zhuxian_sword", 3, 85000, 9, "诛仙帝剑，极道神器")
    )
}

/**
 * Daoist Companion (仙友 / 道侣系统)
 */
data class CompanionInfo(
    val id: String,
    val name: String,
    val title: String,
    val gender: String,
    val description: String,
    val reqRealmId: Int,
    val dualCultivationBonusExpRate: Double, // 双修修为加成比率 (如 0.2 = +20%)
    val dualCultivationBonusQiRate: Double,  // 双修灵气加成
    val specialGiftName: String
)

object CompanionCatalog {
    val companions = listOf(
        CompanionInfo(
            id = "c_lengyue",
            name = "冷月仙子",
            title = "天海阁·冰清圣女",
            gender = "女",
            description = "天海阁闭关百年的绝美天骄，性格清冷如霜，唯对至诚向道者另眼相待。",
            reqRealmId = 2,
            dualCultivationBonusExpRate = 0.25,
            dualCultivationBonusQiRate = 0.30,
            specialGiftName = "九叶冰魄灵莲"
        ),
        CompanionInfo(
            id = "c_dantong",
            name = "太乙丹童·青玄",
            title = "太乙仙宗·灵药仙使",
            gender = "男",
            description = "天生通灵药体，熟读百草仙经，常年游历九州寻找罕见仙草灵药。",
            reqRealmId = 3,
            dualCultivationBonusExpRate = 0.35,
            dualCultivationBonusQiRate = 0.20,
            specialGiftName = "太玄凝神丹方"
        ),
        CompanionInfo(
            id = "c_lingfeng",
            name = "青云剑痴·凌风",
            title = "蜀山剑派·真传大弟子",
            gender = "男",
            description = "以剑为道，一柄本命青锋横扫妖邪，行侠仗义，豪迈不羁。",
            reqRealmId = 5,
            dualCultivationBonusExpRate = 0.50,
            dualCultivationBonusQiRate = 0.40,
            specialGiftName = "极道诛魔剑诀"
        ),
        CompanionInfo(
            id = "c_ling_er",
            name = "九尾天狐·灵儿",
            title = "十万大山·青丘帝姬",
            gender = "女",
            description = "上古九尾神狐遗裔，纯真灵动，掌天地幻化之术与无上魅灵神通。",
            reqRealmId = 7,
            dualCultivationBonusExpRate = 0.70,
            dualCultivationBonusQiRate = 0.60,
            specialGiftName = "天狐九转内丹"
        ),
        CompanionInfo(
            id = "c_yaochi",
            name = "瑶池圣女·紫菱",
            title = "九重天庭·瑶池金仙",
            gender = "女",
            description = "居于三十三天瑶池仙境，掌仙界甘霖与蟠桃盛宴，超凡脱俗。",
            reqRealmId = 11,
            dualCultivationBonusExpRate = 1.20,
            dualCultivationBonusQiRate = 1.00,
            specialGiftName = "九千年九品蟠桃"
        )
    )

    fun getCompanion(id: String): CompanionInfo = companions.find { it.id == id } ?: companions.first()
}

/**
 * Adventure Map (历练地图)
 */
data class AdventureMap(
    val id: Int,
    val name: String,
    val description: String,
    val reqRealmId: Int,
    val stepCount: Int,
    val enemyName: String,
    val enemyHp: Long,
    val enemyAtk: Long,
    val enemyDef: Long,
    val stoneRewardMin: Int,
    val stoneRewardMax: Int,
    val expReward: Long,
    val possibleHerbs: List<String>,
    val possibleOres: List<String>,
    val isImmortalMap: Boolean = false
)

object MapCatalog {
    val maps = listOf(
        AdventureMap(1, "坠仙谷", "凡间散修历练之险地，常有初阶妖兽出没。", 1, 10, "谷中赤尾妖狼", 600, 60, 20, 30, 80, 200, listOf("mat_herb_1"), listOf("mat_ore_1")),
        AdventureMap(2, "幽冥古窟", "阴森幽暗的古老洞窟，聚集了大量阴魂尸傀。", 2, 12, "幽冥尸将", 2500, 220, 100, 80, 200, 800, listOf("mat_herb_1"), listOf("mat_ore_1")),
        AdventureMap(3, "昆仑雪岭", "终年积雪不化的灵脉雪山，凶禽猛兽盘踞于此。", 3, 15, "雪岭冰晶巨猿", 8000, 700, 380, 250, 500, 3200, listOf("mat_herb_1", "mat_herb_2"), listOf("mat_ore_1", "mat_ore_2")),
        AdventureMap(4, "无极魔窟", "魔道余孽汇聚之深渊，危机四伏。", 4, 18, "无极魔门护法", 25000, 2200, 1200, 600, 1200, 12000, listOf("mat_herb_2"), listOf("mat_ore_2")),
        AdventureMap(5, "十万大山", "横跨南疆的苍莽古林，多有上古异种灵兽。", 5, 20, "上古吞天蟒", 75000, 6000, 3500, 1500, 3000, 40000, listOf("mat_herb_2", "mat_herb_3"), listOf("mat_ore_2", "mat_ore_3")),
        AdventureMap(6, "南天仙路", "通往仙界的登天古道，雷霆肆虐，仙傀镇守。", 8, 25, "镇界仙傀统领", 300000, 22000, 14000, 5000, 10000, 150000, listOf("mat_herb_3"), listOf("mat_ore_3")),
        // 仙界地图
        AdventureMap(7, "太皇天", "仙界九重天之一，仙雾缭绕，仙兽奔腾。", 11, 30, "太皇天巡界金仙", 1200000, 90000, 60000, 20000, 40000, 600000, listOf("mat_herb_3"), listOf("mat_ore_3"), true),
        AdventureMap(8, "凌霄仙域", "万仙朝拜之极乐仙阙，乃仙界至高秘境。", 13, 35, "凌霄镇殿天尊", 6000000, 400000, 280000, 60000, 120000, 3000000, listOf("mat_herb_3"), listOf("mat_ore_3"), true)
    )

    fun getMap(id: Int): AdventureMap = maps.find { it.id == id } ?: maps.first()
}

/**
 * Sweep & Raid Result Models (秘境神游扫荡结算)
 */
data class SweepRewardResult(
    val mapId: Int,
    val mapName: String,
    val times: Int,
    val totalStones: Int,
    val totalExp: Long,
    val herbsGained: Map<String, Int>,
    val oresGained: Map<String, Int>
)

object AdventureHelper {
    fun parseCheckpoints(str: String): Map<Int, Int> {
        if (str.isBlank()) return emptyMap()
        return try {
            str.split(",")
                .mapNotNull {
                    val parts = it.split(":")
                    if (parts.size == 2) parts[0].trim().toIntOrNull()?.let { mapId ->
                        parts[1].trim().toIntOrNull()?.let { step -> mapId to step }
                    } else null
                }.toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun serializeCheckpoints(map: Map<Int, Int>): String {
        return map.entries.joinToString(",") { "${it.key}:${it.value}" }
    }

    fun parseClearedMaps(str: String): Set<Int> {
        if (str.isBlank()) return setOf(1)
        return try {
            str.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
                .ifEmpty { setOf(1) }
        } catch (_: Exception) {
            setOf(1)
        }
    }

    fun serializeClearedMaps(set: Set<Int>): String {
        return set.joinToString(",")
    }

    fun calculateMaxDailySweeps(realmId: Int, extraPurchased: Int): Int {
        // Base 30 + (RealmId * 5) + ExtraPurchased * 10
        val base = 30 + (realmId * 5)
        return base + (extraPurchased * 10)
    }
}

