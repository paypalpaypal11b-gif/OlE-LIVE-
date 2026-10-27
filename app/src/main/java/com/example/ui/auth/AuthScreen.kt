package com.example.ui.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OleBlack
import com.example.ui.theme.OleBlackBorder
import com.example.ui.theme.OleBlackSurface
import com.example.ui.theme.OleBlackSurfaceVariant
import com.example.ui.theme.OleGold
import com.example.ui.theme.OleGrayText
import com.example.ui.theme.OleGreenAccent
import com.example.ui.theme.OleGreenPrimary
import com.example.ui.theme.OleRed

@Composable
fun AuthScreen(
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onRegister: (String, String, String, String, String, (Boolean, String?) -> Unit) -> Unit,
    onQuickDemoSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register

    var usernameOrEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    var regUsername by remember { mutableStateOf("") }
    var regDisplayName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("HOST") } // "VIEWER", "HOST", "AGENCY_MANAGER"

    var authError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OleBlack)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Brand Header
        item {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(OleGreenPrimary, Color(0xFF003816))
                        )
                    )
                    .border(2.dp, OleGreenAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "OlE Live",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Live Streaming & Ethiopian Birr Rewards",
                    color = OleGreenAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "🇪🇹", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Demo Accounts Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OleBlackSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OleGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚡ 1-Tap Quick Demo Sign-In",
                        color = OleGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickDemoButton(
                            label = "Host (Selam)",
                            role = "Creator",
                            onClick = { onQuickDemoSelect("user_host_selam") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickDemoButton(
                            label = "Viewer (Dawit)",
                            role = "Gifter",
                            onClick = { onQuickDemoSelect("user_viewer_dawit") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickDemoButton(
                            label = "Agency (Yared)",
                            role = "Manager",
                            onClick = { onQuickDemoSelect("user_agency_mgr") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickDemoButton(
                            label = "SuperAdmin",
                            role = "Treasury",
                            onClick = { onQuickDemoSelect("user_admin_ole") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Auth Tabs
        item {
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
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, OleBlackBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; authError = null },
                    text = { Text("Sign In", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_login")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; authError = null },
                    text = { Text("Create Account", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_register")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Form Fields
        if (selectedTab == 0) {
            // LOGIN FORM
            item {
                OutlinedTextField(
                    value = usernameOrEmail,
                    onValueChange = { usernameOrEmail = it },
                    label = { Text("Username or Email") },
                    placeholder = { Text("e.g. selam_live or selam@olelive.et", color = OleGrayText) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = OleGreenAccent) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = OleBlackSurface,
                        unfocusedContainerColor = OleBlackSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("login_username_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = loginPassword,
                    onValueChange = { loginPassword = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = OleGreenAccent) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = OleBlackSurface,
                        unfocusedContainerColor = OleBlackSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (authError != null) {
                    Text(authError!!, color = OleRed, fontSize = 12.sp, modifier = Modifier.padding(bottom = 10.dp))
                }

                Button(
                    onClick = {
                        isLoading = true
                        authError = null
                        onLogin(usernameOrEmail, loginPassword) { success, err ->
                            isLoading = false
                            if (!success) authError = err ?: "Sign-in failed."
                        }
                    },
                    enabled = !isLoading && usernameOrEmail.isNotBlank() && loginPassword.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_submit_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Sign In to OlE Live", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // REGISTRATION FORM
            item {
                OutlinedTextField(
                    value = regUsername,
                    onValueChange = { regUsername = it },
                    label = { Text("Username") },
                    placeholder = { Text("e.g. aster_addis", color = OleGrayText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = OleBlackSurface,
                        unfocusedContainerColor = OleBlackSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_username_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = regDisplayName,
                    onValueChange = { regDisplayName = it },
                    label = { Text("Display Name") },
                    placeholder = { Text("e.g. Aster Aweke", color = OleGrayText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = OleBlackSurface,
                        unfocusedContainerColor = OleBlackSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_display_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = regEmail,
                    onValueChange = { regEmail = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("e.g. aster@gmail.com", color = OleGrayText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = OleBlackSurface,
                        unfocusedContainerColor = OleBlackSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OleGreenPrimary,
                        unfocusedBorderColor = OleBlackBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = OleBlackSurface,
                        unfocusedContainerColor = OleBlackSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role Selector
                Text("Select Primary Role:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("HOST" to "Creator Host", "VIEWER" to "Viewer", "AGENCY_MANAGER" to "Agency").forEach { (role, label) ->
                        val isSelected = regRole == role
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) OleGreenPrimary else OleBlackSurface)
                                .border(1.dp, if (isSelected) OleGreenAccent else OleBlackBorder, RoundedCornerShape(8.dp))
                                .clickable { regRole = role }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (isSelected) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (authError != null) {
                    Text(authError!!, color = OleRed, fontSize = 12.sp, modifier = Modifier.padding(bottom = 10.dp))
                }

                Button(
                    onClick = {
                        isLoading = true
                        authError = null
                        onRegister(regUsername, regDisplayName, regEmail, regPassword, regRole) { success, err ->
                            isLoading = false
                            if (!success) authError = err ?: "Registration failed."
                        }
                    },
                    enabled = !isLoading && regUsername.isNotBlank() && regPassword.isNotBlank() && regEmail.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = OleGreenPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("register_submit_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Create My Account", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickDemoButton(
    label: String,
    role: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(OleBlackSurfaceVariant)
            .border(1.dp, OleBlackBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(role, color = OleGreenAccent, fontSize = 9.sp)
        }
    }
}
