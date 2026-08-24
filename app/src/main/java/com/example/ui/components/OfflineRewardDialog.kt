package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.HdAzure
import com.example.ui.theme.HdBackground
import com.example.ui.theme.HdBorder
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
import com.example.viewmodel.OfflineReward

@Composable
fun OfflineRewardDialog(
    reward: OfflineReward?,
    onClaim: () -> Unit
) {
    if (reward == null) return

    val minutes = reward.secondsPassed / 60
    val hours = minutes / 60
    val timeStr = if (hours > 0) "${hours}小时 ${minutes % 60}分钟" else "${minutes}分钟"

    Dialog(onDismissRequest = onClaim) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, HdBorder, RoundedCornerShape(24.dp))
                .testTag("offline_reward_dialog"),
            color = HdSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "闭关归来",
                    tint = HdPurplePrimary,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "闭关潜修 · 神游归来",
                    color = HdTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "道友离线神游 $timeStr，洞府仆役辛勤劳作，积攒了丰厚天地灵韵！",
                    color = HdTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gains Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(HdSurfaceVariant)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GainRow(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "打坐修为收益",
                        value = "+${reward.expEarned} 修为",
                        color = HdPurplePrimary
                    )

                    GainRow(
                        icon = Icons.Default.Spa,
                        label = "聚灵阵聚气",
                        value = "+${reward.qiEarned} 灵气",
                        color = HdJade
                    )

                    if (reward.woodEarned > 0 || reward.ironEarned > 0 || reward.stoneEarned > 0) {
                        GainRow(
                            icon = Icons.Default.Nature,
                            label = "洞府木材采伐",
                            value = "+${reward.woodEarned} 木材",
                            color = HdAzure
                        )
                        GainRow(
                            icon = Icons.Default.Terrain,
                            label = "洞府矿脉产出",
                            value = "+${reward.ironEarned} 陨铁, +${reward.stoneEarned} 玄石",
                            color = HdTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("claim_offline_reward_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HdPurplePrimary,
                        contentColor = HdTextWhite
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "收纳灵气 闭关归来",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GainRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = HdTextSecondary,
                fontSize = 12.sp
            )
        }

        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
