package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.InventoryItemEntity
import com.example.data.db.PlayerProfileEntity
import com.example.model.CompanionCatalog
import com.example.model.CompanionInfo
import com.example.model.ItemCatalog
import com.example.model.ItemType
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

@Composable
fun InventoryScreen(
    profile: PlayerProfileEntity?,
    inventory: List<InventoryItemEntity>,
    onUseItem: (InventoryItemEntity) -> Unit,
    onEquipItem: (InventoryItemEntity) -> Unit,
    onUnequipItem: (String) -> Unit,
    onSellItem: (InventoryItemEntity) -> Unit,
    onMeetCompanion: (String) -> Unit,
    onGiftCompanion: (String) -> Unit,
    onDualCultivate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return

    var mainTab by remember { mutableIntStateOf(0) } // 0: 储物袋, 1: 仙友道侣
    var inventoryFilter by remember { mutableIntStateOf(0) } // 0: 全部, 1: 灵丹, 2: 法宝, 3: 材料

    val filteredItems = when (inventoryFilter) {
        1 -> inventory.filter {
            val item = ItemCatalog.getItem(it.itemId)
            item.type in listOf(ItemType.PILL_TRIBULATION, ItemType.PILL_EXP, ItemType.PILL_STAT)
        }
        2 -> inventory.filter {
            val item = ItemCatalog.getItem(it.itemId)
            item.type in listOf(ItemType.EQUIP_WEAPON, ItemType.EQUIP_ARMOR, ItemType.EQUIP_RING)
        }
        3 -> inventory.filter {
            val item = ItemCatalog.getItem(it.itemId)
            item.type in listOf(ItemType.MATERIAL, ItemType.RECIPE, ItemType.SEED)
        }
        else -> inventory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HdBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Main Category Switch: 储物袋 VS 仙友道侣
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (mainTab == 0) HdPurplePrimary else HdSurface)
                        .border(1.dp, if (mainTab == 0) HdPurplePrimary else HdBorder, RoundedCornerShape(12.dp))
                        .clickable { mainTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = if (mainTab == 0) HdTextWhite else HdTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "乾坤储物袋 (${inventory.size}格)",
                            color = if (mainTab == 0) HdTextWhite else HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (mainTab == 1) HdPurplePrimary else HdSurface)
                        .border(1.dp, if (mainTab == 1) HdPurplePrimary else HdBorder, RoundedCornerShape(12.dp))
                        .clickable { mainTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (mainTab == 1) HdTextWhite else HdCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "仙友道侣 · 结缘双修",
                            color = if (mainTab == 1) HdTextWhite else HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (mainTab == 0) {
            // ================= 储物袋 =================
            // Equipped Equipment Card
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
                            text = "当前佩戴法宝神兵",
                            color = HdTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            EquipSlotPill("神兵", p.equippedWeaponId, { onUnequipItem("WEAPON") }, Modifier.weight(1f))
                            EquipSlotPill("道袍", p.equippedArmorId, { onUnequipItem("ARMOR") }, Modifier.weight(1f))
                            EquipSlotPill("仙戒", p.equippedRingId, { onUnequipItem("RING") }, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Inventory Filter Pills
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    InvFilterPill("全部", 0, inventoryFilter == 0) { inventoryFilter = 0 }
                    InvFilterPill("灵丹妙药", 1, inventoryFilter == 1) { inventoryFilter = 1 }
                    InvFilterPill("法宝神装", 2, inventoryFilter == 2) { inventoryFilter = 2 }
                    InvFilterPill("天材地宝", 3, inventoryFilter == 3) { inventoryFilter = 3 }
                }
            }

            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "储物袋内空空如也，快去历练或仙坊收集吧！", color = HdTextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                items(filteredItems) { invItem ->
                    val item = ItemCatalog.getItem(invItem.itemId)
                    val isEquipped = p.equippedWeaponId == item.id || p.equippedArmorId == item.id || p.equippedRingId == item.id
                    val isUsable = item.type in listOf(ItemType.PILL_EXP, ItemType.PILL_STAT, ItemType.SEED)
                    val isEquippable = item.type in listOf(ItemType.EQUIP_WEAPON, ItemType.EQUIP_ARMOR, ItemType.EQUIP_RING)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = HdSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEquipped) HdPurplePrimary else HdBorder)
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
                                        text = item.name,
                                        color = HdTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "x${invItem.count}",
                                        color = HdPurplePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    if (isEquipped) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(HdPurpleContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "已穿戴", color = HdPurpleOnContainer, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = item.description, color = HdTextSecondary, fontSize = 11.sp)
                                Text(text = "典当估值: ${(item.priceStones * 0.7).toInt()} 灵石", color = HdTextMuted, fontSize = 10.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (isUsable) {
                                    Button(
                                        onClick = { onUseItem(invItem) },
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = HdJade, contentColor = HdTextWhite),
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(text = if (item.type == ItemType.SEED) "播种" else "吞服", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else if (isEquippable && !isEquipped) {
                                    Button(
                                        onClick = { onEquipItem(invItem) },
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = HdPurplePrimary, contentColor = HdTextWhite),
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(text = "佩戴", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { onSellItem(invItem) },
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(text = "典当", fontSize = 10.sp, color = HdGoldPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ================= 仙友道侣结交 =================
            item {
                Text(
                    text = "修仙道侣同参造化，每日双修可获巨额修为与灵气反哺！",
                    color = HdTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            items(CompanionCatalog.companions) { comp ->
                val isCompanion = p.companionId == comp.id
                val canMeet = p.realmId >= comp.reqRealmId
                val reqRealmName = RealmCatalog.getRealm(comp.reqRealmId).name

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HdSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompanion) HdCrimson else HdBorder)
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
                                Text(
                                    text = comp.name,
                                    color = HdTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = comp.title,
                                    color = HdPurplePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (isCompanion) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(HdCrimson.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "结缘道侣 (亲密: ${p.companionAffection})",
                                        color = HdCrimson,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = if (canMeet) "可结识" else "需【$reqRealmName】",
                                    color = if (canMeet) HdJade else HdTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = comp.description, color = HdTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "双修加成: 修为获取 +${(comp.dualCultivationBonusExpRate * 100).toInt()}%, 灵气转化 +${(comp.dualCultivationBonusQiRate * 100).toInt()}%",
                            color = HdJade,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isCompanion) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onDualCultivate(comp.id) },
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("dual_cultivate_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = HdCrimson, contentColor = HdTextWhite),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(text = "道侣双修 打坐吐纳", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                val canGift = p.spiritStones >= 500
                                OutlinedButton(
                                    onClick = { onGiftCompanion(comp.id) },
                                    enabled = canGift,
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = if (canGift) "赠送天山灵茶 (-500灵石)" else "缺灵石 (500)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { onMeetCompanion(comp.id) },
                                enabled = canMeet,
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canMeet) HdPurplePrimary else HdBorder,
                                    contentColor = if (canMeet) HdTextWhite else HdTextMuted
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(text = if (canMeet) "前往拜访 结为道侣" else "境界不足以结缘", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
private fun EquipSlotPill(slotName: String, itemId: String?, onUnequip: () -> Unit, modifier: Modifier = Modifier) {
    val item = itemId?.let { ItemCatalog.getItem(it) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HdSurfaceVariant)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = slotName, color = HdTextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            if (item != null) {
                Text(
                    text = item.name,
                    color = HdPurplePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "卸下",
                    color = HdCrimson,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onUnequip() }
                )
            } else {
                Text(text = "未佩戴", color = HdTextMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun InvFilterPill(title: String, index: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isSelected) HdPurpleContainer else HdSurfaceVariant)
            .border(1.dp, if (isSelected) HdPurplePrimary else Color.Transparent, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
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
