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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.CaveAbodeEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.ItemCatalog
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
import com.example.ui.theme.HdSurfaceElevated
import com.example.ui.theme.HdSurfaceVariant
import com.example.ui.theme.HdTextMuted
import com.example.ui.theme.HdTextPrimary
import com.example.ui.theme.HdTextSecondary
import com.example.ui.theme.HdTextWhite

@Composable
fun CaveAbodeScreen(
    caveAbode: CaveAbodeEntity?,
    profile: PlayerProfileEntity?,
    onRecruitServant: () -> Unit,
    onAdjustServant: (String, Int) -> Unit,
    onChangeGardenPlant: (String) -> Unit,
    onFertilizeGarden: () -> Unit,
    onUpgradeSpiritArray: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cave = caveAbode ?: return
    val p = profile ?: return

    val assignedServants = cave.foodServants + cave.woodServants + cave.ironServants + cave.stoneServants
    val freeServants = (cave.totalServants - assignedServants).coerceAtLeast(0)
    val recruitCostFood = 100L + (cave.totalServants * 50L)

    // Production calculation
    val foodProd = cave.foodServants * 2L
    val foodCons = cave.totalServants * 1L
    val netFood = foodProd - foodCons

    val woodProd = cave.woodServants * 2L
    val ironProd = cave.ironServants * 1L
    val stoneProd = cave.stoneServants * 1L

    val curHerb = ItemCatalog.getItem(cave.currentPlantSeedId)
    val plantProgress = if (cave.plantTargetSeconds > 0) {
        (cave.plantProgressSeconds.toFloat() / cave.plantTargetSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Cave Abode Hero Banner & Dashboard
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
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_cave_abode),
                            contentDescription = "洞府仙境",
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

                        // Floating title and array badge over banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HdPurplePrimary.copy(alpha = 0.9f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        tint = HdTextWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "仙山洞府 · 灵脉福地",
                                        color = HdTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "集日月造化 · 聚天地精粹",
                                        color = HdTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
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
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = HdPurpleOnContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "聚灵阵 ${p.spiritArrayLevel} 阶",
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
                        // 4 Core Cave Resources Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CaveResourcePill("灵谷", "${cave.food}", "${if (netFood >= 0) "+$netFood" else "$netFood"}/s", HdGoldPrimary, Modifier.weight(1f))
                            CaveResourcePill("灵木", "${cave.wood}", "+${woodProd}/s", HdJade, Modifier.weight(1f))
                            CaveResourcePill("玄铁", "${cave.iron}", "+${ironProd}/s", HdAzure, Modifier.weight(1f))
                            CaveResourcePill("玄石", "${cave.stone}", "+${stoneProd}/s", HdTextSecondary, Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 2. Herb Garden (洞府药园)
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
                                imageVector = Icons.Default.Grass,
                                contentDescription = null,
                                tint = HdJade,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "洞府药园 · 仙草培植",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(HdJade.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "药园 ${cave.herbGardenLevel} 阶 (单次产出 +${2 + cave.herbGardenLevel})",
                                color = HdJade,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "当前培育: 【${curHerb.name}】 (成熟自动收获至储物袋)",
                        color = HdTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "成长进度", color = HdTextMuted, fontSize = 11.sp)
                        Text(
                            text = "${cave.plantProgressSeconds}s / ${cave.plantTargetSeconds}s (${(plantProgress * 100).toInt()}%)",
                            color = HdJade,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { plantProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HdJade,
                        trackColor = HdSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Seed Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeedChoicePill("千年灵芝", "mat_herb_1", cave.currentPlantSeedId == "mat_herb_1", onChangeGardenPlant, Modifier.weight(1f))
                        SeedChoicePill("玄天元果", "mat_herb_2", cave.currentPlantSeedId == "mat_herb_2", onChangeGardenPlant, Modifier.weight(1f))
                        SeedChoicePill("九叶劫厄草", "mat_herb_3", cave.currentPlantSeedId == "mat_herb_3", onChangeGardenPlant, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rain spell button
                    val canRain = p.spiritQi >= 200L
                    Button(
                        onClick = onFertilizeGarden,
                        enabled = canRain,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("fertilize_garden_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canRain) HdAzure else HdBorder,
                            contentColor = if (canRain) HdTextWhite else HdTextMuted
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = if (canRain) HdTextWhite else HdTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (canRain) "降下甘霖灵雨 催熟60秒 (-200灵气)" else "灵气不足以施展灵雨 (需200灵气)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3. Daoist Servants Assignment (仙仆调配)
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
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = HdPurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "洞府道童仙仆",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "总仆役: ${cave.totalServants} (空闲: $freeServants)",
                            color = if (freeServants > 0) HdJade else HdTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Servant Assignment Rows
                    ServantRow("灵田耕种 (产出灵谷)", cave.foodServants, Icons.Default.Agriculture, HdGoldPrimary, freeServants > 0, { onAdjustServant("FOOD", -1) }, { onAdjustServant("FOOD", 1) })
                    ServantRow("古木采伐 (产出灵木)", cave.woodServants, Icons.Default.Forest, HdJade, freeServants > 0, { onAdjustServant("WOOD", -1) }, { onAdjustServant("WOOD", 1) })
                    ServantRow("玄铁淬炼 (产出玄铁)", cave.ironServants, Icons.Default.Hardware, HdAzure, freeServants > 0, { onAdjustServant("IRON", -1) }, { onAdjustServant("IRON", 1) })
                    ServantRow("灵山采矿 (产出玄石)", cave.stoneServants, Icons.Default.Terrain, HdTextSecondary, freeServants > 0, { onAdjustServant("STONE", -1) }, { onAdjustServant("STONE", 1) })

                    Spacer(modifier = Modifier.height(10.dp))

                    val canRecruit = cave.food >= recruitCostFood
                    Button(
                        onClick = onRecruitServant,
                        enabled = canRecruit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("recruit_servant_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canRecruit) HdPurplePrimary else HdBorder,
                            contentColor = if (canRecruit) HdTextWhite else HdTextMuted
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = if (canRecruit) "招募仙仆道童 (-$recruitCostFood 灵谷)" else "灵谷不足以招募 (需 $recruitCostFood 灵谷)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
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
private fun CaveResourcePill(title: String, amount: String, rate: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HdSurfaceVariant)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = HdTextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = amount, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = rate, color = HdTextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SeedChoicePill(name: String, seedId: String, isSelected: Boolean, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) HdJade.copy(alpha = 0.15f) else HdSurfaceVariant)
            .border(1.dp, if (isSelected) HdJade else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable { onSelect(seedId) }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = if (isSelected) HdJade else HdTextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ServantRow(
    title: String,
    count: Int,
    icon: ImageVector,
    iconColor: Color,
    canAdd: Boolean,
    onDec: () -> Unit,
    onInc: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = HdTextPrimary, fontSize = 12.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDec,
                enabled = count > 0,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "减少", tint = if (count > 0) HdTextPrimary else HdTextMuted, modifier = Modifier.size(14.dp))
            }

            Text(
                text = "$count",
                color = HdPurplePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            IconButton(
                onClick = onInc,
                enabled = canAdd,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "增加", tint = if (canAdd) HdTextPrimary else HdTextMuted, modifier = Modifier.size(14.dp))
            }
        }
    }
}
