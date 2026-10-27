package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.components.OleBottomNavBar
import com.example.ui.components.OleScreen
import com.example.ui.components.OleTopAppBar
import com.example.ui.dashboard.AdminDashboardScreen
import com.example.ui.dashboard.AgencyDashboardScreen
import com.example.ui.dashboard.HostDashboardScreen
import com.example.ui.guide.ArchitectureGuideScreen
import com.example.ui.home.HomeScreen
import com.example.ui.live.LiveBroadcasterScreen
import com.example.ui.live.LivePlayerScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OleBlack
import com.example.ui.viewmodel.OleLiveViewModel
import com.example.ui.wallet.WalletScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OleLiveApp()
            }
        }
    }
}

@Composable
fun OleLiveApp(viewModel: OleLiveViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeStreams by viewModel.activeStreams.collectAsStateWithLifecycle()
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val activeGifts by viewModel.activeGifts.collectAsStateWithLifecycle()
    val userLedgers by viewModel.userLedgers.collectAsStateWithLifecycle()
    val userWithdrawals by viewModel.userWithdrawals.collectAsStateWithLifecycle()
    val allWithdrawals by viewModel.allWithdrawals.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allAgencies by viewModel.allAgencies.collectAsStateWithLifecycle()
    val agencyHosts by viewModel.agencyHosts.collectAsStateWithLifecycle()
    val giftAnimation by viewModel.giftAnimation.collectAsStateWithLifecycle()
    val heartsCount by viewModel.heartsCount.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(OleScreen.EXPLORE) }
    var isPlayerOpen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.notificationMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Unauthenticated state
    if (currentUser == null) {
        AuthScreen(
            onLogin = { user, pass, cb -> viewModel.login(user, pass, cb) },
            onRegister = { u, d, e, p, r, cb -> viewModel.register(u, d, e, p, r, cb) },
            onQuickDemoSelect = { userId -> viewModel.switchUser(userId) }
        )
        return
    }

    // Back button handling
    BackHandler(enabled = isPlayerOpen || currentScreen != OleScreen.EXPLORE) {
        if (isPlayerOpen) {
            isPlayerOpen = false
        } else {
            currentScreen = OleScreen.EXPLORE
        }
    }

    // Full screen live stream player view
    if (isPlayerOpen) {
        LivePlayerScreen(
            activeStream = selectedStream,
            chatMessages = chatMessages,
            gifts = activeGifts,
            currentUser = currentUser,
            activeGiftAnimation = giftAnimation,
            heartsCount = heartsCount,
            onSendMessage = { text -> viewModel.sendChatMessage(text) },
            onSendGift = { giftId -> viewModel.sendGift(giftId) },
            onHeartReaction = { viewModel.addHeartReaction() },
            onRechargePoints = {
                currentScreen = OleScreen.WALLET
                isPlayerOpen = false
            },
            onClosePlayer = { isPlayerOpen = false }
        )
        return
    }

    // Broadcaster Screen
    if (currentScreen == OleScreen.BROADCASTER) {
        LiveBroadcasterScreen(
            currentUser = currentUser,
            activeStream = selectedStream,
            chatMessages = chatMessages,
            activeGiftAnimation = giftAnimation,
            streamingEngine = viewModel.streamingEngine,
            onEndBroadcast = { streamId -> viewModel.endBroadcast(streamId) },
            onBackToExplore = { currentScreen = OleScreen.EXPLORE }
        )
        return
    }

    Scaffold(
        topBar = {
            OleTopAppBar(
                currentUser = currentUser,
                onSwitchUserClick = { currentScreen = OleScreen.PROFILE },
                onGuideClick = { currentScreen = OleScreen.GUIDE }
            )
        },
        bottomBar = {
            OleBottomNavBar(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    if (screen == OleScreen.BROADCASTER) {
                        // Start broadcast session
                        viewModel.startBroadcast(
                            title = "Live with ${currentUser?.displayName ?: "Creator"} 🇪🇹",
                            category = if (currentUser?.role == "HOST") "Music" else "Lifestyle"
                        ) {
                            currentScreen = OleScreen.BROADCASTER
                        }
                    } else {
                        currentScreen = screen
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OleBlack,
        modifier = Modifier.fillMaxSize().testTag("ole_live_main_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OleBlack)
        ) {
            when (currentScreen) {
                OleScreen.EXPLORE -> {
                    HomeScreen(
                        activeStreams = activeStreams,
                        onSelectStream = { streamId ->
                            viewModel.selectStream(streamId)
                            isPlayerOpen = true
                        },
                        onGoLiveClick = {
                            viewModel.startBroadcast(
                                title = "Addis Live with ${currentUser?.displayName ?: "Creator"} 🇪🇹",
                                category = "Music"
                            ) {
                                currentScreen = OleScreen.BROADCASTER
                            }
                        }
                    )
                }

                OleScreen.WALLET -> {
                    WalletScreen(
                        currentUser = currentUser,
                        userLedgers = userLedgers,
                        userWithdrawals = userWithdrawals,
                        onRechargeDemoPoints = { pts -> viewModel.rechargePoints(pts) },
                        onSubmitWithdrawal = { amt, code, acc, name, cb ->
                            viewModel.submitWithdrawal(amt, code, acc, name, cb)
                        }
                    )
                }

                OleScreen.DASHBOARD -> {
                    when (currentUser?.role) {
                        "ADMIN" -> {
                            AdminDashboardScreen(
                                allWithdrawals = allWithdrawals,
                                allUsers = allUsers,
                                allAgencies = allAgencies,
                                allGifts = activeGifts,
                                onApproveWithdrawal = { id, note -> viewModel.adminApproveWithdrawal(id, note) },
                                onMarkAsPaid = { id, ref -> viewModel.adminMarkWithdrawalAsPaid(id, ref) },
                                onRejectWithdrawal = { id, reason -> viewModel.adminRejectWithdrawal(id, reason) },
                                onToggleUserBan = { id, ban -> viewModel.toggleUserBan(id, ban) },
                                onToggleUserVerified = { id, ver -> viewModel.toggleUserVerification(id, ver) }
                            )
                        }
                        "AGENCY_MANAGER" -> {
                            val agency = allAgencies.find { it.id == currentUser?.agencyId || it.managerId == currentUser?.id }
                            AgencyDashboardScreen(
                                agency = agency,
                                agencyHosts = agencyHosts,
                                onRegisterAgency = { name, phone, cb -> viewModel.registerAgency(name, phone, cb) }
                            )
                        }
                        else -> {
                            // Host & Viewer Dashboard
                            val agency = allAgencies.find { it.id == currentUser?.agencyId }
                            HostDashboardScreen(
                                currentUser = currentUser,
                                agency = agency,
                                onGoToWallet = { currentScreen = OleScreen.WALLET },
                                onJoinAgency = { code, cb -> viewModel.joinAgency(code, cb) }
                            )
                        }
                    }
                }

                OleScreen.GUIDE -> {
                    ArchitectureGuideScreen()
                }

                OleScreen.PROFILE -> {
                    ProfileScreen(
                        currentUser = currentUser,
                        onSwitchUser = { uid -> viewModel.switchUser(uid) },
                        onOpenWallet = { currentScreen = OleScreen.WALLET },
                        onOpenGuide = { currentScreen = OleScreen.GUIDE },
                        onLogout = { viewModel.logout() }
                    )
                }

                OleScreen.BROADCASTER -> {
                    // Handled above in full-screen
                }
            }
        }
    }
}
