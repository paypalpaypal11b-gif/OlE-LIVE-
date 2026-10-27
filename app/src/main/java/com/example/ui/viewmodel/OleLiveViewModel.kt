package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AgencyEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.GiftEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletLedgerEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.data.repository.OleLiveRepository
import com.example.service.StreamingEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OleLiveViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = OleLiveRepository(db)
    val streamingEngine = StreamingEngine()

    private val _currentUserId = MutableStateFlow<String?>("user_host_selam") // Default demo host on cold start
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    // Observe active current user
    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.observeUser(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Streams
    val activeStreams: StateFlow<List<LiveStreamEntity>> = repository.observeActiveStreams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedStreamId = MutableStateFlow<String?>("stream_selam_01")
    val selectedStreamId: StateFlow<String?> = _selectedStreamId.asStateFlow()

    val selectedStream: StateFlow<LiveStreamEntity?> = _selectedStreamId.flatMapLatest { id ->
        if (id != null) repository.observeStream(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Chat messages for currently viewed stream
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _selectedStreamId.flatMapLatest { id ->
        if (id != null) repository.observeChatMessages(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gifts catalog
    val activeGifts: StateFlow<List<GiftEntity>> = repository.observeActiveGifts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User ledger transactions
    val userLedgers: StateFlow<List<WalletLedgerEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.observeUserLedger(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User withdrawals
    val userWithdrawals: StateFlow<List<WithdrawalRequestEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.observeUserWithdrawals(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin & Agency data
    val allWithdrawals: StateFlow<List<WithdrawalRequestEntity>> = repository.observeAllWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAgencies: StateFlow<List<AgencyEntity>> = repository.observeAllAgencies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agencyHosts: StateFlow<List<UserEntity>> = currentUser.flatMapLatest { user ->
        val agencyId = user?.agencyId
        if (agencyId != null) repository.observeAgencyHosts(agencyId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Gift Pop-up Animation: (Gift, SenderName)
    private val _giftAnimation = MutableStateFlow<Pair<GiftEntity, String>?>(null)
    val giftAnimation: StateFlow<Pair<GiftEntity, String>?> = _giftAnimation.asStateFlow()

    // Floating heart reaction counter for viewers
    private val _heartsCount = MutableStateFlow(0)
    val heartsCount: StateFlow<Int> = _heartsCount.asStateFlow()

    // Toast / Snack Notification Events
    private val _notificationMessage = MutableSharedFlow<String>()
    val notificationMessage: SharedFlow<String> = _notificationMessage.asSharedFlow()

    private var viewerSimulationJob: Job? = null

    init {
        // Start viewer pulse simulation for active stream
        startViewerPulse()
    }

    private fun startViewerPulse() {
        viewerSimulationJob?.cancel()
        viewerSimulationJob = viewModelScope.launch {
            while (true) {
                delay(4000)
                val current = selectedStream.value
                if (current != null && current.isLive) {
                    val delta = (-3..7).random()
                    val newCount = (current.viewerCount + delta).coerceAtLeast(1)
                    repository.updateViewerCount(current.id, newCount)
                }
            }
        }
    }

    fun selectStream(streamId: String) {
        _selectedStreamId.value = streamId
    }

    fun sendChatMessage(message: String) {
        val user = currentUser.value ?: return
        val stream = selectedStream.value ?: return
        viewModelScope.launch {
            val result = repository.sendChatMessage(stream.id, user, message)
            if (result.isFailure) {
                _notificationMessage.emit(result.exceptionOrNull()?.message ?: "Failed to send message")
            }
        }
    }

    fun sendGift(giftId: String) {
        val user = currentUser.value ?: return
        val stream = selectedStream.value ?: return
        viewModelScope.launch {
            val result = repository.sendVirtualGift(
                streamId = stream.id,
                senderId = user.id,
                recipientHostId = stream.hostId,
                giftId = giftId
            )
            result.fold(
                onSuccess = { (gift, _) ->
                    _giftAnimation.value = Pair(gift, user.displayName)
                    _notificationMessage.emit("Sent ${gift.name} ${gift.iconEmoji}!")
                    // Clear animation banner after 4.5 seconds
                    launch {
                        delay(4500)
                        if (_giftAnimation.value?.first?.id == gift.id) {
                            _giftAnimation.value = null
                        }
                    }
                },
                onFailure = { error ->
                    _notificationMessage.emit(error.message ?: "Failed to send gift")
                }
            )
        }
    }

    fun addHeartReaction() {
        _heartsCount.value += 1
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
        viewModelScope.launch {
            _notificationMessage.emit("Switched to demo account: $userId")
        }
    }

    fun login(usernameOrEmail: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(usernameOrEmail, pass)
            result.fold(
                onSuccess = { user ->
                    _currentUserId.value = user.id
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun register(
        username: String,
        displayName: String,
        email: String,
        pass: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(username, displayName, email, pass, role)
            result.fold(
                onSuccess = { user ->
                    _currentUserId.value = user.id
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun logout() {
        _currentUserId.value = null
    }

    fun startBroadcast(title: String, category: String, onStarted: (String) -> Unit) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.startLiveStream(user, title, category)
            result.fold(
                onSuccess = { stream ->
                    _selectedStreamId.value = stream.id
                    streamingEngine.startBroadcastSession(viewModelScope, stream.channelKey)
                    onStarted(stream.id)
                },
                onFailure = { err ->
                    _notificationMessage.emit(err.message ?: "Could not start broadcast")
                }
            )
        }
    }

    fun endBroadcast(streamId: String) {
        viewModelScope.launch {
            repository.endLiveStream(streamId)
            streamingEngine.stopBroadcastSession()
            _notificationMessage.emit("Live broadcast concluded.")
        }
    }

    fun rechargePoints(points: Long) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.rechargeDemoPoints(user.id, points)
            result.fold(
                onSuccess = { newBal ->
                    _notificationMessage.emit("Refilled +$points demo points! New balance: $newBal")
                },
                onFailure = { err ->
                    _notificationMessage.emit(err.message ?: "Recharge failed")
                }
            )
        }
    }

    fun submitWithdrawal(
        amountETB: Double,
        methodCode: String,
        accountIdentifier: String,
        holderFullName: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.submitEthiopianWithdrawal(
                userId = user.id,
                amountETB = amountETB,
                paymentMethodCode = methodCode,
                rawAccountIdentifier = accountIdentifier,
                accountHolderFullName = holderFullName
            )
            result.fold(
                onSuccess = { req ->
                    _notificationMessage.emit("Withdrawal request of ${"%.2f".format(amountETB)} ETB submitted (Status: Pending).")
                    onComplete(true, null)
                },
                onFailure = { err ->
                    onComplete(false, err.message)
                }
            )
        }
    }

    fun adminApproveWithdrawal(id: String, note: String = "Compliance verified") {
        viewModelScope.launch {
            val result = repository.adminApproveWithdrawal(id, note)
            result.fold(
                onSuccess = {
                    _notificationMessage.emit("Withdrawal approved for banking dispatch.")
                },
                onFailure = { err ->
                    _notificationMessage.emit(err.message ?: "Approval failed")
                }
            )
        }
    }

    fun adminMarkWithdrawalAsPaid(id: String, paymentReference: String) {
        viewModelScope.launch {
            val result = repository.adminMarkWithdrawalAsPaid(id, paymentReference)
            result.fold(
                onSuccess = {
                    _notificationMessage.emit("Disbursement confirmed & marked as Paid (Ref: $paymentReference)!")
                },
                onFailure = { err ->
                    _notificationMessage.emit(err.message ?: "Could not mark as paid")
                }
            )
        }
    }

    fun adminRejectWithdrawal(id: String, reason: String) {
        viewModelScope.launch {
            val result = repository.adminRejectWithdrawal(id, reason)
            result.fold(
                onSuccess = {
                    _notificationMessage.emit("Withdrawal rejected and escrow funds refunded.")
                },
                onFailure = { err ->
                    _notificationMessage.emit(err.message ?: "Rejection failed")
                }
            )
        }
    }

    fun registerAgency(name: String, phone: String, onDone: (Boolean, String?) -> Unit) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.registerAgency(name, user, phone)
            result.fold(
                onSuccess = { agency ->
                    _notificationMessage.emit("Agency '${agency.name}' registered! Code: ${agency.agencyCode}")
                    onDone(true, null)
                },
                onFailure = { err ->
                    onDone(false, err.message)
                }
            )
        }
    }

    fun joinAgency(code: String, onDone: (Boolean, String?) -> Unit) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.joinAgencyByCode(user.id, code)
            result.fold(
                onSuccess = { agency ->
                    _notificationMessage.emit("Successfully joined agency: ${agency.name}!")
                    onDone(true, null)
                },
                onFailure = { err ->
                    onDone(false, err.message)
                }
            )
        }
    }

    fun toggleUserBan(userId: String, banned: Boolean) {
        viewModelScope.launch {
            repository.toggleUserBan(userId, banned)
            _notificationMessage.emit(if (banned) "User suspended." else "User restored.")
        }
    }

    fun toggleUserVerification(userId: String, verified: Boolean) {
        viewModelScope.launch {
            repository.toggleUserVerification(userId, verified)
            _notificationMessage.emit("Verification status updated.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        streamingEngine.stopBroadcastSession()
        viewerSimulationJob?.cancel()
    }
}
