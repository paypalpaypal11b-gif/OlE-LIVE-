package com.example.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary

@Composable
fun ArchitectureGuideScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .padding(16.dp)
            .testTag("architecture_guide_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "OlE Live Platform Architecture & Guide 📖",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Production engineering guide: Streaming services, service costs, security requirements, and Android APK builds.",
                color = OleGrayText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Section 1: Real-Time Live Video Streaming Services
        item {
            GuideSectionCard(
                icon = Icons.Default.Videocam,
                iconColor = OleGreenAccent,
                title = "1. Real-Time Video Streaming Architecture",
                items = listOf(
                    GuideItem(
                        header = "Recommended Streaming Engine (Agora RTC vs LiveKit)",
                        content = "For ultra-low latency interactive broadcasting (<200ms), Agora RTC SDK or LiveKit Cloud are the gold standard. Broadcasters publish WebRTC audio/video tracks while viewers subscribe to live streams with dynamic quality switching."
                    ),
                    GuideItem(
                        header = "Token Security & Channel Authentication",
                        content = "broadcasters and viewers generate temporary HMAC SHA-256 tokens on the backend using an App Certificate. Channel names and participant roles (Host vs Audience) are verified prior to joining."
                    ),
                    GuideItem(
                        header = "CameraX Broadcaster Pipeline",
                        content = "The Android client uses Jetpack CameraX PreviewView bound to Android Lifecycle, handling front/back camera flips, beauty filter OpenGL shaders, and audio microphone sampling."
                    )
                )
            )
        }

        // Section 2: Service Costs Breakdown
        item {
            GuideSectionCard(
                icon = Icons.Default.MonetizationOn,
                iconColor = OleGold,
                title = "2. Real-Time Service Costs Breakdown",
                items = listOf(
                    GuideItem(
                        header = "Live Video Streaming Costs",
                        content = "• Agora RTC: 10,000 free minutes/month, then ~$0.99 to $3.99 per 1,000 participant minutes depending on resolution (720p vs 1080p).\n• LiveKit Cloud: ~$0.004 per GB bandwidth or ~$0.001 per participant minute.\n• AWS IVS (Interactive Video Service): ~$0.075 per hour for live input + ~$0.015 per viewer hour."
                    ),
                    GuideItem(
                        header = "Cloud Database & Storage",
                        content = "• Firestore / PostgreSQL (Supabase / Cloud SQL): ~$0.06 per 100k reads; ~$0.18 per 100k writes.\n• CDN / Video recording storage (GCS / S3): ~$0.026 per GB/month."
                    ),
                    GuideItem(
                        header = "Ethiopian Payment Gateway Fees",
                        content = "• Telebirr B2C: ~0.5% - 1.0% per disbursement transaction.\n• CBE Direct Host-to-Host: Negotiated bank partner tariff (~5 - 15 ETB per bulk batch).\n• EthSwitch / Aggregators (Chapa, SantimPay): 1.5% - 2.5% for wallet/card acquiring."
                    )
                )
            )
        }

        // Section 3: Security & Server-Side Accounting
        item {
            GuideSectionCard(
                icon = Icons.Default.Security,
                iconColor = Color(0xFF40C4FF),
                title = "3. Security & Server-Side Accounting Rules",
                items = listOf(
                    GuideItem(
                        header = "Double-Entry Ledger Accounting",
                        content = "All points and diamond transfers are atomic. Every gift send event writes twin ledger rows: debiting viewer points and crediting host diamonds simultaneously in a single transaction."
                    ),
                    GuideItem(
                        header = "Escrow State Machine for Withdrawals",
                        content = "Funds requested for withdrawal are immediately locked in escrow and deducted from available balance (Pending -> Approved -> Paid). If rejected by compliance, escrow funds are automatically refunded."
                    ),
                    GuideItem(
                        header = "No Plain-Text Banking Storage",
                        content = "Bank account numbers and Telebirr phone numbers are stored as HMAC-SHA256 tokens and displayed masked (e.g. •••• •••• 1234 or 09•• ••• •89). In production, AES-256-GCM is used with KMS."
                    ),
                    GuideItem(
                        header = "Strict Real vs Demo Distinction",
                        content = "Demo points used for testing gifting can never be withdrawn or converted to fiat currency. Withdrawals are strictly restricted to verified creator gift diamonds."
                    )
                )
            )
        }

        // Section 4: Android Release APK Build Guide
        item {
            GuideSectionCard(
                icon = Icons.Default.Android,
                iconColor = OleGreenPrimary,
                title = "4. Android APK / AAB Build Process",
                items = listOf(
                    GuideItem(
                        header = "Step 1: Release Keystore Configuration",
                        content = "Generate a production signing keystore:\nkeytool -genkey -v -keystore release.jks -alias olelive -keyalg RSA -keysize 2048 -validity 10000\nStore STORE_PASSWORD and KEY_PASSWORD in environment variables."
                    ),
                    GuideItem(
                        header = "Step 2: ProGuard Rules & Obfuscation",
                        content = "In proguard-rules.pro, preserve CameraX, WebRTC/Agora SDK JNI bindings, and Room database entities:\n-keep class com.example.data.local.entity.** { *; }\n-keepattributes *Annotation*"
                    ),
                    GuideItem(
                        header = "Step 3: Gradle Build Execution",
                        content = "To build a signed Release APK:\ngradle :app:assembleRelease\nTo build a Google Play Store App Bundle (.aab):\ngradle :app:bundleRelease"
                    ),
                    GuideItem(
                        header = "Step 4: AI Studio Export",
                        content = "In AI Studio Build, users can export the entire repository as a ZIP archive, push to GitHub, or download the compiled APK directly via the top right menu."
                    )
                )
            )
        }
    }
}

data class GuideItem(val header: String, val content: String)

@Composable
fun GuideSectionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    items: List<GuideItem>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = OleBlackBorder)
            Spacer(modifier = Modifier.height(10.dp))

            items.forEachIndexed { index, item ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = item.header,
                        color = OleGreenAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.content,
                        color = Color(0xFFD3DDD5),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
                if (index < items.size - 1) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}
