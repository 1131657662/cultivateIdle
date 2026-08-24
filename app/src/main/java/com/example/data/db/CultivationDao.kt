package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivationDao {
    // Player Profile
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getPlayerProfileDirect(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    // Cave Abode
    @Query("SELECT * FROM cave_abode WHERE id = 1 LIMIT 1")
    fun getCaveAbode(): Flow<CaveAbodeEntity?>

    @Query("SELECT * FROM cave_abode WHERE id = 1 LIMIT 1")
    suspend fun getCaveAbodeDirect(): CaveAbodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCaveAbode(caveAbode: CaveAbodeEntity)

    // Inventory
    @Query("SELECT * FROM inventory_items WHERE count > 0")
    fun getInventoryItems(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE count > 0")
    suspend fun getInventoryItemsDirect(): List<InventoryItemEntity>

    @Query("SELECT * FROM inventory_items WHERE itemId = :itemId LIMIT 1")
    suspend fun getItemCount(itemId: String): InventoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: InventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE itemId = :itemId")
    suspend fun deleteItem(itemId: String)

    // Learned Skills
    @Query("SELECT * FROM learned_skills")
    fun getLearnedSkills(): Flow<List<LearnedSkillEntity>>

    @Query("SELECT * FROM learned_skills")
    suspend fun getLearnedSkillsDirect(): List<LearnedSkillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearnedSkill(skill: LearnedSkillEntity)

    // Logs
    @Query("SELECT * FROM cultivation_logs ORDER BY id DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<CultivationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CultivationLogEntity)

    @Query("DELETE FROM cultivation_logs")
    suspend fun clearLogs()
}
