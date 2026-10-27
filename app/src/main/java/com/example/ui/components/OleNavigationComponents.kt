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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class OleScreen(val title: String, val icon: ImageVector) {
    EXPLORE("Live", Icons.Outlined.Explore),
    BROADCASTER("Go Live", Icons.Default.Videocam),
    WALLET("Wallet", Icons.Default.AccountBalanceWallet),
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    GUIDE("Architecture", Icons.Default.MenuBook),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun OleTopAppBar(
    currentUser: UserEntity?,
    onSwitchUserClick: () -> Unit,
    onGuideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(OleBlack)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand logo & title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(OleGreenPrimary, Color(0xFF004D20))
                        )
                    )
                    .border(1.5.dp, OleGreenAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OlE",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OlE Live",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OleGreenPrimary.copy(alpha = 0.2f))
                            .border(1.dp, OleGreenPrimary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "🇪🇹 ETB",
                            color = OleGreenAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Live Stream & Rewards",
                    color = OleGrayText,
                    fontSize = 11.sp
                )
            }
        }

        // Right side: Architecture Doc + User Profile Switcher
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Setup & Arch Guide Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(OleBlackSurfaceVariant)
                    .border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
                    .clickable { onGuideClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("architecture_guide_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Architecture Guide",
                        tint = OleGreenAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Docs",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // User Profile quick switch chip
            if (currentUser != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(OleBlackSurface)
                        .border(1.dp, OleGreenPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { onSwitchUserClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("switch_user_chip"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentUser.role,
                        color = if (currentUser.role == "ADMIN") OleGold else OleGreenAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OleBottomNavBar(
    currentScreen: OleScreen,
    onScreenSelected: (OleScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, OleBlackBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = OleBlackSurface,
        tonalElevation = 8.dp
    ) {
        val screens = listOf(
            OleScreen.EXPLORE,
            OleScreen.BROADCASTER,
            OleScreen.WALLET,
            OleScreen.DASHBOARD,
            OleScreen.PROFILE
        )

        screens.forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    if (screen == OleScreen.BROADCASTER) {
                        // Special glowing green button for Go Live
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(OleGreenPrimary, OleGreenAccent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OleGreenAccent,
                    selectedTextColor = OleGreenAccent,
                    unselectedIconColor = OleGrayText,
                    unselectedTextColor = OleGrayText,
                    indicatorColor = OleBlackSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
            )
        }
    }
}
