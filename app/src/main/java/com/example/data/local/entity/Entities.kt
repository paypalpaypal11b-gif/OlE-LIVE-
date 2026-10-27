package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val role: String, // "VIEWER", "HOST", "AGENCY_MANAGER", "ADMIN"
    val avatarUrl: String,
    val bio: String,
    val agencyId: String? = null,
    val demoPoints: Long = 5000L, // DEMO ONLY - Strictly non-withdrawable
    val earnedDiamonds: Long = 0L, // Real earnings from received gifts
    val withdrawableETB: Double = 0.0, // Real withdrawable earnings in Ethiopian Birr
    val escrowETB: Double = 0.0, // Held in escrow during pending/approved withdrawals
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val level: Int = 1,
    val isVerified: Boolean = false,
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "live_streams")
data class LiveStreamEntity(
    @PrimaryKey val id: String,
    val hostId: String,
    val hostName: String,
    val hostAvatar: String,
    val title: String,
    val category: String, // "Music", "Talk Show", "Gaming", "Ethiopian Culture", "Lifestyle"
    val isLive: Boolean = true,
    val viewerCount: Int = 1,
    val peakViewers: Int = 1,
    val diamondsEarned: Long = 0L,
    val channelKey: String,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val streamId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val senderRole: String,
    val message: String,
    val isGiftNotice: Boolean = false,
    val giftIcon: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gifts")
data class GiftEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconEmoji: String,
    val pointsCost: Long,
    val hostDiamondReward: Long,
    val category: String,
    val animationType: String,
    val description: String,
    val isActive: Boolean = true
)

@Entity(tableName = "agencies")
data class AgencyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val agencyCode: String,
    val managerId: String,
    val managerName: String,
    val commissionPercent: Double = 10.0,
    val isApproved: Boolean = true,
    val contactPhone: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_ledgers")
data class WalletLedgerEntity(
    @PrimaryKey val transactionId: String,
    val userId: String,
    val type: String, // "DEMO_RECHARGE", "GIFT_SENT", "GIFT_RECEIVED", "WITHDRAWAL_ESCROW", "WITHDRAWAL_PAID", "WITHDRAWAL_REFUND"
    val pointsDelta: Long = 0L,
    val diamondsDelta: Long = 0L,
    val etbDelta: Double = 0.0,
    val referenceId: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userRole: String,
    val amountETB: Double,
    val diamondsDeducted: Long,
    val paymentMethod: String, // "COMMERCIAL_BANK_OF_ETHIOPIA", "BANK_OF_ABYSSINIA", "AWASH_BANK", "DASHEN_BANK", "OROMIA_BANK", "TELEBIRR"
    val paymentMethodName: String,
    val maskedAccountIdentifier: String, // Masked for display security e.g. "•••• •••• 5678" or "09•• ••• •89"
    val encryptedAccountHash: String, // Secured account verification token
    val accountHolderName: String,
    val status: String, // "Pending", "Approved", "Rejected", "Paid"
    val idempotencyKey: String, // Prevents duplicate submissions
    val rejectionReason: String? = null,
    val paymentReference: String? = null, // Banking or Telebirr B2C Transaction Reference
    val requestedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val paidAt: Long? = null
)
