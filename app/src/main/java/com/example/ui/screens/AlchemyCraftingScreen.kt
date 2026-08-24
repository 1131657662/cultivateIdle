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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CaveAbodeEntity
import com.example.data.db.InventoryItemEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.AlchemyCatalog
import com.example.model.AlchemyRecipe
import com.example.model.CraftingCatalog
import com.example.model.CraftingRecipe
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
import com.example.ui.theme.HdSurfaceVariant
import com.example.ui.theme.HdTextMuted
import com.example.ui.theme.HdTextPrimary
import com.example.ui.theme.HdTextSecondary
import com.example.ui.theme.HdTextWhite

@Composable
fun AlchemyCraftingScreen(
    profile: PlayerProfileEntity?,
    caveAbode: CaveAbodeEntity?,
    inventory: List<InventoryItemEntity>,
    onCraftAlchemy: (AlchemyRecipe) -> Unit,
    onCraftArtifact: (CraftingRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return
    val cave = caveAbode ?: return

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("三昧炼丹房", "玄真炼器鼎")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Mastery Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HdSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HdBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Default.Science else Icons.Default.Build,
                                contentDescription = null,
                                tint = if (selectedTab == 0) HdJade else HdPurplePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedTab == 0) "丹道造诣：${p.alchemyLevel}品炼丹师" else "器道造诣：${p.smithLevel}品炼器大师",
                                color = HdTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            text = if (selectedTab == 0) "经验: ${p.alchemyExp} / ${p.alchemyLevel * 100} · 药材与木材充足即可开炉"
                                   else "经验: ${p.smithExp} / ${p.smithLevel * 100} · 矿石与陨铁充足即可锻造",
                            color = HdTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Resources available badges
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(HdAzure.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "木材: ${cave.wood}",
                                color = HdAzure,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(HdPurpleContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "陨铁: ${cave.iron}",
                                color = HdPurpleOnContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 2. High Density Tab Bar
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = HdSurface,
                contentColor = HdPurplePrimary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = HdPurplePrimary
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) HdPurplePrimary else HdTextSecondary
                            )
                        }
                    )
                }
            }
        }

        // 3. Tab Content
        if (selectedTab == 0) {
            // Alchemy Recipes
            items(AlchemyCatalog.recipes) { recipe ->
                val resultItem = ItemCatalog.getItem(recipe.resultItemId)
                var hasAllHerbs = true
                val herbListDesc = recipe.reqHerbs.entries.joinToString(", ") { (herbId, count) ->
                    val herbItem = ItemCatalog.getItem(herbId)
                    val owned = inventory.find { it.itemId == herbId }?.count ?: 0
                    if (owned < count) hasAllHerbs = false
                    "${herbItem.name} x$count(有$owned)"
                }

                val canCraft = hasAllHerbs && cave.wood >= recipe.reqWood && p.spiritStones >= recipe.costStones

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (canCraft) HdJade else HdBorder
                    )
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = recipe.name,
                                    tint = HdJade,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = recipe.name,
                                    color = HdTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(HdSurfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "出丹 x${recipe.resultCount}",
                                        color = HdTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Text(
                                text = resultItem.description,
                                color = HdTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Requirements
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(HdSurfaceVariant)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "需要药材: $herbListDesc",
                                color = if (hasAllHerbs) HdTextPrimary else HdCrimson,
                                fontSize = 11.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "炉火木材: ${recipe.reqWood} (有${cave.wood})",
                                    color = if (cave.wood >= recipe.reqWood) HdTextSecondary else HdCrimson,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "消耗灵石: ${recipe.costStones}",
                                    color = if (p.spiritStones >= recipe.costStones) HdPurplePrimary else HdCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onCraftAlchemy(recipe) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("craft_alchemy_${recipe.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canCraft) HdJade else HdBorder,
                                contentColor = if (canCraft) HdTextWhite else HdTextMuted
                            ),
                            shape = RoundedCornerShape(100.dp),
                            enabled = canCraft
                        ) {
                            Text(
                                text = if (canCraft) "引火开炉 炼制灵丹" else "原料不足",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Artifact Crafting Recipes
            items(CraftingCatalog.recipes) { recipe ->
                val resultItem = ItemCatalog.getItem(recipe.resultItemId)
                var hasAllOres = true
                val oreListDesc = if (recipe.reqOres.isEmpty()) "无需稀有矿石" else recipe.reqOres.entries.joinToString(", ") { (oreId, count) ->
                    val oreItem = ItemCatalog.getItem(oreId)
                    val owned = inventory.find { it.itemId == oreId }?.count ?: 0
                    if (owned < count) hasAllOres = false
                    "${oreItem.name} x$count(有$owned)"
                }

                val canCraft = hasAllOres && cave.iron >= recipe.reqIron && p.spiritStones >= recipe.costStones

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (canCraft) HdPurplePrimary else HdBorder
                    )
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Hardware,
                                    contentDescription = recipe.name,
                                    tint = HdPurplePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = recipe.name,
                                    color = HdTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Text(
                                text = resultItem.description,
                                color = HdTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Requirements
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(HdSurfaceVariant)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "需要矿石: $oreListDesc",
                                color = if (hasAllOres) HdTextPrimary else HdCrimson,
                                fontSize = 11.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "需要陨铁: ${recipe.reqIron} (有${cave.iron})",
                                    color = if (cave.iron >= recipe.reqIron) HdTextSecondary else HdCrimson,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "消耗灵石: ${recipe.costStones}",
                                    color = if (p.spiritStones >= recipe.costStones) HdPurplePrimary else HdCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onCraftArtifact(recipe) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("craft_artifact_${recipe.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canCraft) HdPurplePrimary else HdBorder,
                                contentColor = if (canCraft) HdTextWhite else HdTextMuted
                            ),
                            shape = RoundedCornerShape(100.dp),
                            enabled = canCraft
                        ) {
                            Text(
                                text = if (canCraft) "淬火锻打 铸就法宝" else "材料不足",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
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
