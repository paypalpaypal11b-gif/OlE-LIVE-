package com.example.ui.wallet

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletLedgerEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleBlackSurfaceVariant
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary
import com.example.ui.theme.OleRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    currentUser: UserEntity?,
    userLedgers: List<WalletLedgerEntity>,
    userWithdrawals: List<WithdrawalRequestEntity>,
    onRechargeDemoPoints: (Long) -> Unit,
    onSubmitWithdrawal: (Double, String, String, String, (Boolean, String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var showWithdrawalPage by remember { mutableStateOf(false) }

    if (showWithdrawalPage) {
        Column(modifier = modifier.fillMaxSize()) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OleBlackSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(OleBlackSurfaceVariant)
                        .clickable { showWithdrawalPage = false }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("back_to_wallet_button")
                ) {
                    Text("← Back to Wallet", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Withdraw to Ethiopian Bank / Telebirr",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            WithdrawalScreen(
                currentUser = currentUser,
                userWithdrawals = userWithdrawals,
                onSubmitWithdrawal = onSubmitWithdrawal
            )
        }
        return
    }

    val demoPoints = currentUser?.demoPoints ?: 0L
    val diamonds = currentUser?.earnedDiamonds ?: 0L
    val withdrawableETB = currentUser?.withdrawableETB ?: 0.0
    val escrowETB = currentUser?.escrowETB ?: 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OlE Creator Wallet",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real Ethiopian Birr (ETB) Accounting & Demo Points",
                        color = OleGrayText,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F2B16))
                        .border(1.dp, OleGreenAccent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🇪🇹 NBE Audited", color = OleGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Real Withdrawable Earnings Card (Green / Black)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(OleGreenPrimary, OleGreenAccent)),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F361B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = OleGreenAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Withdrawable Real Earnings",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Converted from verified creator gifts",
                                    color = OleGrayText,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OleBlackSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "100 💎 = 25 ETB",
                                color = OleGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "ETB ",
                            color = OleGreenAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "%.2f".format(withdrawableETB),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (escrowETB > 0) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "(+%.2f Escrow)".format(escrowETB),
                                color = OleGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showWithdrawalPage = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OleGreenPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_withdrawal_page_button")
                    ) {
                        Text(
                            text = "Withdraw to Ethiopian Bank / Telebirr 🇪🇹",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Split Row: Earned Creator Diamonds vs Gifting Demo Points
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Earned Diamonds Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = OleGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Diamonds", color = OleGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$diamonds",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Gift Rewards",
                            color = OleGrayText,
                            fontSize = 10.sp
                        )
                    }
                }

                // Demo Points Card (Non-withdrawable)
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = OleGreenAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gifting Pts", color = OleGreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$demoPoints",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Non-withdrawable",
                            color = OleGrayText,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Demo Points Quick Top-Up Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OleBlackBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Need points to test gifting?",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Free refill for live stream simulation",
                            color = OleGrayText,
                            fontSize = 10.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OleBlackSurface)
                                .border(1.dp, OleGreenPrimary, RoundedCornerShape(8.dp))
                                .clickable { onRechargeDemoPoints(5000L) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("refill_5000_button")
                        ) {
                            Text("+5k Pts", color = OleGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OleBlackSurface)
                                .border(1.dp, OleGreenPrimary, RoundedCornerShape(8.dp))
                                .clickable { onRechargeDemoPoints(20000L) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("refill_20000_button")
                        ) {
                            Text("+20k Pts", color = OleGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Transaction Ledger Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction Ledger (${userLedgers.size})",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Immutable Audit Trail",
                    color = OleGrayText,
                    fontSize = 11.sp
                )
            }
        }

        // Transaction Ledger List
        if (userLedgers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No ledger entries yet. Send gifts or receive rewards to generate accounting entries.",
                        color = OleGrayText,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(userLedgers) { ledger ->
                WalletLedgerRow(ledger = ledger)
            }
        }
    }
}

@Composable
fun WalletLedgerRow(ledger: WalletLedgerEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    val isPositive = ledger.pointsDelta > 0 || ledger.diamondsDelta > 0 || ledger.etbDelta > 0
    val deltaColor = if (isPositive) OleGreenAccent else OleRed

    Card(
        colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isPositive) Color(0xFF0E2E16) else Color(0xFF2E1313)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = deltaColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = ledger.note,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = "${ledger.type} • ${dateFormat.format(Date(ledger.timestamp))}",
                        color = OleGrayText,
                        fontSize = 10.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (ledger.etbDelta != 0.0) {
                    Text(
                        text = "${if (ledger.etbDelta > 0) "+" else ""}%.2f ETB".format(ledger.etbDelta),
                        color = deltaColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (ledger.diamondsDelta != 0L) {
                    Text(
                        text = "${if (ledger.diamondsDelta > 0) "+" else ""}${ledger.diamondsDelta} 💎",
                        color = OleGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "${if (ledger.pointsDelta > 0) "+" else ""}${ledger.pointsDelta} pts",
                        color = deltaColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
