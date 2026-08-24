package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.model.BodyRealmCatalog
import com.example.model.RealmCatalog
import com.example.model.SectRank
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBorder
import com.example.ui.theme.HdBorderStrong
import com.example.ui.theme.HdConsoleAlert
import com.example.ui.theme.HdCrimson
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
fun TopBarHeader(
    profile: PlayerProfileEntity?,
    stats: PlayerStats,
    onOpenRealmGuide: ((tab: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val p = profile ?: return
    val realm = RealmCatalog.getRealm(p.realmId)
    val body = BodyRealmCatalog.getBodyRealm(p.bodyRealmId)
    val sectRank = try { SectRank.valueOf(p.currentSectRank).title } catch (e: Exception) { "外门弟子" }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .testTag("top_bar_header"),
        color = HdSurfaceVariant,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row 1: High Density Avatar + Name + Realm Badge + Currency Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Daoist Profile Summary
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(HdPurplePrimary)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onOpenRealmGuide?.invoke(0) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (p.isAscended) "仙" else "玄",
                            color = HdTextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = p.daoistName,
                                color = HdTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            if (p.isAscended) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(HdPurpleContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "已飞升",
                                        color = HdPurpleOnContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdPurpleContainer)
                                    .clickable { onOpenRealmGuide?.invoke(0) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .testTag("top_bar_realm_badge")
                            ) {
                                Text(
                                    text = "${realm.name} · ${p.realmStage}层",
                                    color = HdPurpleOnContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HdCrimson.copy(alpha = 0.12f))
                                    .clickable { onOpenRealmGuide?.invoke(1) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .testTag("top_bar_body_badge")
                            ) {
                                Text(
                                    text = "${body.name} ${p.bodyRealmStage}阶",
                                    color = HdCrimson,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // High Density Currency Chips
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ResourceChip(
                        label = "灵石",
                        value = "${p.spiritStones}"
                    )

                    if (p.isAscended) {
                        ResourceChip(
                            label = "仙晶",
                            value = "${p.celestialCrystals}"
                        )
                    } else {
                        ResourceChip(
                            label = "灵气",
                            value = "${p.spiritQi}/${p.spiritQiMax}"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: High Density Cultivation Rates & Sect Rank Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(0.5.dp, HdBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "修为增益",
                        tint = HdPurplePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "修真: ",
                        color = HdTextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "+${stats.cultivationRatePerSec}/秒",
                        color = HdPurplePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "灵气产出",
                        tint = HdJade,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "聚灵: ",
                        color = HdTextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "+${stats.spiritQiRatePerSec}/秒",
                        color = HdJade,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "职位: $sectRank",
                    color = HdTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ResourceChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(0.5.dp, HdBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = HdTextSecondary,
            fontSize = 10.sp
        )
        Text(
            text = value,
            color = HdPurplePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun RoundedCornerShape(fullDp: Int): RoundedCornerShape = RoundedCornerShape(100.dp)
