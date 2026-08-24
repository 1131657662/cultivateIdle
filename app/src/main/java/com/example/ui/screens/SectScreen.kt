package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.db.LearnedSkillEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.CultivationSkill
import com.example.model.SectCatalog
import com.example.model.SectInfo
import com.example.model.SectRank
import com.example.model.SectTask
import com.example.model.SkillCatalog
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBackground
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdBorderStrong
import com.example.ui.theme.HdCrimson
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

@Composable
fun SectScreen(
    profile: PlayerProfileEntity?,
    learnedSkills: List<LearnedSkillEntity>,
    onPromoteRank: () -> Unit,
    onClaimSalary: () -> Unit,
    onEnterTrainingRoom: () -> Unit,
    onRequestMasterGuidance: () -> Unit,
    onCompleteTask: (SectTask) -> Unit,
    onLearnSkill: (String) -> Unit,
    onJoinSect: (Int) -> Unit,
    onLeaveSect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return
    val currentSect = SectCatalog.getSect(p.currentSectId)
    val rank = try { SectRank.valueOf(p.currentSectRank) } catch (e: Exception) { SectRank.OUTER }
    val nextRank = rank.next()

    var selectedTab by remember { mutableStateOf(0) } // 0: 宗门大殿, 1: 练功房与请教, 2: 藏经阁, 3: 宗门任务, 4: 九州宗门

    val sectSkills = SkillCatalog.allSkills.filter { it.sectId == currentSect.id }

    val defaultTasks = listOf(
        SectTask("t1", "清扫山门阶梯", "清理落叶与山野杂草", SectRank.OUTER, 10, 15, 40, 100),
        SectTask("t2", "照看灵草药田", "为宗门药圃除虫浇水", SectRank.OUTER, 30, 35, 100, 300),
        SectTask("t3", "巡查后山禁地", "防范魔教暗哨窥探", SectRank.INNER, 60, 80, 250, 1000),
        SectTask("t4", "护送宗门灵矿", "将精金原矿护送至分舵", SectRank.DEACON, 120, 200, 600, 3000),
        SectTask("t5", "镇压锁妖塔异动", "协同长老镇压暴动妖魔", SectRank.GUARDIAN, 300, 600, 2000, 10000)
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

            // 1. Sect Hero Banner & Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Scenic Hero Image with gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(135.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_sect_gate),
                            contentDescription = "宗门仙山",
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
                                            HdSurface.copy(alpha = 0.5f),
                                            HdSurface
                                        )
                                    )
                                )
                        )

                        // Floating Sect Title & Contribution badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentSect.name,
                                        color = HdTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(HdGoldPrimary.copy(alpha = 0.2f))
                                            .border(1.dp, HdGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${currentSect.stars}星宗门",
                                            color = HdGoldPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "尊位: ${rank.title} · ${currentSect.specialBonus}",
                                    color = HdTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdPurpleContainer)
                                    .border(1.dp, HdPurplePrimary.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = HdPurpleOnContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "贡献: ${p.sectContribution}",
                                        color = HdPurpleOnContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Promotion / Daily Salary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onClaimSalary,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("claim_salary_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = HdJade, contentColor = HdTextWhite),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(text = "领取俸禄 (+${rank.salaryStones}灵石)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (nextRank != null) {
                                val canPromote = p.sectContribution >= nextRank.reqContribution
                                OutlinedButton(
                                    onClick = onPromoteRank,
                                    enabled = canPromote,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("promote_rank_button"),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = if (canPromote) "晋升【${nextRank.title}】" else "晋升(需${nextRank.reqContribution}贡献)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sub Tabs Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TabPill("大殿", 0, selectedTab == 0) { selectedTab = 0 }
                TabPill("练功房", 1, selectedTab == 1) { selectedTab = 1 }
                TabPill("藏经阁", 2, selectedTab == 2) { selectedTab = 2 }
                TabPill("任务", 3, selectedTab == 3) { selectedTab = 3 }
                TabPill("投奔宗门", 4, selectedTab == 4) { selectedTab = 4 }
            }
        }

        when (selectedTab) {
            0 -> {
                // 大殿与叛门
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
                            Text(
                                text = "宗门主殿 · 道法传承",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentSect.description,
                                color = HdTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "宗门职位福利与俸禄一览:",
                                color = HdPurplePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            SectRank.entries.forEach { r ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${r.title} ${if (r == rank) "(当前)" else ""}",
                                        color = if (r == rank) HdPurplePrimary else HdTextPrimary,
                                        fontWeight = if (r == rank) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "俸禄: ${r.salaryStones}灵石 + ${r.salaryContribution}贡献",
                                        color = HdTextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Leave Sect Button
                            val canLeave = p.reputation >= currentSect.quitCostReputation && currentSect.id != 1
                            if (currentSect.id != 1) {
                                OutlinedButton(
                                    onClick = onLeaveSect,
                                    enabled = canLeave,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = HdCrimson, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (canLeave) "叛出宗门 (-${currentSect.quitCostReputation} 声望)" else "声望不足以叛门 (需 ${currentSect.quitCostReputation})",
                                        color = if (canLeave) HdCrimson else HdTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // 练功房与请教掌门
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
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = HdCrimson, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "门派练功房 (5倍修炼加速)",
                                        color = HdTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                if (p.trainingRoomSeconds > 0) {
                                    Text(
                                        text = "剩余: ${p.trainingRoomSeconds}秒",
                                        color = HdCrimson,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "消耗 150 宗门贡献，可踏入宗门顶级灵脉练功房，持续 10 分钟享受【500% 吐纳修炼速度】！",
                                color = HdTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val canEnterRoom = p.sectContribution >= 150
                            Button(
                                onClick = onEnterTrainingRoom,
                                enabled = canEnterRoom,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .testTag("enter_training_room_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canEnterRoom) HdCrimson else HdBorder,
                                    contentColor = if (canEnterRoom) HdTextWhite else HdTextMuted
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = if (canEnterRoom) "开启 10 分钟 5倍练功房 (-150 贡献)" else "宗门贡献不足 (需 150 贡献)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 掌门请教
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = HdPurplePrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "向掌门请教论道",
                                    color = HdTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "掌门乃得道高人，每隔数刻指点一次，可使道友醍醐灌顶，瞬增大量修为感悟！",
                                color = HdTextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = onRequestMasterGuidance,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("master_guidance_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = HdPurplePrimary, contentColor = HdTextWhite),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(text = "恭敬请教 掌门指点", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            2 -> {
                // 藏经阁
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        sectSkills.forEach { skill ->
                            val isLearned = learnedSkills.any { it.skillId == skill.id }
                            val canLearn = p.sectContribution >= skill.costContribution && !isLearned

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
                                        Text(
                                            text = "${skill.name} (${skill.star}星)",
                                            color = HdTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${skill.type.title}: ${skill.description}",
                                            color = HdTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Button(
                                        onClick = { onLearnSkill(skill.id) },
                                        enabled = canLearn,
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isLearned) HdSurfaceVariant else if (canLearn) HdPurplePrimary else HdBorder,
                                            contentColor = if (isLearned) HdTextMuted else if (canLearn) HdTextWhite else HdTextMuted
                                        ),
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = if (isLearned) "已参悟" else if (canLearn) "参悟 (-${skill.costContribution}贡献)" else "需${skill.costContribution}贡献",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // 宗门任务
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        defaultTasks.forEach { task ->
                            val isRankOk = rank.ordinal >= task.reqRank.ordinal

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
                                        Text(
                                            text = task.title,
                                            color = HdTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${task.desc} · 需【${task.reqRank.title}】",
                                            color = HdTextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "奖励: +${task.rewardContribution}贡献, +${task.rewardStones}灵石, +${task.rewardExp}修为",
                                            color = HdJade,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { onCompleteTask(task) },
                                        enabled = isRankOk,
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isRankOk) HdJade else HdBorder,
                                            contentColor = if (isRankOk) HdTextWhite else HdTextMuted
                                        ),
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = if (isRankOk) "执行任务" else "职位不足",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            4 -> {
                // 九州各大名门正派列表 (投奔宗门)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectCatalog.sects.forEach { s ->
                            val isCurrent = s.id == currentSect.id
                            val canJoin = p.realmId >= s.reqRealmId && !isCurrent

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = HdSurface),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) HdPurplePrimary else HdBorder)
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
                                            Text(
                                                text = "${s.name} (${s.stars}星)",
                                                color = HdTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            if (isCurrent) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "【当前宗门】", color = HdPurplePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(text = s.specialBonus, color = HdGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        Text(text = s.description, color = HdTextSecondary, fontSize = 10.sp)
                                    }

                                    if (!isCurrent) {
                                        Button(
                                            onClick = { onJoinSect(s.id) },
                                            enabled = canJoin,
                                            modifier = Modifier.height(34.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (canJoin) HdPurplePrimary else HdBorder,
                                                contentColor = if (canJoin) HdTextWhite else HdTextMuted
                                            ),
                                            shape = RoundedCornerShape(100.dp)
                                        ) {
                                            Text(
                                                text = if (canJoin) "拜入山门" else "境界不足",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
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
private fun TabPill(title: String, index: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isSelected) HdPurpleContainer else HdSurfaceVariant)
            .border(1.dp, if (isSelected) HdPurplePrimary else Color.Transparent, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) HdPurpleOnContainer else HdTextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}
