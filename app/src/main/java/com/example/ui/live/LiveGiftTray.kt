package com.example.ui.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleBlackSurfaceVariant
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary

@Composable
fun LiveGiftTray(
    gifts: List<GiftEntity>,
    currentUser: UserEntity?,
    onSendGift: (String) -> Unit,
    onRechargeClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGiftId by remember { mutableStateOf(gifts.firstOrNull()?.id ?: "") }
    val selectedGift = gifts.find { it.id == selectedGiftId }
    val pointsBalance = currentUser?.demoPoints ?: 0L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(OleBlackSurface)
            .border(1.dp, OleBlackBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(16.dp)
            .testTag("live_gift_tray")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Virtual Creator Gifts 🎁",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Support the host with animated gifts",
                    color = OleGrayText,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp).testTag("close_gift_tray_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = OleGrayText
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gifts Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(210.dp),
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gifts) { gift ->
                val isSelected = gift.id == selectedGiftId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFF1B3D23) else OleBlackSurfaceVariant)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) OleGreenAccent else OleBlackBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedGiftId = gift.id }
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("gift_item_${gift.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = gift.iconEmoji,
                            fontSize = 30.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = gift.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "${gift.pointsCost}",
                                color = OleGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " pts",
                                color = OleGrayText,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Balance & Send Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(OleBlack)
                .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Balance & top-up
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Points",
                        color = OleGrayText,
                        fontSize = 10.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$pointsBalance",
                            color = OleGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OleGreenPrimary.copy(alpha = 0.2f))
                                .clickable { onRechargeClick() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("recharge_points_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Refill",
                                    tint = OleGreenAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Refill",
                                    color = OleGreenAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Send Button
            Button(
                onClick = {
                    if (selectedGift != null) {
                        onSendGift(selectedGift.id)
                    }
                },
                enabled = selectedGift != null && pointsBalance >= (selectedGift?.pointsCost ?: Long.MAX_VALUE),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OleGreenPrimary,
                    contentColor = Color.Black,
                    disabledContainerColor = OleBlackBorder,
                    disabledContentColor = OleGrayText
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("send_gift_button")
            ) {
                Text(
                    text = if (selectedGift != null && pointsBalance < selectedGift.pointsCost)
                        "Low Points" else "Send Gift",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
