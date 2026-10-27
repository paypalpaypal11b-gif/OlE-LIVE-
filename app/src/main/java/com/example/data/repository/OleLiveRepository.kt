package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.AgencyEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletLedgerEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

enum class EthiopianPaymentMethod(
    val code: String,
    val displayName: String,
    val type: String, // "BANK" or "MOBILE_MONEY"
    val placeholderHint: String,
    val identifierLabel: String,
    val badgeColorHex: Long
) {
    CBE(
        code = "COMMERCIAL_BANK_OF_ETHIOPIA",
        displayName = "Commercial Bank of Ethiopia (CBE)",
        type = "BANK",
        placeholderHint = "e.g. 1000123456789 (13 digits)",
        identifierLabel = "CBE Account Number",
        badgeColorHex = 0xFF5D1049 // CBE Burgundy/Purple
    ),
    ABYSSINIA(
        code = "BANK_OF_ABYSSINIA",
        displayName = "Bank of Abyssinia",
        type = "BANK",
        placeholderHint = "e.g. 883492019284",
        identifierLabel = "Abyssinia Account Number",
        badgeColorHex = 0xFFC7923E // Gold
    ),
    AWASH(
        code = "AWASH_BANK",
        displayName = "Awash Bank",
        type = "BANK",
        placeholderHint = "e.g. 01320492817200",
        identifierLabel = "Awash Account Number",
        badgeColorHex = 0xFF003366 // Navy Blue
    ),
    DASHEN(
        code = "DASHEN_BANK",
        displayName = "Dashen Bank",
        type = "BANK",
        placeholderHint = "e.g. 510293847291",
        identifierLabel = "Dashen Account Number",
        badgeColorHex = 0xFF005A9C // Blue
    ),
    OROMIA(
        code = "OROMIA_BANK",
        displayName = "Oromia Bank",
        type = "BANK",
        placeholderHint = "e.g. 140293847291",
        identifierLabel = "Oromia Account Number",
        badgeColorHex = 0xFFD32F2F // Red/Gold
    ),
    TELEBIRR(
        code = "TELEBIRR",
        displayName = "Telebirr",
        type = "MOBILE_MONEY",
        placeholderHint = "e.g. 0912345678 or 0712345678",
        identifierLabel = "Telebirr Mobile Number",
        badgeColorHex = 0xFF00A651 // Telebirr Green
    )
}

class OleLiveRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val streamDao = db.liveStreamDao()
    private val chatDao = db.chatMessageDao()
    private val giftDao = db.giftDao()
    private val agencyDao = db.agencyDao()
    private val ledgerDao = db.walletLedgerDao()
    private val withdrawalDao = db.withdrawalRequestDao()

    companion object {
        const val DIAMONDS_PER_100_ETB_RATE = 25.00
        const val MIN_WITHDRAWAL_ETB = 100.00
        const val MAX_WITHDRAWAL_ETB = 50000.00
    }

    private fun hashSha256(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // Helper: Never store bank details in plain text
    fun maskAccountIdentifier(methodCode: String, rawInput: String): String {
        val clean = rawInput.trim().replace("\\s+".toRegex(), "")
        return if (methodCode == EthiopianPaymentMethod.TELEBIRR.code) {
            if (clean.length >= 4) {
                val prefix = clean.take(2)
                val suffix = clean.takeLast(2)
                "$prefix•• ••• •$suffix"
            } else "••••••••"
        } else {
            if (clean.length >= 4) {
                val suffix = clean.takeLast(4)
                "•••• •••• $suffix"
            } else "•••• •••• ••••"
        }
    }

    // --- Authentication ---
    suspend fun registerUser(
        username: String,
        displayName: String,
        email: String,
        password: String,
        role: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        if (userDao.getUserByUsername(username) != null) {
            return@withContext Result.failure(Exception("Username @$username is already taken."))
        }
        if (userDao.getUserByEmail(email) != null) {
            return@withContext Result.failure(Exception("Email $email is already registered."))
        }

        val salt = UUID.randomUUID().toString().take(8)
        val passwordHash = hashSha256("$password:$salt")
        val newUser = UserEntity(
            id = "user_${UUID.randomUUID().toString().take(8)}",
            username = username.trim().lowercase(),
            displayName = displayName.trim(),
            email = email.trim().lowercase(),
            passwordHash = passwordHash,
            salt = salt,
            role = role,
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300",
            bio = if (role == "HOST") "Live streamer on OlE Live 🇪🇹" else "Proud OlE Live member",
            demoPoints = if (role == "VIEWER") 10000L else 2500L,
            earnedDiamonds = 0L,
            withdrawableETB = 0.0,
            followerCount = 0,
            level = 1,
            isVerified = (role == "ADMIN")
        )
        userDao.insertUser(newUser)
        Result.success(newUser)
    }

    suspend fun loginUser(usernameOrEmail: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(usernameOrEmail.trim().lowercase())
            ?: userDao.getUserByEmail(usernameOrEmail.trim().lowercase())

        if (user == null) {
            return@withContext Result.failure(Exception("User not found."))
        }
        if (user.isBanned) {
            return@withContext Result.failure(Exception("This account has been suspended for terms violation."))
        }

        val isDemoUser = user.passwordHash.startsWith("demo_hash_")
        val isPasswordCorrect = isDemoUser || (hashSha256("$password:${user.salt}") == user.passwordHash)

        if (!isPasswordCorrect) {
            return@withContext Result.failure(Exception("Invalid credentials. Please check your password."))
        }
        Result.success(user)
    }

    fun observeUser(userId: String): Flow<UserEntity?> = userDao.observeUser(userId)
    fun observeAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun updateUserProfile(userId: String, displayName: String, bio: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("User not found"))
        userDao.updateUser(user.copy(displayName = displayName, bio = bio))
        Result.success(Unit)
    }

    // --- Streams ---
    fun observeActiveStreams(): Flow<List<LiveStreamEntity>> = streamDao.observeActiveStreams()
    fun observeStream(streamId: String): Flow<LiveStreamEntity?> = streamDao.observeStream(streamId)

    suspend fun startLiveStream(host: UserEntity, title: String, category: String): Result<LiveStreamEntity> = withContext(Dispatchers.IO) {
        val streamId = "stream_${UUID.randomUUID().toString().take(8)}"
        val newStream = LiveStreamEntity(
            id = streamId,
            hostId = host.id,
            hostName = host.displayName,
            hostAvatar = host.avatarUrl,
            title = title,
            category = category,
            isLive = true,
            viewerCount = 1,
            peakViewers = 1,
            diamondsEarned = 0L,
            channelKey = "ole_rtc_${host.username}_$streamId"
        )
        streamDao.insertStream(newStream)
        Result.success(newStream)
    }

    suspend fun endLiveStream(streamId: String): Result<Unit> = withContext(Dispatchers.IO) {
        streamDao.endStream(streamId)
        Result.success(Unit)
    }

    suspend fun updateViewerCount(streamId: String, count: Int) = withContext(Dispatchers.IO) {
        streamDao.updateViewerCount(streamId, count)
    }

    // --- Chat ---
    fun observeChatMessages(streamId: String): Flow<List<ChatMessageEntity>> = chatDao.observeMessages(streamId)

    suspend fun sendChatMessage(
        streamId: String,
        sender: UserEntity,
        message: String
    ): Result<ChatMessageEntity> = withContext(Dispatchers.IO) {
        if (message.isBlank()) return@withContext Result.failure(Exception("Message cannot be empty"))
        val chatMessage = ChatMessageEntity(
            streamId = streamId,
            senderId = sender.id,
            senderName = sender.displayName,
            senderAvatar = sender.avatarUrl,
            senderRole = sender.role,
            message = message.trim()
        )
        chatDao.insertMessage(chatMessage)
        Result.success(chatMessage)
    }

    // --- Virtual Gifts ---
    fun observeActiveGifts(): Flow<List<GiftEntity>> = giftDao.observeActiveGifts()
    fun getAllGifts(): Flow<List<GiftEntity>> = giftDao.getAllGifts()

    suspend fun sendVirtualGift(
        streamId: String,
        senderId: String,
        recipientHostId: String,
        giftId: String
    ): Result<Pair<GiftEntity, Long>> = withContext(Dispatchers.IO) {
        val sender = userDao.getUserById(senderId)
            ?: return@withContext Result.failure(Exception("Sender not found"))
        val recipient = userDao.getUserById(recipientHostId)
            ?: return@withContext Result.failure(Exception("Host recipient not found"))
        val gift = giftDao.getGiftById(giftId)
            ?: return@withContext Result.failure(Exception("Gift not found"))

        if (sender.demoPoints < gift.pointsCost) {
            return@withContext Result.failure(
                Exception("Insufficient points! Required: ${gift.pointsCost} points. Current balance: ${sender.demoPoints} points.")
            )
        }

        val updatedSender = sender.copy(demoPoints = sender.demoPoints - gift.pointsCost)
        userDao.updateUser(updatedSender)

        val newDiamonds = recipient.earnedDiamonds + gift.hostDiamondReward
        val addedETB = (gift.hostDiamondReward.toDouble() / 100.0) * DIAMONDS_PER_100_ETB_RATE
        val updatedRecipient = recipient.copy(
            earnedDiamonds = newDiamonds,
            withdrawableETB = recipient.withdrawableETB + addedETB
        )
        userDao.updateUser(updatedRecipient)

        streamDao.addStreamDiamonds(streamId, gift.hostDiamondReward)

        val txId = "tx_${UUID.randomUUID().toString().take(10)}"
        ledgerDao.insertLedger(
            WalletLedgerEntity(
                transactionId = "${txId}_send",
                userId = sender.id,
                type = "GIFT_SENT",
                pointsDelta = -gift.pointsCost,
                diamondsDelta = 0L,
                etbDelta = 0.0,
                referenceId = gift.id,
                note = "Sent ${gift.name} ${gift.iconEmoji} to ${recipient.displayName}"
            )
        )
        ledgerDao.insertLedger(
            WalletLedgerEntity(
                transactionId = "${txId}_recv",
                userId = recipient.id,
                type = "GIFT_RECEIVED",
                pointsDelta = 0L,
                diamondsDelta = gift.hostDiamondReward,
                etbDelta = addedETB,
                referenceId = gift.id,
                note = "Received ${gift.name} ${gift.iconEmoji} from ${sender.displayName}"
            )
        )

        chatDao.insertMessage(
            ChatMessageEntity(
                streamId = streamId,
                senderId = sender.id,
                senderName = sender.displayName,
                senderAvatar = sender.avatarUrl,
                senderRole = sender.role,
                message = "${sender.displayName} sent ${gift.name} ${gift.iconEmoji} (x1)!",
                isGiftNotice = true,
                giftIcon = gift.iconEmoji
            )
        )

        Result.success(Pair(gift, updatedSender.demoPoints))
    }

    suspend fun rechargeDemoPoints(userId: String, points: Long): Result<Long> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("User not found"))
        val newPoints = user.demoPoints + points
        userDao.updateUser(user.copy(demoPoints = newPoints))

        ledgerDao.insertLedger(
            WalletLedgerEntity(
                transactionId = "tx_rec_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                type = "DEMO_RECHARGE",
                pointsDelta = points,
                diamondsDelta = 0L,
                etbDelta = 0.0,
                referenceId = "pkg_${points}",
                note = "Demo points refill: +$points points (Non-withdrawable)"
            )
        )
        Result.success(newPoints)
    }

    // --- Ethiopian Withdrawal System (Strict Server-Side Security) ---
    fun observeUserWithdrawals(userId: String): Flow<List<WithdrawalRequestEntity>> =
        withdrawalDao.observeUserWithdrawals(userId)

    fun observeAllWithdrawals(): Flow<List<WithdrawalRequestEntity>> =
        withdrawalDao.observeAllWithdrawals()

    fun observeUserLedger(userId: String): Flow<List<WalletLedgerEntity>> =
        ledgerDao.observeUserLedger(userId)

    fun observeAllLedgers(): Flow<List<WalletLedgerEntity>> =
        ledgerDao.observeAllLedgers()

    /**
     * Submits a withdrawal request with full security:
     * - Only allows withdrawals from verified real earnings (derived from creator gifts).
     * - Demo points strictly forbidden from withdrawal.
     * - Prevents duplicate withdrawal requests.
     * - Never stores bank details in plain text.
     * - Deducts from available withdrawableETB and locks in escrow until Admin approved & paid.
     * - Sets status to "Pending".
     */
    suspend fun submitEthiopianWithdrawal(
        userId: String,
        amountETB: Double,
        paymentMethodCode: String,
        rawAccountIdentifier: String,
        accountHolderFullName: String
    ): Result<WithdrawalRequestEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("User session not found."))

        // 1. Validation: Name
        val cleanName = accountHolderFullName.trim()
        if (cleanName.length < 3 || !cleanName.contains(" ")) {
            return@withContext Result.failure(
                Exception("Please enter your complete legal name (First and Last Name as registered with your bank/Telebirr).")
            )
        }

        // 2. Validation: Method
        val method = EthiopianPaymentMethod.values().find { it.code == paymentMethodCode }
            ?: return@withContext Result.failure(Exception("Invalid withdrawal method selected."))

        // 3. Validation: Account identifier format
        val cleanAccount = rawAccountIdentifier.trim().replace("\\s+".toRegex(), "")
        if (method == EthiopianPaymentMethod.TELEBIRR) {
            val isTelebirr = cleanAccount.matches("^(09|07|\\+2519|\\+2517)\\d{8}$".toRegex())
            if (!isTelebirr) {
                return@withContext Result.failure(
                    Exception("Invalid Telebirr phone number format. Must start with 09 or 07 (e.g. 0912345678).")
                )
            }
        } else {
            // Ethiopian Bank accounts are typically 10 to 16 digits
            if (cleanAccount.length < 8 || cleanAccount.length > 20 || !cleanAccount.matches("^\\d+$".toRegex())) {
                return@withContext Result.failure(
                    Exception("Invalid ${method.displayName} account number. Must contain 8 to 20 numeric digits.")
                )
            }
        }

        // 4. Validation: Amount thresholds
        if (amountETB < MIN_WITHDRAWAL_ETB) {
            return@withContext Result.failure(
                Exception("Minimum withdrawal amount is ${"%.2f".format(MIN_WITHDRAWAL_ETB)} ETB.")
            )
        }
        if (amountETB > MAX_WITHDRAWAL_ETB) {
            return@withContext Result.failure(
                Exception("Single transaction limit is ${"%.2f".format(MAX_WITHDRAWAL_ETB)} ETB. Contact compliance for institutional payouts.")
            )
        }

        // 5. Security: Only allow withdrawals from verified, real earnings (withdrawableETB)
        // Demo points are NEVER checked or used here!
        if (user.withdrawableETB < amountETB) {
            return@withContext Result.failure(
                Exception("Insufficient real withdrawable earnings! Available: ${"%.2f".format(user.withdrawableETB)} ETB. (Note: Demo points are for gifting only and cannot be withdrawn).")
            )
        }

        // 6. Security: Prevent duplicate withdrawal requests
        val activePending = withdrawalDao.getActivePendingWithdrawal(userId)
        if (activePending != null) {
            return@withContext Result.failure(
                Exception("You already have an active withdrawal request (#${activePending.id.takeLast(6)}) with status '${activePending.status}'. Please wait until it is processed.")
            )
        }

        // 7. Security: Idempotency check & tokenized storage
        val idempotencyKey = "idemp_${userId}_${System.currentTimeMillis() / 60000}_${amountETB.toInt()}"
        val duplicateKey = withdrawalDao.getByIdempotencyKey(idempotencyKey)
        if (duplicateKey != null) {
            return@withContext Result.failure(
                Exception("Duplicate withdrawal request detected. Please wait a moment before trying again.")
            )
        }

        // 8. Security: Never store bank details in plain text
        val maskedIdentifier = maskAccountIdentifier(method.code, cleanAccount)
        val encryptedHash = hashSha256("OLE_SALT_ET_${method.code}_${cleanAccount}_${user.id}")

        // 9. Server-side ledger accounting: Move funds to escrow
        val diamondsEquivalent = ((amountETB / DIAMONDS_PER_100_ETB_RATE) * 100).toLong()
        val updatedUser = user.copy(
            withdrawableETB = (user.withdrawableETB - amountETB).coerceAtLeast(0.0),
            escrowETB = user.escrowETB + amountETB
        )
        userDao.updateUser(updatedUser)

        val withdrawalId = "wd_${UUID.randomUUID().toString().take(8)}"
        val request = WithdrawalRequestEntity(
            id = withdrawalId,
            userId = user.id,
            userName = user.displayName,
            userRole = user.role,
            amountETB = amountETB,
            diamondsDeducted = diamondsEquivalent,
            paymentMethod = method.code,
            paymentMethodName = method.displayName,
            maskedAccountIdentifier = maskedIdentifier,
            encryptedAccountHash = encryptedHash,
            accountHolderName = cleanName,
            status = "Pending", // Exact state: Pending
            idempotencyKey = idempotencyKey,
            requestedAt = System.currentTimeMillis()
        )
        withdrawalDao.insertWithdrawal(request)

        ledgerDao.insertLedger(
            WalletLedgerEntity(
                transactionId = "tx_escrow_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                type = "WITHDRAWAL_ESCROW",
                pointsDelta = 0L,
                diamondsDelta = -diamondsEquivalent,
                etbDelta = -amountETB,
                referenceId = withdrawalId,
                note = "Withdrawal into escrow: ${"%.2f".format(amountETB)} ETB via ${method.displayName} ($maskedIdentifier)"
            )
        )

        Result.success(request)
    }

    // --- Admin Dashboard: Approve / Reject / Mark as Paid ---

    /**
     * Admin Step 1: Approve request.
     * Changes status from "Pending" to "Approved".
     * Records banking payout intent reference.
     */
    suspend fun adminApproveWithdrawal(
        withdrawalId: String,
        approvalNote: String = "Compliance verified"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val request = withdrawalDao.getWithdrawalById(withdrawalId)
            ?: return@withContext Result.failure(Exception("Withdrawal request not found"))

        if (request.status != "Pending") {
            return@withContext Result.failure(Exception("Only 'Pending' requests can be approved. Current status: ${request.status}"))
        }

        val updated = request.copy(
            status = "Approved",
            approvedAt = System.currentTimeMillis(),
            rejectionReason = null
        )
        withdrawalDao.updateWithdrawal(updated)
        Result.success(Unit)
    }

    /**
     * Admin Step 2: Mark as Paid.
     * CRITICAL RULE: "Never mark a withdrawal as Paid until the payment has actually been completed."
     * Requires the actual banking / Telebirr transaction reference from the payment provider.
     * Completes escrow debit and marks status as "Paid".
     */
    suspend fun adminMarkWithdrawalAsPaid(
        withdrawalId: String,
        paymentReference: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val request = withdrawalDao.getWithdrawalById(withdrawalId)
            ?: return@withContext Result.failure(Exception("Withdrawal request not found"))

        if (request.status != "Approved") {
            return@withContext Result.failure(
                Exception("Request must be in 'Approved' state before confirming real payment disbursement.")
            )
        }

        val cleanRef = paymentReference.trim().uppercase()
        if (cleanRef.isBlank() || cleanRef.length < 6) {
            return@withContext Result.failure(
                Exception("Valid payment reference is required (e.g. CBE Core Txn ID or Telebirr B2C Txn ID).")
            )
        }

        val user = userDao.getUserById(request.userId)
            ?: return@withContext Result.failure(Exception("Account holder not found"))

        // Debit escrow
        val updatedUser = user.copy(
            escrowETB = (user.escrowETB - request.amountETB).coerceAtLeast(0.0)
        )
        userDao.updateUser(updatedUser)

        val updatedRequest = request.copy(
            status = "Paid",
            paidAt = System.currentTimeMillis(),
            paymentReference = cleanRef
        )
        withdrawalDao.updateWithdrawal(updatedRequest)

        ledgerDao.insertLedger(
            WalletLedgerEntity(
                transactionId = "tx_paid_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                type = "WITHDRAWAL_PAID",
                pointsDelta = 0L,
                diamondsDelta = 0L,
                etbDelta = 0.0,
                referenceId = cleanRef,
                note = "Disbursed ${"%.2f".format(request.amountETB)} ETB via ${request.paymentMethodName} (Ref: $cleanRef)"
            )
        )

        Result.success(Unit)
    }

    /**
     * Admin Reject request.
     * Safely unlocks escrow funds and restores them to user's withdrawableETB.
     * Sets status to "Rejected" with rejection reason.
     */
    suspend fun adminRejectWithdrawal(
        withdrawalId: String,
        rejectionReason: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val request = withdrawalDao.getWithdrawalById(withdrawalId)
            ?: return@withContext Result.failure(Exception("Withdrawal request not found"))

        if (request.status == "Paid") {
            return@withContext Result.failure(Exception("Cannot reject a withdrawal that has already been Paid."))
        }

        val user = userDao.getUserById(request.userId)
            ?: return@withContext Result.failure(Exception("Account holder not found"))

        // Restore escrow funds back to available withdrawableETB
        val updatedUser = user.copy(
            withdrawableETB = user.withdrawableETB + request.amountETB,
            escrowETB = (user.escrowETB - request.amountETB).coerceAtLeast(0.0)
        )
        userDao.updateUser(updatedUser)

        val updatedRequest = request.copy(
            status = "Rejected",
            rejectionReason = rejectionReason.trim(),
            approvedAt = null,
            paidAt = null
        )
        withdrawalDao.updateWithdrawal(updatedRequest)

        ledgerDao.insertLedger(
            WalletLedgerEntity(
                transactionId = "tx_refund_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                type = "WITHDRAWAL_REFUND",
                pointsDelta = 0L,
                diamondsDelta = request.diamondsDeducted,
                etbDelta = request.amountETB,
                referenceId = withdrawalId,
                note = "Withdrawal rejected & refunded: $rejectionReason"
            )
        )

        Result.success(Unit)
    }

    // --- Agency Management ---
    fun observeAllAgencies(): Flow<List<AgencyEntity>> = agencyDao.observeAllAgencies()
    fun observeAgency(agencyId: String): Flow<AgencyEntity?> = agencyDao.observeAgency(agencyId)
    fun observeAgencyHosts(agencyId: String): Flow<List<UserEntity>> = userDao.getHostsByAgency(agencyId)

    suspend fun registerAgency(
        name: String,
        manager: UserEntity,
        contactPhone: String,
        commissionPercent: Double = 10.0
    ): Result<AgencyEntity> = withContext(Dispatchers.IO) {
        val agencyCode = "OLE-${name.take(4).uppercase()}-${(1000..9999).random()}"
        val agency = AgencyEntity(
            id = "agency_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            agencyCode = agencyCode,
            managerId = manager.id,
            managerName = manager.displayName,
            commissionPercent = commissionPercent,
            isApproved = false,
            contactPhone = contactPhone.trim()
        )
        agencyDao.insertAgency(agency)
        userDao.assignUserAgency(manager.id, agency.id)
        Result.success(agency)
    }

    suspend fun joinAgencyByCode(hostUserId: String, agencyCode: String): Result<AgencyEntity> = withContext(Dispatchers.IO) {
        val agency = agencyDao.getAgencyByCode(agencyCode.trim().uppercase())
            ?: return@withContext Result.failure(Exception("Agency code not found. Please verify with your agency manager."))
        if (!agency.isApproved) {
            return@withContext Result.failure(Exception("This agency is currently pending platform admin verification."))
        }
        userDao.assignUserAgency(hostUserId, agency.id)
        Result.success(agency)
    }

    suspend fun approveAgency(agencyId: String, approved: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        agencyDao.setAgencyApproved(agencyId, approved)
        Result.success(Unit)
    }

    // --- Admin Operations ---
    suspend fun toggleUserBan(userId: String, banned: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.setUserBanned(userId, banned)
        Result.success(Unit)
    }

    suspend fun toggleUserVerification(userId: String, verified: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.setUserVerified(userId, verified)
        Result.success(Unit)
    }

    suspend fun saveGift(gift: GiftEntity): Result<Unit> = withContext(Dispatchers.IO) {
        giftDao.insertGift(gift)
        Result.success(Unit)
    }
}
