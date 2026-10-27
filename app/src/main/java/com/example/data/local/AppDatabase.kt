package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AgencyDao
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.GiftDao
import com.example.data.local.dao.LiveStreamDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WalletLedgerDao
import com.example.data.local.dao.WithdrawalRequestDao
import com.example.data.local.entity.AgencyEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletLedgerEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        LiveStreamEntity::class,
        ChatMessageEntity::class,
        GiftEntity::class,
        AgencyEntity::class,
        WalletLedgerEntity::class,
        WithdrawalRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun liveStreamDao(): LiveStreamDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun giftDao(): GiftDao
    abstract fun agencyDao(): AgencyDao
    abstract fun walletLedgerDao(): WalletLedgerDao
    abstract fun withdrawalRequestDao(): WithdrawalRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ole_live_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    seedInitialData(database)
                }
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            val giftDao = db.giftDao()
            val initialGifts = listOf(
                GiftEntity(
                    id = "gift_rose",
                    name = "Habesha Rose",
                    iconEmoji = "🌹",
                    pointsCost = 10,
                    hostDiamondReward = 8,
                    category = "Popular",
                    animationType = "FLOAT",
                    description = "A fragrant red rose token of love"
                ),
                GiftEntity(
                    id = "gift_coffee",
                    name = "Buna Ceremony (ቡና)",
                    iconEmoji = "☕",
                    pointsCost = 50,
                    hostDiamondReward = 40,
                    category = "Cultural",
                    animationType = "FLOAT",
                    description = "Traditional Ethiopian clay jebena coffee"
                ),
                GiftEntity(
                    id = "gift_star",
                    name = "Addis Golden Star",
                    iconEmoji = "⭐",
                    pointsCost = 100,
                    hostDiamondReward = 80,
                    category = "VIP",
                    animationType = "EXPLOSION",
                    description = "Bright shining creator spotlight"
                ),
                GiftEntity(
                    id = "gift_crown",
                    name = "Imperial Crown (ዘውድ)",
                    iconEmoji = "👑",
                    pointsCost = 500,
                    hostDiamondReward = 400,
                    category = "Royal",
                    animationType = "CROWN",
                    description = "Royal golden crown for top hosts"
                ),
                GiftEntity(
                    id = "gift_lion",
                    name = "Lion of Judah (አንበሳ)",
                    iconEmoji = "🦁",
                    pointsCost = 1000,
                    hostDiamondReward = 850,
                    category = "Legendary",
                    animationType = "EXPLOSION",
                    description = "Majestic roar with golden banner"
                ),
                GiftEntity(
                    id = "gift_car",
                    name = "Bole Supercar",
                    iconEmoji = "🏎️",
                    pointsCost = 2500,
                    hostDiamondReward = 2100,
                    category = "Luxury",
                    animationType = "SUPERCAR",
                    description = "Luxury sports car drift across the screen"
                ),
                GiftEntity(
                    id = "gift_fireworks",
                    name = "Ethiopian Dream",
                    iconEmoji = "🎆",
                    pointsCost = 5000,
                    hostDiamondReward = 4250,
                    category = "Supreme",
                    animationType = "FIREWORKS",
                    description = "Grand sky fireworks festival across Addis"
                )
            )
            giftDao.insertGifts(initialGifts)

            val agencyDao = db.agencyDao()
            val defaultAgency = AgencyEntity(
                id = "agency_horn_media",
                name = "Horn of Africa Talent Agency",
                agencyCode = "OLE-HORN-2026",
                managerId = "user_agency_mgr",
                managerName = "Yared Media Group",
                commissionPercent = 10.0,
                isApproved = true,
                contactPhone = "+251911223344"
            )
            agencyDao.insertAgency(defaultAgency)

            val userDao = db.userDao()
            val hostUser = UserEntity(
                id = "user_host_selam",
                username = "selam_live",
                displayName = "Selamawit Yohannes",
                email = "selam@olelive.et",
                passwordHash = "demo_hash_selam",
                salt = "salt1",
                role = "HOST",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                bio = "Singer & Acoustic Live Streamer in Addis Ababa 🇪🇹 | Daily 8 PM EAT",
                agencyId = "agency_horn_media",
                demoPoints = 2500L,
                earnedDiamonds = 12450L,
                withdrawableETB = 3112.50, // 100 diamonds = 25 ETB
                escrowETB = 0.0,
                followerCount = 14200,
                followingCount = 120,
                level = 18,
                isVerified = true
            )

            val viewerUser = UserEntity(
                id = "user_viewer_dawit",
                username = "dawit_bole",
                displayName = "Dawit Bekele",
                email = "dawit@olelive.et",
                passwordHash = "demo_hash_dawit",
                salt = "salt2",
                role = "VIEWER",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                bio = "Music lover & tech creator enthusiast. Supporting local Ethiopian talent!",
                agencyId = null,
                demoPoints = 8500L,
                earnedDiamonds = 0L,
                withdrawableETB = 0.0,
                escrowETB = 0.0,
                followerCount = 340,
                followingCount = 42,
                level = 5,
                isVerified = false
            )

            val agencyUser = UserEntity(
                id = "user_agency_mgr",
                username = "horn_talent",
                displayName = "Yared Talent Director",
                email = "agency@hornmedia.et",
                passwordHash = "demo_hash_agency",
                salt = "salt3",
                role = "AGENCY_MANAGER",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                bio = "Official Talent Management Agency on OlE Live. Recruiting verified creators.",
                agencyId = "agency_horn_media",
                demoPoints = 20000L,
                earnedDiamonds = 45000L,
                withdrawableETB = 11250.0,
                escrowETB = 0.0,
                followerCount = 8900,
                followingCount = 50,
                level = 25,
                isVerified = true
            )

            val adminUser = UserEntity(
                id = "user_admin_ole",
                username = "admin",
                displayName = "OlE Live Compliance & Ops",
                email = "admin@olelive.et",
                passwordHash = "demo_hash_admin",
                salt = "salt4",
                role = "ADMIN",
                avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300",
                bio = "OlE Live Platform Integrity, Treasury & Audit Admin",
                agencyId = null,
                demoPoints = 999999L,
                earnedDiamonds = 0L,
                withdrawableETB = 0.0,
                escrowETB = 0.0,
                followerCount = 50000,
                followingCount = 1,
                level = 99,
                isVerified = true
            )

            userDao.insertUser(hostUser)
            userDao.insertUser(viewerUser)
            userDao.insertUser(agencyUser)
            userDao.insertUser(adminUser)

            val streamDao = db.liveStreamDao()
            val liveStream1 = LiveStreamEntity(
                id = "stream_selam_01",
                hostId = hostUser.id,
                hostName = hostUser.displayName,
                hostAvatar = hostUser.avatarUrl,
                title = "Acoustic Ethiopian Classics & Chat 🇪🇹✨",
                category = "Music",
                isLive = true,
                viewerCount = 428,
                peakViewers = 612,
                diamondsEarned = 1450L,
                channelKey = "ole_channel_selam_live"
            )
            val liveStream2 = LiveStreamEntity(
                id = "stream_habesha_gaming",
                hostId = "user_gamer_naty",
                hostName = "Nati Gamers ET",
                hostAvatar = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=300",
                title = "FC 24 Addis Derby Championship Tournament 🎮🔥",
                category = "Gaming",
                isLive = true,
                viewerCount = 852,
                peakViewers = 1100,
                diamondsEarned = 3200L,
                channelKey = "ole_channel_nati_fc"
            )
            val liveStream3 = LiveStreamEntity(
                id = "stream_culture_buna",
                hostId = "user_host_eden",
                hostName = "Eden Coffee Stories",
                hostAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300",
                title = "Traditional Coffee Ceremony & Yirgacheffe Tasting ☕",
                category = "Ethiopian Culture",
                isLive = true,
                viewerCount = 295,
                peakViewers = 340,
                diamondsEarned = 890L,
                channelKey = "ole_channel_eden_coffee"
            )
            streamDao.insertStream(liveStream1)
            streamDao.insertStream(liveStream2)
            streamDao.insertStream(liveStream3)

            val chatDao = db.chatMessageDao()
            chatDao.insertMessage(
                ChatMessageEntity(
                    streamId = liveStream1.id,
                    senderId = "user_viewer_dawit",
                    senderName = "Dawit Bekele",
                    senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                    senderRole = "VIEWER",
                    message = "Selam Selam! Beautiful song! Greetings from Piassa 👋"
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    streamId = liveStream1.id,
                    senderId = "sys",
                    senderName = "OlE Live System",
                    senderAvatar = "",
                    senderRole = "SYSTEM",
                    message = "Dawit sent Buna Ceremony (ቡና) x1 ☕",
                    isGiftNotice = true,
                    giftIcon = "☕"
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    streamId = liveStream1.id,
                    senderId = "user_viewer_marta",
                    senderName = "Marta T.",
                    senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300",
                    senderRole = "VIEWER",
                    message = "Can you sing Tilahun Gessesse next please? ❤️"
                )
            )
        }
    }
}
