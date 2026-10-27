package com.example.ui.wallet

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.data.repository.EthiopianPaymentMethod
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleBlackSurfaceVariant
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary
import com.example.ui.theme.OleRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalScreen(
    currentUser: UserEntity?,
    userWithdrawals: List<WithdrawalRequestEntity>,
    onSubmitWithdrawal: (Double, String, String, String, (Boolean, String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Request Form, 1 = History & Status, 2 = Security & Integrations
    var selectedMethod by remember { mutableStateOf(EthiopianPaymentMethod.TELEBIRR) }
    var methodDropdownExpanded by remember { mutableStateOf(false) }

    var accountHolderName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var accountIdentifier by remember { mutableStateOf("") }
    var amountETBText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val withdrawableETB = currentUser?.withdrawableETB ?: 0.0
    val escrowETB = currentUser?.escrowETB ?: 0.0
    val demoPoints = currentUser?.demoPoints ?: 0L

    val hasPending = userWithdrawals.any { it.status == "Pending" || it.status == "Approved" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .testTag("withdrawal_screen")
    ) {
        // Balance Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F2E19),
                            OleBlackSurface
                        )
                    )
                )
                .border(1.dp, OleGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Verified Withdrawable Earnings 🇪🇹",
                            color = OleGreenAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "ETB ",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.2f".format(withdrawableETB),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OleBlackSurfaceVariant)
                            .border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Held in Escrow",
                                color = OleGrayText,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "%.2f ETB".format(escrowETB),
                                color = if (escrowETB > 0) OleGold else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = OleBlackBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Strict Demo Points vs Real Earnings Disclaimer
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = OleGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Demo Points ($demoPoints pts) are strictly for gifting and cannot be withdrawn.",
                        color = OleGrayText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Tabs: Form, History & Status, Integrations Architecture
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = OleBlackSurface,
            contentColor = OleGreenAccent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = OleGreenPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Withdraw",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_withdraw_form")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "History & Status (${userWithdrawals.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_withdraw_history")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "Bank Integrations",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_withdraw_integrations")
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Withdrawal Form
                WithdrawalFormView(
                    withdrawableETB = withdrawableETB,
                    hasPending = hasPending,
                    selectedMethod = selectedMethod,
                    onMethodSelected = { selectedMethod = it },
                    methodDropdownExpanded = methodDropdownExpanded,
                    onDropdownToggle = { methodDropdownExpanded = it },
                    accountHolderName = accountHolderName,
                    onNameChange = { accountHolderName = it },
                    accountIdentifier = accountIdentifier,
                    onAccountChange = { accountIdentifier = it },
                    amountETBText = amountETBText,
                    onAmountChange = { amountETBText = it },
                    errorMessage = errorMessage,
                    successMessage = successMessage,
                    isSubmitting = isSubmitting,
                    onSubmit = {
                        val amount = amountETBText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            errorMessage = "Please enter a valid amount in Ethiopian Birr (ETB)."
                            return@WithdrawalFormView
                        }
                        errorMessage = null
                        successMessage = null
                        isSubmitting = true
                        onSubmitWithdrawal(
                            amount,
                            selectedMethod.code,
                            accountIdentifier,
                            accountHolderName
                        ) { success, error ->
                            isSubmitting = false
                            if (success) {
                                successMessage = "Withdrawal request submitted successfully! Status: Pending review."
                                amountETBText = ""
                                accountIdentifier = ""
                            } else {
                                errorMessage = error ?: "Submission failed. Please check your details."
                            }
                        }
                    }
                )
            }
            1 -> {
                // History & Status list
                WithdrawalHistoryView(withdrawals = userWithdrawals)
            }
            2 -> {
                // Payment & Backend Integration Guide
                EthiopianIntegrationsGuideView()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalFormView(
    withdrawableETB: Double,
    hasPending: Boolean,
    selectedMethod: EthiopianPaymentMethod,
    onMethodSelected: (EthiopianPaymentMethod) -> Unit,
    methodDropdownExpanded: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    accountHolderName: String,
    onNameChange: (String) -> Unit,
    accountIdentifier: String,
    onAccountChange: (String) -> Unit,
    amountETBText: String,
    onAmountChange: (String) -> Unit,
    errorMessage: String?,
    successMessage: String?,
    isSubmitting: Boolean,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Warning if user has an active pending request
        if (hasPending) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF332A15)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OleGold, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Pending",
                            tint = OleGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You have an active withdrawal request under review. New requests can be submitted once it is resolved.",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 1. Withdrawal Method Selection
        item {
            Text(
                text = "1. Select Withdrawal Method",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = methodDropdownExpanded,
                onExpandedChange = onDropdownToggle,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedMethod.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodDropdownExpanded) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (selectedMethod.type == "MOBILE_MONEY") Icons.Default.PhoneAndroid else Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = OleGreenAccent
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedLabelColor = OleGreenAccent,
                        unfocusedContainerColor = OleBlackSurface,
                        focusedContainerColor = OleBlackSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("method_dropdown")
                )

                ExposedDropdownMenu(
                    expanded = methodDropdownExpanded,
                    onDismissRequest = { onDropdownToggle(false) },
                    modifier = Modifier.background(OleBlackSurface)
                ) {
                    EthiopianPaymentMethod.values().forEach { method ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (method.type == "MOBILE_MONEY") Icons.Default.PhoneAndroid else Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Color(method.badgeColorHex),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = method.displayName,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (method == selectedMethod) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = {
                                onMethodSelected(method)
                                onDropdownToggle(false)
                            },
                            modifier = Modifier.testTag("method_item_${method.code.lowercase()}")
                        )
                    }
                }
            }
        }

        // 2. Account Holder Full Name
        item {
            Text(
                text = "2. Account Holder's Full Name",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = accountHolderName,
                onValueChange = onNameChange,
                label = { Text("Full Legal Name (as on ID / Bank)") },
                placeholder = { Text("e.g. Abebe Bikila Chala", color = OleGrayText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OleGreenPrimary,
                    unfocusedBorderColor = OleBlackBorder,
                    focusedLabelColor = OleGreenAccent,
                    unfocusedContainerColor = OleBlackSurface,
                    focusedContainerColor = OleBlackSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_holder_name_input")
            )
        }

        // 3. Account Number / Telebirr Phone Number
        item {
            Text(
                text = "3. ${selectedMethod.identifierLabel}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = accountIdentifier,
                onValueChange = onAccountChange,
                label = { Text(selectedMethod.identifierLabel) },
                placeholder = { Text(selectedMethod.placeholderHint, color = OleGrayText) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = OleGreenAccent,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OleGreenPrimary,
                    unfocusedBorderColor = OleBlackBorder,
                    focusedLabelColor = OleGreenAccent,
                    unfocusedContainerColor = OleBlackSurface,
                    focusedContainerColor = OleBlackSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_identifier_input")
            )
            Text(
                text = "🔒 Security: Account details are encrypted with HMAC-SHA256 and never stored in plain text.",
                color = OleGrayText,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        // 4. Withdrawal Amount in ETB
        item {
            Text(
                text = "4. Withdrawal Amount (Ethiopian Birr)",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountETBText,
                onValueChange = onAmountChange,
                label = { Text("Amount in ETB (Min: 100 ETB)") },
                placeholder = { Text("e.g. 500.00", color = OleGrayText) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .clickable {
                                if (withdrawableETB >= 100.0) {
                                    onAmountChange("%.2f".format(withdrawableETB))
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "MAX",
                            color = OleGreenAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OleGreenPrimary,
                    unfocusedBorderColor = OleBlackBorder,
                    focusedLabelColor = OleGreenAccent,
                    unfocusedContainerColor = OleBlackSurface,
                    focusedContainerColor = OleBlackSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("withdrawal_amount_input")
            )
        }

        // Error message banner
        if (errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1515)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OleRed, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = OleRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Success message banner
        if (successMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14331C)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OleGreenAccent, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = OleGreenAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = onSubmit,
                enabled = !isSubmitting && !hasPending && withdrawableETB >= 100.0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OleGreenPrimary,
                    contentColor = Color.Black,
                    disabledContainerColor = OleBlackBorder,
                    disabledContentColor = OleGrayText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_withdrawal_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasPending) "Pending Request Active" else "Submit Withdrawal Request",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Trust badge
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Secured",
                    tint = OleGreenAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "National Bank of Ethiopia (NBE) & EthSwitch Compliance Gateway",
                    color = OleGrayText,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun WithdrawalHistoryView(
    withdrawals: List<WithdrawalRequestEntity>,
    modifier: Modifier = Modifier
) {
    if (withdrawals.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = OleGrayText,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Withdrawal Requests Yet",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Submit a request to withdraw real creator earnings via CBE, Telebirr, Abyssinia, Awash, Dashen, or Oromia Bank.",
                    color = OleGrayText,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(withdrawals) { item ->
                WithdrawalStatusCard(item = item)
            }
        }
    }
}

@Composable
fun WithdrawalStatusCard(item: WithdrawalRequestEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    // 4 exact statuses: Pending, Approved, Rejected, Paid
    val (statusColor, statusBg, statusIcon) = when (item.status) {
        "Paid" -> Triple(OleGreenAccent, Color(0xFF13361E), Icons.Default.CheckCircle)
        "Approved" -> Triple(OleGold, Color(0xFF382E14), Icons.Default.CheckCircle)
        "Rejected" -> Triple(OleRed, Color(0xFF3B1515), Icons.Default.Close)
        else -> Triple(Color(0xFF40C4FF), Color(0xFF142838), Icons.Default.HourglassTop) // "Pending"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
            .testTag("withdrawal_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Method & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.paymentMethod == "TELEBIRR") Icons.Default.PhoneAndroid else Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = OleGreenAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.paymentMethodName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Exact 4 status chip: Pending, Approved, Rejected, Paid
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .border(1.dp, statusColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = item.status,
                            tint = statusColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.status,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Amount Requested",
                        color = OleGrayText,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "ETB %.2f".format(item.amountETB),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Account (Masked)",
                        color = OleGrayText,
                        fontSize = 11.sp
                    )
                    Text(
                        text = item.maskedAccountIdentifier,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = OleBlackBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Timestamps and References
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Requested: ${dateFormat.format(Date(item.requestedAt))}",
                    color = OleGrayText,
                    fontSize = 10.sp
                )
                Text(
                    text = "Holder: ${item.accountHolderName}",
                    color = OleGrayText,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // If Paid: Display Payment Reference
            if (item.status == "Paid" && item.paymentReference != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D2415))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✅ Confirmed Bank Txn Ref: ${item.paymentReference}",
                        color = OleGreenAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // If Rejected: Display Reason
            if (item.status == "Rejected" && item.rejectionReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E1111))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "❌ Rejection Reason: ${item.rejectionReason} (Escrow funds refunded)",
                        color = OleRed,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EthiopianIntegrationsGuideView(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Ethiopian Payment Gateways & Backend Setup 🇪🇹",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Required cloud services, APIs, security protocols, and banking rails for production withdrawals.",
                color = OleGrayText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item {
            IntegrationDetailCard(
                title = "1. Telebirr B2C SuperApp Open API",
                subtitle = "Ethio Telecom Business-to-Consumer Gateway",
                details = listOf(
                    "API: Telebirr B2C Disbursement Endpoint (POST /api/v2/b2c/payment)",
                    "Credentials: App ID, App Key, Private RSA Key, Shortcode from partner.telebirr.et",
                    "Disbursement Time: Real-time (under 5 seconds directly to recipient wallet)",
                    "Charges: Standard Ethio Telecom B2C bulk disbursement fee (~0.5% - 1.0%)",
                    "Security: Webhook verification via SHA256-with-RSA digital signature"
                ),
                badgeColor = Color(0xFF00A651)
            )
        }

        item {
            IntegrationDetailCard(
                title = "2. Commercial Bank of Ethiopia (CBE) API",
                subtitle = "CBE Birr & Core Banking Host-to-Host (H2H)",
                details = listOf(
                    "Rail: CBE Core Banking ISO 8583 / REST Host-to-Host API",
                    "Disbursement: Direct account-to-account transfer (13-digit CBE account)",
                    "Verification: Account Name Enquiry (pre-validates account holder before sending)",
                    "Documentation: Commercial partner agreement with CBE Digital Banking Division",
                    "Webhook: Instant settlement confirmation callbacks with Unique RRN"
                ),
                badgeColor = Color(0xFF7A1D65)
            )
        }

        item {
            IntegrationDetailCard(
                title = "3. Commercial Banks (Abyssinia, Awash, Dashen, Oromia)",
                subtitle = "National Switch (EthSwitch) & Aggregators",
                details = listOf(
                    "National Switch: EthSwitch P2P / B2C interoperability network",
                    "Certified Aggregators: Chapa Financial Technologies, SantimPay, ArifPay",
                    "Settlement Window: Instant via RTGS / ACH for interbank settlement",
                    "Account Masking: Client apps must display only masked PAN / Account numbers",
                    "Anti-Fraud: Cooldown throttles and daily NBE transaction ceilings"
                ),
                badgeColor = Color(0xFF1E88E5)
            )
        }

        item {
            IntegrationDetailCard(
                title = "4. Server-Side Security & Double-Entry Accounting",
                subtitle = "Strict Integrity Invariants",
                details = listOf(
                    "Escrow State Machine: Pending -> Approved -> Paid (or Rejected -> Refund)",
                    "Never mark Paid before payment confirmation from bank / Telebirr gateway",
                    "Idempotency: Cryptographic transaction keys to prevent duplicate clicks",
                    "Zero-Trust: Bank account numbers encrypted with AES-256 in production database",
                    "NBE Compliance: KYC level 2 verification for withdrawals above 10,000 ETB"
                ),
                badgeColor = OleGold
            )
        }
    }
}

@Composable
fun IntegrationDetailCard(
    title: String,
    subtitle: String,
    details: List<String>,
    badgeColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OleBlackBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = OleGreenAccent,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = OleBlackBorder)
            Spacer(modifier = Modifier.height(8.dp))

            details.forEach { detail ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        color = OleGreenAccent,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = detail,
                        color = Color(0xFFD3DDD5),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
