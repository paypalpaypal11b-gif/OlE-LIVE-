package com.example.ui.dashboard

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.AgencyEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.UserEntity
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
fun AdminDashboardScreen(
    allWithdrawals: List<WithdrawalRequestEntity>,
    allUsers: List<UserEntity>,
    allAgencies: List<AgencyEntity>,
    allGifts: List<GiftEntity>,
    onApproveWithdrawal: (String, String) -> Unit,
    onMarkAsPaid: (String, String) -> Unit,
    onRejectWithdrawal: (String, String) -> Unit,
    onToggleUserBan: (String, Boolean) -> Unit,
    onToggleUserVerified: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0 = Withdrawals, 1 = Users, 2 = Agencies, 3 = Gifts

    // Modals for actions
    var markPaidItem by remember { mutableStateOf<WithdrawalRequestEntity?>(null) }
    var paymentReferenceInput by remember { mutableStateOf("") }

    var rejectItem by remember { mutableStateOf<WithdrawalRequestEntity?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Admin Governance & Treasury",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OleGold.copy(alpha = 0.2f))
                            .border(1.dp, OleGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("SUPERADMIN", color = OleGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Review Ethiopian withdrawals, reconcile ledger, audit users",
                    color = OleGrayText,
                    fontSize = 11.sp
                )
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = OleBlackSurface,
            contentColor = OleGreenAccent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = OleGreenPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = {
                    Text(
                        "Withdrawals (${allWithdrawals.count { it.status == "Pending" || it.status == "Approved" }})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("admin_tab_withdrawals")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = {
                    Text(
                        "Users (${allUsers.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("admin_tab_users")
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = {
                    Text(
                        "Agencies (${allAgencies.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSection == 2) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("admin_tab_agencies")
            )
            Tab(
                selected = selectedSection == 3,
                onClick = { selectedSection = 3 },
                text = {
                    Text(
                        "Gifts (${allGifts.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSection == 3) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("admin_tab_gifts")
            )
        }

        when (selectedSection) {
            0 -> {
                AdminWithdrawalsSection(
                    withdrawals = allWithdrawals,
                    onApprove = { item -> onApproveWithdrawal(item.id, "Verified by Admin") },
                    onOpenMarkPaid = { item ->
                        markPaidItem = item
                        paymentReferenceInput = "ET-${item.paymentMethod.take(4)}-${(100000..999999).random()}"
                    },
                    onOpenReject = { item ->
                        rejectItem = item
                        rejectionReasonInput = "Account name does not match KYC identity."
                    }
                )
            }
            1 -> {
                AdminUsersSection(
                    users = allUsers,
                    onToggleBan = onToggleUserBan,
                    onToggleVerified = onToggleUserVerified
                )
            }
            2 -> {
                AdminAgenciesSection(agencies = allAgencies)
            }
            3 -> {
                AdminGiftsSection(gifts = allGifts)
            }
        }
    }

    // Modal: Mark as Paid (Confirming real payment disbursement)
    if (markPaidItem != null) {
        val item = markPaidItem!!
        AlertDialog(
            onDismissRequest = { markPaidItem = null },
            title = {
                Text("Confirm Bank Disbursement (Mark as Paid)", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "CRITICAL: Only mark as Paid if the funds have actually left the platform account and arrived via ${item.paymentMethodName}.",
                        color = OleGold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Recipient: ${item.accountHolderName} (${item.maskedAccountIdentifier})",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Amount: ETB %.2f".format(item.amountETB),
                        color = OleGreenAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = paymentReferenceInput,
                        onValueChange = { paymentReferenceInput = it },
                        label = { Text("Banking / Telebirr Transaction Ref") },
                        placeholder = { Text("e.g. CBE-B2C-9824104") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OleGreenPrimary,
                            unfocusedBorderColor = OleBlackBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("payment_ref_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onMarkAsPaid(item.id, paymentReferenceInput)
                        markPaidItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                    modifier = Modifier.testTag("confirm_mark_as_paid_button")
                ) {
                    Text("Confirm Paid", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { markPaidItem = null }) {
                    Text("Cancel", color = OleGrayText)
                }
            },
            containerColor = OleBlackSurface
        )
    }

    // Modal: Reject with reason
    if (rejectItem != null) {
        val item = rejectItem!!
        AlertDialog(
            onDismissRequest = { rejectItem = null },
            title = {
                Text("Reject & Refund Escrow", color = OleRed, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Rejecting this request will immediately refund ETB %.2f back to ${item.userName}'s withdrawable balance.".format(item.amountETB),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        label = { Text("Rejection Reason") },
                        placeholder = { Text("e.g. Invalid account number") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OleRed,
                            unfocusedBorderColor = OleBlackBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("rejection_reason_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectWithdrawal(item.id, rejectionReasonInput)
                        rejectItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OleRed, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_reject_withdrawal_button")
                ) {
                    Text("Reject & Refund", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectItem = null }) {
                    Text("Cancel", color = OleGrayText)
                }
            },
            containerColor = OleBlackSurface
        )
    }
}

@Composable
fun AdminWithdrawalsSection(
    withdrawals: List<WithdrawalRequestEntity>,
    onApprove: (WithdrawalRequestEntity) -> Unit,
    onOpenMarkPaid: (WithdrawalRequestEntity) -> Unit,
    onOpenReject: (WithdrawalRequestEntity) -> Unit
) {
    var statusFilter by remember { mutableStateOf("All") }
    val filtered = when (statusFilter) {
        "Pending" -> withdrawals.filter { it.status == "Pending" }
        "Approved" -> withdrawals.filter { it.status == "Approved" }
        "Paid" -> withdrawals.filter { it.status == "Paid" }
        "Rejected" -> withdrawals.filter { it.status == "Rejected" }
        else -> withdrawals
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Pending", "Approved", "Paid", "Rejected").forEach { filter ->
                val isSelected = statusFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) OleGreenPrimary else OleBlackSurfaceVariant)
                        .clickable { statusFilter = filter }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("filter_$filter")
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No withdrawal requests in '$statusFilter' category.", color = OleGrayText, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { item ->
                    AdminWithdrawalCard(
                        item = item,
                        onApprove = { onApprove(item) },
                        onOpenMarkPaid = { onOpenMarkPaid(item) },
                        onOpenReject = { onOpenReject(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalCard(
    item: WithdrawalRequestEntity,
    onApprove: () -> Unit,
    onOpenMarkPaid: () -> Unit,
    onOpenReject: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    val (statusColor, statusBg) = when (item.status) {
        "Paid" -> Pair(OleGreenAccent, Color(0xFF13361E))
        "Approved" -> Pair(OleGold, Color(0xFF382E14))
        "Rejected" -> Pair(OleRed, Color(0xFF3B1515))
        else -> Pair(Color(0xFF40C4FF), Color(0xFF142838))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OleBlackBorder, RoundedCornerShape(14.dp))
            .testTag("admin_withdrawal_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: User, Method, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.userName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.paymentMethodName} • ${item.maskedAccountIdentifier}",
                        color = OleGreenAccent,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Requested Amount", color = OleGrayText, fontSize = 10.sp)
                    Text(
                        text = "ETB %.2f".format(item.amountETB),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Account Holder Name", color = OleGrayText, fontSize = 10.sp)
                    Text(
                        text = item.accountHolderName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Reference if Paid
            if (item.status == "Paid" && item.paymentReference != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ref: ${item.paymentReference}",
                    color = OleGreenAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Rejection reason
            if (item.status == "Rejected" && item.rejectionReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reason: ${item.rejectionReason}",
                    color = OleRed,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = OleBlackBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons based on state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (item.status) {
                    "Pending" -> {
                        OutlinedButton(
                            onClick = onOpenReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OleRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_reject_${item.id}")
                        ) {
                            Text("Reject", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = OleGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_approve_${item.id}")
                        ) {
                            Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "Approved" -> {
                        OutlinedButton(
                            onClick = onOpenReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OleRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_reject_approved_${item.id}")
                        ) {
                            Text("Reject & Refund", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Mark as Paid: requires confirming real payment
                        Button(
                            onClick = onOpenMarkPaid,
                            colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_mark_paid_${item.id}")
                        ) {
                            Text("Record Real Payment & Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "Paid" -> {
                        Text(
                            text = "Disbursed on ${dateFormat.format(Date(item.paidAt ?: item.requestedAt))}",
                            color = OleGreenAccent,
                            fontSize = 11.sp
                        )
                    }
                    "Rejected" -> {
                        Text(
                            text = "Escrow Refunded",
                            color = OleGrayText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersSection(
    users: List<UserEntity>,
    onToggleBan: (String, Boolean) -> Unit,
    onToggleVerified: (String, Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = users.filter {
        it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.role.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search users by name, username or role...", color = OleGrayText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OleGrayText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OleGreenPrimary,
                unfocusedBorderColor = OleBlackBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("user_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered) { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.displayName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    if (user.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = OleGreenAccent, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text("@${user.username} • ${user.role}", color = OleGrayText, fontSize = 11.sp)
                                Text("Earnings: ETB %.2f • Diamonds: %d".format(user.withdrawableETB, user.earnedDiamonds), color = OleGold, fontSize = 10.sp)
                            }
                        }

                        Row {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (user.isVerified) OleBlackBorder else Color(0xFF0F361B))
                                    .clickable { onToggleVerified(user.id, !user.isVerified) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(if (user.isVerified) "Unverify" else "Verify", color = Color.White, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (user.isBanned) Color(0xFF0F361B) else Color(0xFF381414))
                                    .clickable { onToggleBan(user.id, !user.isBanned) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(if (user.isBanned) "Unban" else "Ban", color = if (user.isBanned) OleGreenAccent else OleRed, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAgenciesSection(agencies: List<AgencyEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(agencies) { agency ->
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(agency.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (agency.isApproved) Color(0xFF0F361B) else Color(0xFF382E14))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(if (agency.isApproved) "VERIFIED" else "PENDING", color = if (agency.isApproved) OleGreenAccent else OleGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Agency Code: ${agency.agencyCode}", color = OleGreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Manager: ${agency.managerName} • Phone: ${agency.contactPhone}", color = OleGrayText, fontSize = 11.sp)
                    Text("Commission Rate: ${agency.commissionPercent}% on host gifts", color = OleGold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminGiftsSection(gifts: List<GiftEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(gifts) { gift ->
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(gift.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(gift.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${gift.category} • Animation: ${gift.animationType}", color = OleGrayText, fontSize = 10.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("${gift.pointsCost} Points", color = OleGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("+${gift.hostDiamondReward} Host 💎", color = OleGreenAccent, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
