package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AgencyEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletLedgerEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE agencyId = :agencyId")
    fun getHostsByAgency(agencyId: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isBanned = :banned WHERE id = :userId")
    suspend fun setUserBanned(userId: String, banned: Boolean)

    @Query("UPDATE users SET isVerified = :verified WHERE id = :userId")
    suspend fun setUserVerified(userId: String, verified: Boolean)

    @Query("UPDATE users SET agencyId = :agencyId WHERE id = :userId")
    suspend fun assignUserAgency(userId: String, agencyId: String?)
}

@Dao
interface LiveStreamDao {
    @Query("SELECT * FROM live_streams WHERE isLive = 1 ORDER BY viewerCount DESC")
    fun observeActiveStreams(): Flow<List<LiveStreamEntity>>

    @Query("SELECT * FROM live_streams WHERE id = :streamId")
    fun observeStream(streamId: String): Flow<LiveStreamEntity?>

    @Query("SELECT * FROM live_streams WHERE id = :streamId")
    suspend fun getStreamById(streamId: String): LiveStreamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStream(stream: LiveStreamEntity)

    @Update
    suspend fun updateStream(stream: LiveStreamEntity)

    @Query("UPDATE live_streams SET isLive = 0, endedAt = :endedAt WHERE id = :streamId")
    suspend fun endStream(streamId: String, endedAt: Long = System.currentTimeMillis())

    @Query("UPDATE live_streams SET viewerCount = :count, peakViewers = CASE WHEN :count > peakViewers THEN :count ELSE peakViewers END WHERE id = :streamId")
    suspend fun updateViewerCount(streamId: String, count: Int)

    @Query("UPDATE live_streams SET diamondsEarned = diamondsEarned + :diamonds WHERE id = :streamId")
    suspend fun addStreamDiamonds(streamId: String, diamonds: Long)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE streamId = :streamId ORDER BY timestamp ASC LIMIT 100")
    fun observeMessages(streamId: String): Flow<List<ChatMessageEntity>>

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE streamId = :streamId")
    suspend fun clearMessagesForStream(streamId: String)
}

@Dao
interface GiftDao {
    @Query("SELECT * FROM gifts WHERE isActive = 1 ORDER BY pointsCost ASC")
    fun observeActiveGifts(): Flow<List<GiftEntity>>

    @Query("SELECT * FROM gifts ORDER BY pointsCost ASC")
    fun getAllGifts(): Flow<List<GiftEntity>>

    @Query("SELECT * FROM gifts WHERE id = :giftId")
    suspend fun getGiftById(giftId: String): GiftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGift(gift: GiftEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGifts(gifts: List<GiftEntity>)

    @Update
    suspend fun updateGift(gift: GiftEntity)
}

@Dao
interface AgencyDao {
    @Query("SELECT * FROM agencies ORDER BY createdAt DESC")
    fun observeAllAgencies(): Flow<List<AgencyEntity>>

    @Query("SELECT * FROM agencies WHERE id = :agencyId")
    fun observeAgency(agencyId: String): Flow<AgencyEntity?>

    @Query("SELECT * FROM agencies WHERE agencyCode = :code LIMIT 1")
    suspend fun getAgencyByCode(code: String): AgencyEntity?

    @Query("SELECT * FROM agencies WHERE managerId = :managerId LIMIT 1")
    suspend fun getAgencyByManager(managerId: String): AgencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgency(agency: AgencyEntity)

    @Update
    suspend fun updateAgency(agency: AgencyEntity)

    @Query("UPDATE agencies SET isApproved = :approved WHERE id = :agencyId")
    suspend fun setAgencyApproved(agencyId: String, approved: Boolean)
}

@Dao
interface WalletLedgerDao {
    @Query("SELECT * FROM wallet_ledgers WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun observeUserLedger(userId: String): Flow<List<WalletLedgerEntity>>

    @Query("SELECT * FROM wallet_ledgers ORDER BY timestamp DESC LIMIT 100")
    fun observeAllLedgers(): Flow<List<WalletLedgerEntity>>

    @Insert
    suspend fun insertLedger(ledger: WalletLedgerEntity)
}

@Dao
interface WithdrawalRequestDao {
    @Query("SELECT * FROM withdrawal_requests WHERE userId = :userId ORDER BY requestedAt DESC")
    fun observeUserWithdrawals(userId: String): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawal_requests ORDER BY requestedAt DESC")
    fun observeAllWithdrawals(): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawal_requests WHERE id = :id")
    suspend fun getWithdrawalById(id: String): WithdrawalRequestEntity?

    @Query("SELECT * FROM withdrawal_requests WHERE userId = :userId AND (status = 'Pending' OR status = 'Approved') LIMIT 1")
    suspend fun getActivePendingWithdrawal(userId: String): WithdrawalRequestEntity?

    @Query("SELECT * FROM withdrawal_requests WHERE idempotencyKey = :key LIMIT 1")
    suspend fun getByIdempotencyKey(key: String): WithdrawalRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(request: WithdrawalRequestEntity)

    @Update
    suspend fun updateWithdrawal(request: WithdrawalRequestEntity)
}
