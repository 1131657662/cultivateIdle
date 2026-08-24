package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PlayerProfileEntity
import com.example.model.RealmCatalog
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
import com.example.viewmodel.PlayerStats

@Composable
fun AscensionScreen(
    profile: PlayerProfileEntity?,
    stats: PlayerStats,
    onAscend: (String) -> Unit,
    onExchangeCrystals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return
    val realm = RealmCatalog.getRealm(p.realmId)

    // Requirements for Ascension
    val reqRealmOk = p.realmId >= 9 // 大乘期
    val reqHpOk = stats.maxHp >= 50000
    val reqAtkOk = stats.attack >= 10000
    val canAscendNow = reqRealmOk && (reqHpOk || reqAtkOk)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Ascension Status Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (p.isAscended) HdPurplePrimary else HdBorder
                )
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
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = "飞升",
                                tint = HdPurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (p.isAscended) "太皇天 · 仙界天庭" else "南天门 · 飞升仙界",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (p.isAscended) HdPurpleContainer else HdSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (p.isAscended) "位列仙班" else "凡尘渡劫中",
                                color = if (p.isAscended) HdPurpleOnContainer else HdTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (p.isAscended)
                            "道友已褪去凡胎，白日飞升！居于三十三天凌霄仙阙，享仙界无穷仙气与极品仙晶！"
                        else
                            "大道三千，九死一生。唯有突破大乘极境，凝聚浑厚气血真元，方可叩开南天仙门，羽化登仙！",
                        color = HdTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        if (!p.isAscended) {
            // 2. Pre-Ascension Checklist
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "飞升登仙条件",
                            color = HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        CheckConditionRow("道法境界", "达到【大乘期】以上", reqRealmOk, "当前: ${realm.name}")
                        CheckConditionRow("浑厚气血", "生命上限达到 50,000", reqHpOk, "当前: ${stats.maxHp}")
                        CheckConditionRow("真元攻伐", "攻击力达到 10,000", reqAtkOk, "当前: ${stats.attack}")
                    }
                }
            }

            // 3. Three Pathways to Ascension
            item {
                Text(
                    text = "三大飞升途径",
                    color = HdTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            // Pathway 1: 闯仙门
            item {
                AscensionPathwayCard(
                    title = "勇闯南天仙门 (破阵直上)",
                    description = "硬闯九天仙门守卫与上古雷灵，获得大量真元洗礼与仙界声望！",
                    tag = "战力要求高",
                    buttonText = "叩关闯仙门",
                    color = HdPurplePrimary,
                    enabled = canAscendNow,
                    onClick = { onAscend("GATE") },
                    testTag = "ascend_gate_button"
                )
            }

            // Pathway 2: 肉身成圣
            item {
                AscensionPathwayCard(
                    title = "肉身成圣 (九天灭世雷劫)",
                    description = "引动灭世紫霄神雷淬炼金身，肉身成圣后获得永久防御与生命翻倍！",
                    tag = "淬体极道",
                    buttonText = "肉身渡雷劫",
                    color = HdCrimson,
                    enabled = canAscendNow,
                    onClick = { onAscend("BODY") },
                    testTag = "ascend_body_button"
                )
            }

            // Pathway 3: 仙界接引
            item {
                AscensionPathwayCard(
                    title = "仙尊接引 (功德圆满)",
                    description = "受上界大能符诏接引，稳妥飞升仙界，获赠接引仙晶大礼包！",
                    tag = "祥云接引",
                    buttonText = "受诏飞升",
                    color = HdAzure,
                    enabled = canAscendNow,
                    onClick = { onAscend("GUIDE") },
                    testTag = "ascend_guide_button"
                )
            }
        } else {
            // 4. Immortal Realm Features (Post-Ascension)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HdPurplePrimary)
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
                            Text(
                                text = "仙界天宝阁 · 仙晶凝聚",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdPurpleContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "仙晶: ${p.celestialCrystals}",
                                    color = HdPurpleOnContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "在仙界中，可将凡间极品灵石凝聚为至尊仙晶，仙晶可在仙界百宝阁兑换神品丹方与诛仙神剑！",
                            color = HdTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onExchangeCrystals,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("exchange_crystals_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HdPurplePrimary,
                                contentColor = HdTextWhite
                            ),
                            shape = RoundedCornerShape(100.dp),
                            enabled = p.spiritStones >= 10000
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = HdTextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "凝聚仙晶 (消耗 10,000 灵石 ➔ +10 仙晶)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "仙界九天仙域",
                            color = HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        ImmortalDomainRow("太皇黄曾天", "已解锁，仙气浓郁度 +200%")
                        ImmortalDomainRow("太明玉完天", "需金仙境界解锁")
                        ImmortalDomainRow("清明何童天", "需仙帝境界解锁")
                        ImmortalDomainRow("大罗天 · 凌霄殿", "需鸿蒙圣尊境界解锁")
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
private fun CheckConditionRow(
    title: String,
    desc: String,
    isMet: Boolean,
    currentValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HdSurfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (isMet) HdJade else HdCrimson,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, color = HdTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = desc, color = HdTextSecondary, fontSize = 11.sp)
            }
        }

        Text(
            text = currentValue,
            color = if (isMet) HdJade else HdCrimson,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun AscensionPathwayCard(
    title: String,
    description: String,
    tag: String,
    buttonText: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = HdSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
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
                Text(
                    text = title,
                    color = HdTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = tag, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = HdTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag(testTag),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled) color else HdBorder,
                    contentColor = if (enabled) HdTextWhite else HdTextMuted
                ),
                shape = RoundedCornerShape(100.dp),
                enabled = enabled
            ) {
                Text(
                    text = if (enabled) buttonText else "未满足飞升条件",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ImmortalDomainRow(name: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HdSurfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, color = HdTextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        Text(text = status, color = HdPurplePrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
