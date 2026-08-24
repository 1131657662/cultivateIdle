package com.example.data

import com.example.data.db.AppDatabase
import com.example.data.db.CaveAbodeEntity
import com.example.data.db.CultivationDao
import com.example.data.db.CultivationLogEntity
import com.example.data.db.InventoryItemEntity
import com.example.data.db.LearnedSkillEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.ItemCatalog
import kotlinx.coroutines.flow.Flow

class CultivationRepository(private val dao: CultivationDao) {

    val playerProfile: Flow<PlayerProfileEntity?> = dao.getPlayerProfile()
    val caveAbode: Flow<CaveAbodeEntity?> = dao.getCaveAbode()
    val inventoryItems: Flow<List<InventoryItemEntity>> = dao.getInventoryItems()
    val learnedSkills: Flow<List<LearnedSkillEntity>> = dao.getLearnedSkills()
    val recentLogs: Flow<List<CultivationLogEntity>> = dao.getRecentLogs()

    suspend fun ensureInitialized() {
        var profile = dao.getPlayerProfileDirect()
        if (profile == null) {
            profile = PlayerProfileEntity()
            dao.insertOrUpdateProfile(profile)

            // Initial starter inventory
            dao.insertOrUpdateItem(InventoryItemEntity("p_juqi", 5))
            dao.insertOrUpdateItem(InventoryItemEntity("p_zhuji", 2))
            dao.insertOrUpdateItem(InventoryItemEntity("mat_herb_1", 10))
            dao.insertOrUpdateItem(InventoryItemEntity("mat_ore_1", 10))
            dao.insertOrUpdateItem(InventoryItemEntity("eq_wood_sword", 1))
            dao.insertOrUpdateItem(InventoryItemEntity("eq_linen_robe", 1))

            // Starter skill
            dao.insertLearnedSkill(LearnedSkillEntity("s1", 1))

            dao.insertLog(
                CultivationLogEntity(
                    content = "初涉仙途，偶得残破玉简与下品灵剑，于灵山深处开启凡人修真之道。",
                    type = "BREAKTHROUGH"
                )
            )
        }

        var cave = dao.getCaveAbodeDirect()
        if (cave == null) {
            cave = CaveAbodeEntity()
            dao.insertOrUpdateCaveAbode(cave)
        }
    }

    suspend fun saveProfile(profile: PlayerProfileEntity) {
        dao.insertOrUpdateProfile(profile.copy(lastSaveTimestamp = System.currentTimeMillis()))
    }

    suspend fun saveCaveAbode(cave: CaveAbodeEntity) {
        dao.insertOrUpdateCaveAbode(cave)
    }

    suspend fun addItem(itemId: String, count: Int) {
        val existing = dao.getItemCount(itemId)
        val currentCount = existing?.count ?: 0
        val newCount = currentCount + count
        if (newCount > 0) {
            dao.insertOrUpdateItem(InventoryItemEntity(itemId, newCount))
        } else {
            dao.deleteItem(itemId)
        }
    }

    suspend fun removeItem(itemId: String, count: Int): Boolean {
        val existing = dao.getItemCount(itemId) ?: return false
        if (existing.count < count) return false
        val newCount = existing.count - count
        if (newCount > 0) {
            dao.insertOrUpdateItem(InventoryItemEntity(itemId, newCount))
        } else {
            dao.deleteItem(itemId)
        }
        return true
    }

    suspend fun learnSkill(skillId: String) {
        dao.insertLearnedSkill(LearnedSkillEntity(skillId, 1))
    }

    suspend fun addLog(content: String, type: String = "INFO") {
        dao.insertLog(CultivationLogEntity(content = content, type = type))
    }

    suspend fun getProfileDirect(): PlayerProfileEntity =
        dao.getPlayerProfileDirect() ?: PlayerProfileEntity()

    suspend fun getCaveAbodeDirect(): CaveAbodeEntity =
        dao.getCaveAbodeDirect() ?: CaveAbodeEntity()

    suspend fun getInventoryDirect(): List<InventoryItemEntity> =
        dao.getInventoryItemsDirect()

    suspend fun getSkillsDirect(): List<LearnedSkillEntity> =
        dao.getLearnedSkillsDirect()

    suspend fun getLearnedSkillsDirect(): List<LearnedSkillEntity> =
        dao.getLearnedSkillsDirect()
}
