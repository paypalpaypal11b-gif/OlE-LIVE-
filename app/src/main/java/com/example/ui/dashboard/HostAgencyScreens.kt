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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.AgencyEntity
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
fun HostDashboardScreen(
    currentUser: UserEntity?,
    agency: AgencyEntity?,
    onGoToWallet: () -> Unit,
    onJoinAgency: (String, (Boolean, String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var agencyCodeInput by remember { mutableStateOf("") }
    var joinMessage by remember { mutableStateOf<String?>(null) }
    var isJoining by remember { mutableStateOf(false) }

    val diamonds = currentUser?.earnedDiamonds ?: 0L
    val withdrawableETB = currentUser?.withdrawableETB ?: 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Host Creator Analytics 🎙️",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track your live streaming performance, gift diamonds, and ETB earnings",
                color = OleGrayText,
                fontSize = 12.sp
            )
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Diamonds
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = OleGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Diamonds", color = OleGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$diamonds", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("Earned from gifts", color = OleGrayText, fontSize = 10.sp)
                    }
                }

                // ETB Value
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).border(1.dp, OleGreenPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = OleGreenAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Earnings (ETB)", color = OleGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("%.2f".format(withdrawableETB), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("Withdrawable ETB", color = OleGrayText, fontSize = 10.sp)
                    }
                }
            }
        }

        // Broadcast stats
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Broadcast Performance (Last 30 Days)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Total Stream Time", color = OleGrayText, fontSize = 10.sp)
                            Text("34.5 hrs", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Peak Viewers", color = OleGrayText, fontSize = 10.sp)
                            Text("1,450 Live", color = OleGreenAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Followers Gained", color = OleGrayText, fontSize = 10.sp)
                            Text("+3,240", color = OleGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Action: Withdraw
        item {
            Button(
                onClick = onGoToWallet,
                colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("host_go_to_wallet_button")
            ) {
                Text("Open Wallet & Request Withdrawal (ETB)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Agency Affiliation Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = OleGreenAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agency Management Affiliation", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (agency != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F2E18))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Signed with: ${agency.name}", color = OleGreenAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Agency Code: ${agency.agencyCode} • Commission: ${agency.commissionPercent}%", color = Color.White, fontSize = 11.sp)
                                Text("Manager: ${agency.managerName} (${agency.contactPhone})", color = OleGrayText, fontSize = 11.sp)
                            }
                        }
                    } else {
                        Text(
                            text = "Join an official talent agency to receive coaching, studio equipment, and verified promoter status.",
                            color = OleGrayText,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = agencyCodeInput,
                                onValueChange = { agencyCodeInput = it },
                                placeholder = { Text("Enter Agency Code (e.g. OLE-HORN-2026)", color = OleGrayText, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OleGreenPrimary,
                                    unfocusedBorderColor = OleBlackBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("join_agency_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    isJoining = true
                                    onJoinAgency(agencyCodeInput) { success, msg ->
                                        isJoining = false
                                        joinMessage = msg ?: if (success) "Agency joined!" else "Failed"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Join", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (joinMessage != null) {
                            Text(joinMessage!!, color = OleGold, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgencyDashboardScreen(
    agency: AgencyEntity?,
    agencyHosts: List<UserEntity>,
    onRegisterAgency: (String, String, (Boolean, String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var agencyNameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Agency Talent Management 🏢",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage signed creators, track host performance, and earn platform commissions",
                color = OleGrayText,
                fontSize = 12.sp
            )
        }

        if (agency == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Register Talent Agency", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = agencyNameInput,
                            onValueChange = { agencyNameInput = it },
                            label = { Text("Agency Name") },
                            placeholder = { Text("e.g. Sheger Talent Group") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OleGreenPrimary,
                                unfocusedBorderColor = OleBlackBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Business Phone (Telebirr/CBE)") },
                            placeholder = { Text("0911223344") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OleGreenPrimary,
                                unfocusedBorderColor = OleBlackBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onRegisterAgency(agencyNameInput, phoneInput) { success, err ->
                                    msg = err ?: "Agency registered!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Agency Registration", fontWeight = FontWeight.Bold)
                        }
                        if (msg != null) {
                            Text(msg!!, color = OleGold, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        } else {
            // Agency Overview
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, OleGreenPrimary, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(agency.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F361B))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("ACTIVE", color = OleGreenAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Agency Invite Code: ${agency.agencyCode}", color = OleGold, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Share this code with creators so they can sign under your management", color = OleGrayText, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Signed Hosts", color = OleGrayText, fontSize = 10.sp)
                                Text("${agencyHosts.size} Creators", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Commission Share", color = OleGrayText, fontSize = 10.sp)
                                Text("${agency.commissionPercent}% on Gifts", color = OleGreenAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Total Diamonds Generated", color = OleGrayText, fontSize = 10.sp)
                                val total = agencyHosts.sumOf { it.earnedDiamonds }
                                Text("$total 💎", color = OleGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text("Signed Hosts (${agencyHosts.size})", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            if (agencyHosts.isEmpty()) {
                item {
                    Text("No hosts signed yet. Give creators your code: ${agency.agencyCode}", color = OleGrayText, fontSize = 12.sp)
                }
            } else {
                items(agencyHosts) { host ->
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
                                    model = host.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(host.displayName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("@${host.username} • Level ${host.level}", color = OleGrayText, fontSize = 11.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${host.earnedDiamonds} 💎", color = OleGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("ETB %.2f".format(host.withdrawableETB), color = OleGreenAccent, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
