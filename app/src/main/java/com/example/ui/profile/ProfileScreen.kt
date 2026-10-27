package com.example.ui.profile

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleBlackSurfaceVariant
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary
import com.example.ui.theme.OleRed

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    onSwitchUser: (String) -> Unit,
    onOpenWallet: () -> Unit,
    onOpenGuide: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OleBlackBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, OleGreenAccent, CircleShape)
                            .background(OleBlackSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser?.displayName ?: "User",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentUser?.isVerified == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = OleGreenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "@${currentUser?.username ?: "username"}",
                        color = OleGrayText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F361A))
                            .border(1.dp, OleGreenAccent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${currentUser?.role} • LEVEL ${currentUser?.level ?: 1}",
                            color = OleGreenAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser?.bio ?: "No bio yet.",
                        color = Color(0xFFD6DFD8),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = OleBlackBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Stats: Followers / Following / Earnings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.followerCount ?: 0}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Followers", color = OleGrayText, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.followingCount ?: 0}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Following", color = OleGrayText, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ETB %.2f".format(currentUser?.withdrawableETB ?: 0.0),
                                color = OleGreenAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Real Earnings", color = OleGrayText, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Quick Demo Accounts Switcher (For seamless evaluation)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OleGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = OleGreenAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quick Demo Account Switcher",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Instant 1-tap testing across Creator, Viewer, Agency & Admin roles:",
                        color = OleGrayText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    val demoUsers = listOf(
                        DemoProfile("user_host_selam", "Selamawit Live", "HOST", "12,450 💎 • 3,112.50 ETB", Color(0xFF00C853)),
                        DemoProfile("user_viewer_dawit", "Dawit Bekele", "VIEWER", "8,500 Gifting Points", Color(0xFF40C4FF)),
                        DemoProfile("user_agency_mgr", "Yared Media", "AGENCY_MGR", "Horn of Africa Agency", Color(0xFFB388FF)),
                        DemoProfile("user_admin_ole", "OlE Compliance", "ADMIN", "SuperAdmin & Treasury", OleGold)
                    )

                    demoUsers.forEach { profile ->
                        val isCurrent = currentUser?.id == profile.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) Color(0xFF14331C) else OleBlackSurfaceVariant)
                                .clickable { onSwitchUser(profile.id) }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("demo_user_${profile.id}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.name,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(profile.badgeColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(profile.role, color = profile.badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(profile.desc, color = OleGrayText, fontSize = 10.sp)
                            }

                            if (isCurrent) {
                                Text("ACTIVE", color = OleGreenAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("Switch →", color = OleGrayText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Navigation Shortcuts
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    // Open Wallet & Ethiopian Withdrawals
                    ProfileMenuRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = OleGreenAccent,
                        title = "Ethiopian Birr Wallet & Withdrawals",
                        subtitle = "Telebirr, CBE, Abyssinia, Awash, Dashen, Oromia Bank",
                        onClick = onOpenWallet,
                        tag = "menu_wallet"
                    )

                    Divider(color = OleBlackBorder, modifier = Modifier.padding(horizontal = 10.dp))

                    // Architecture & Integrations Guide
                    ProfileMenuRow(
                        icon = Icons.Default.MenuBook,
                        iconTint = OleGold,
                        title = "Architecture & Service Costs Guide",
                        subtitle = "Streaming services, costs, security & APK build",
                        onClick = onOpenGuide,
                        tag = "menu_guide"
                    )
                }
            }
        }

        // Logout
        item {
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OleRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("logout_button")
            ) {
                Text("Log Out / Change Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class DemoProfile(
    val id: String,
    val name: String,
    val role: String,
    val desc: String,
    val badgeColor: Color
)

@Composable
fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = OleGrayText,
                    fontSize = 11.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = OleGrayText,
            modifier = Modifier.size(18.dp)
        )
    }
}
