package com.example.ui.live

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.UserEntity
import com.example.service.StreamingEngine
import com.example.ui.components.GiftAnimationOverlay
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary
import com.example.ui.theme.OleRed
import kotlinx.coroutines.delay

@Composable
fun LiveBroadcasterScreen(
    currentUser: UserEntity?,
    activeStream: LiveStreamEntity?,
    chatMessages: List<ChatMessageEntity>,
    activeGiftAnimation: Pair<GiftEntity, String>?,
    streamingEngine: StreamingEngine,
    onEndBroadcast: (String) -> Unit,
    onBackToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val metrics by streamingEngine.metrics.collectAsStateWithLifecycle()

    var showEndSummaryDialog by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var cameraActive by remember { mutableStateOf(false) }

    // Stream duration timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val formattedTime = remember(secondsElapsed) {
        val hrs = secondsElapsed / 3600
        val mins = (secondsElapsed % 3600) / 60
        val secs = secondsElapsed % 60
        if (hrs > 0) "%02d:%02d:%02d".format(hrs, mins, secs) else "%02d:%02d".format(mins, secs)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_broadcaster_screen")
    ) {
        // CameraX Preview View / Fallback Live Studio Art
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    streamingEngine.bindCameraPreview(ctx, lifecycleOwner, this) { success ->
                        cameraActive = success
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // If camera fails or runs in headless container, provide stylized broadcast background
        if (!cameraActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF072412),
                                Color(0xFF09140D),
                                Color(0xFF000000)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(3.dp, OleGreenAccent, CircleShape)
                            .background(OleBlackSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Broadcasting Live to Addis & World 🇪🇹",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${metrics.provider.displayName} • ${metrics.bitrateKbps} kbps",
                        color = OleGreenAccent,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Broadcaster Overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Live Badge, Streamer Info, Viewer Counter, End Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Host pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, OleBlackBorder, RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser?.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = currentUser?.displayName ?: "Broadcaster",
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
                                text = "LIVE • $formattedTime",
                                color = OleGreenAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Viewers & Diamonds counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Viewers
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
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
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Diamonds earned in this stream
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = OleGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${activeStream?.diamondsEarned ?: 0}",
                                color = OleGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // End stream button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { showEndSummaryDialog = true }
                            .testTag("end_stream_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "End Live",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Real-Time Telemetry Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "📶 ${metrics.bitrateKbps} kbps | ${metrics.fps} FPS | ${metrics.latencyMs}ms | Loss: ${(metrics.packetLossPct * 100).toInt()}%",
                    color = OleGreenAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Chat Messages Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    items(chatMessages.takeLast(10)) { msg ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

            Spacer(modifier = Modifier.height(10.dp))

            // Broadcaster Controls Bottom Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera switch
                BroadcasterActionButton(
                    icon = Icons.Default.Cameraswitch,
                    label = "Flip",
                    onClick = { streamingEngine.toggleCameraFacing() },
                    isActive = metrics.isFrontCamera,
                    tag = "flip_camera_button"
                )

                // Mic Mute toggle
                BroadcasterActionButton(
                    icon = if (metrics.isMicrophoneMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (metrics.isMicrophoneMuted) "Muted" else "Mic On",
                    onClick = { streamingEngine.toggleMicrophone() },
                    isActive = !metrics.isMicrophoneMuted,
                    tag = "mute_mic_button"
                )

                // Beauty Filter
                BroadcasterActionButton(
                    icon = Icons.Default.Face,
                    label = "Beauty",
                    onClick = { streamingEngine.toggleBeautyFilter() },
                    isActive = metrics.isBeautyFilterEnabled,
                    tag = "beauty_filter_button"
                )
            }
        }

        // Live Gift pop-up banner animation
        GiftAnimationOverlay(activeGift = activeGiftAnimation)
    }

    // End Broadcast Summary Dialog
    if (showEndSummaryDialog) {
        val earnedDiamonds = activeStream?.diamondsEarned ?: 0L
        val earnedETB = (earnedDiamonds.toDouble() / 100.0) * 25.0

        AlertDialog(
            onDismissRequest = { showEndSummaryDialog = false },
            title = {
                Text("End Live Stream?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to end your live broadcast?",
                        color = OleGrayText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2E19)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, OleGreenPrimary, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Broadcast Summary", color = OleGreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Duration: $formattedTime", color = Color.White, fontSize = 12.sp)
                            Text("Peak Viewers: ${activeStream?.peakViewers ?: 1}", color = Color.White, fontSize = 12.sp)
                            Text("Gift Diamonds: $earnedDiamonds 💎", color = OleGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Estimated Earnings: ETB %.2f".format(earnedETB), color = OleGreenAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndSummaryDialog = false
                        activeStream?.let { onEndBroadcast(it.id) }
                        onBackToExplore()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OleRed, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_end_stream_button")
                ) {
                    Text("End Broadcast", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndSummaryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = OleBlackBorder, contentColor = Color.White)
                ) {
                    Text("Continue Live")
                }
            },
            containerColor = OleBlackSurface
        )
    }
}

@Composable
fun BroadcasterActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFF0F381D) else Color.Black.copy(alpha = 0.6f))
                .border(1.dp, if (isActive) OleGreenAccent else OleBlackBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) OleGreenAccent else OleGrayText,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
