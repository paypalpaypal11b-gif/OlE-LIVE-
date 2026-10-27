package com.example.ui.live

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.GiftAnimationOverlay
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary
import com.example.ui.theme.OleRed
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FloatingHeart(
    val id: Long = Random.nextLong(),
    val color: Color,
    val startX: Float,
    val size: Int = (20..32).random()
)

@Composable
fun LivePlayerScreen(
    activeStream: LiveStreamEntity?,
    chatMessages: List<ChatMessageEntity>,
    gifts: List<GiftEntity>,
    currentUser: UserEntity?,
    activeGiftAnimation: Pair<GiftEntity, String>?,
    heartsCount: Int,
    onSendMessage: (String) -> Unit,
    onSendGift: (String) -> Unit,
    onHeartReaction: () -> Unit,
    onRechargePoints: () -> Unit,
    onClosePlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGiftTray by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    var isFollowing by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Floating hearts list
    val hearts = remember { mutableStateListOf<FloatingHeart>() }

    LaunchedEffect(heartsCount) {
        if (heartsCount > 0) {
            val colors = listOf(OleGreenAccent, OleGold, OleRed, Color(0xFFFF4081), Color(0xFF64FFDA))
            val newHeart = FloatingHeart(
                color = colors.random(),
                startX = (Random.nextFloat() * 60f) - 30f
            )
            hearts.add(newHeart)
            if (hearts.size > 25) {
                hearts.removeFirstOrNull()
            }
        }
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    // Video visualizer animation
    val infiniteTransition = rememberInfiniteTransition(label = "player_viz")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_player_screen")
    ) {
        // Stream Video Background / Broadcaster Visual Feed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(waveScale)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF041A0D),
                            Color(0xFF0A2B16),
                            Color(0xFF020D06)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Live broadcast visualizer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(3.5.dp, OleGreenAccent, CircleShape)
                        .background(OleBlackSurface),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = activeStream?.hostAvatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = activeStream?.title ?: "Live Broadcast",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${activeStream?.category} • Agora RTC Ultra-Low Latency (118ms)",
                    color = OleGreenAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Top Stream Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host info pill & follow
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, OleBlackBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = activeStream?.hostAvatar,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = activeStream?.hostName ?: "Host",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(OleRed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            color = OleGreenAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isFollowing) OleBlackBorder else OleGreenPrimary)
                        .clickable { isFollowing = !isFollowing }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("follow_host_button")
                ) {
                    Text(
                        text = if (isFollowing) "Following" else "+ Follow",
                        color = if (isFollowing) Color.White else Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Viewers & Close
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = OleGreenAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${activeStream?.viewerCount ?: 1}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .clickable { onClosePlayer() }
                        .testTag("close_player_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Player",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Live Chat Message Feed (Bottom Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 72.dp, start = 14.dp, end = 80.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.height(220.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                items(chatMessages) { msg ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (msg.isGiftNotice) {
                            Text(
                                text = "🎁 ",
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${msg.senderName}: ",
                            color = if (msg.isGiftNotice) OleGold else OleGreenAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = msg.message,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Floating Hearts Animation Canvas
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .size(80.dp, 260.dp)
        ) {
            hearts.takeLast(10).forEach { heart ->
                FloatingHeartItem(heart = heart)
            }
        }

        // Bottom Action Bar: Chat Input, Gift Tray Button, Heart Tap
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chat Input
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Say something nice...", color = OleGrayText, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OleGreenPrimary,
                    unfocusedBorderColor = OleBlackBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = OleBlackSurface,
                    unfocusedContainerColor = OleBlackSurface
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (chatInput.isNotBlank()) {
                        onSendMessage(chatInput)
                        chatInput = ""
                    }
                }),
                trailingIcon = {
                    if (chatInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSendMessage(chatInput)
                                chatInput = ""
                            },
                            modifier = Modifier.testTag("send_chat_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = OleGreenAccent)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chat_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Gift Tray Trigger Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F361A))
                    .border(1.5.dp, OleGold, CircleShape)
                    .clickable { showGiftTray = true }
                    .testTag("open_gift_tray_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Send Gift",
                    tint = OleGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Floating Heart Reaction Tap Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38141D))
                    .border(1.5.dp, OleRed, CircleShape)
                    .clickable { onHeartReaction() }
                    .testTag("heart_reaction_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Love Reaction",
                    tint = OleRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Live Gift Pop-up Banner
        GiftAnimationOverlay(activeGift = activeGiftAnimation)

        // Gift Tray Drawer
        AnimatedVisibility(
            visible = showGiftTray,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            LiveGiftTray(
                gifts = gifts,
                currentUser = currentUser,
                onSendGift = { giftId ->
                    onSendGift(giftId)
                },
                onRechargeClick = {
                    showGiftTray = false
                    onRechargePoints()
                },
                onDismiss = { showGiftTray = false }
            )
        }
    }
}

@Composable
fun FloatingHeartItem(heart: FloatingHeart) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(heart.id) {
        launch {
            offsetY.animateTo(
                targetValue = -350f,
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(heart.startX.toInt(), offsetY.value.toInt()) }
            .alpha(alpha.value)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = heart.color,
            modifier = Modifier.size(heart.size.dp)
        )
    }
}
