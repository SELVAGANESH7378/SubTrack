package com.selvaganesh7378.subtrack.ui.screens.profile

import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.selvaganesh7378.subtrack.domain.model.Profile
import com.selvaganesh7378.subtrack.presentation.profile.ProfileViewModel
import com.selvaganesh7378.subtrack.ui.theme.dangerBg
import com.selvaganesh7378.subtrack.ui.theme.dangerBorder
import com.selvaganesh7378.subtrack.ui.theme.dangerRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle Toasts for success/error messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.uploadProfileImage(uri.toString())
            }
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Profile") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshProfile() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val profile = uiState.profile
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Top Profile Header ---
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        ) {
                            ProfilePhoto(
                                imageUrl = profile?.photoUrl,
                                isUploading = uiState.isUploadingImage,
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = profile?.name ?: "User",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = profile?.email ?: "",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Chip(profile?.currency ?: "USD")
                                Chip("member since ${profile?.createdAt}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Profile Info (Name & Timezone) ---
                    ProfileInformationCard(
                        profile = profile,
                        isSaving = uiState.isSavingProfile,
                        onSave = { newName, email, newTimezone, newCurrency->
                            viewModel.updateProfile(newName, email, newTimezone, newCurrency)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Password Update ---
                    ChangePasswordCard(
                        isUpdating = uiState.isUpdatingPassword,
                        onUpdate = { oldPsw, newPsw ->
                            viewModel.updatePassword(oldPsw, newPsw)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Delete Account ---
                    DangerZoneCard(
                        isDeleting = uiState.isDeletingAccount,
                        onDelete = { viewModel.deleteAccount() }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInformationCard(
    profile: Profile?,
    isSaving: Boolean,
    onSave: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable(profile?.name) { mutableStateOf(profile?.name ?: "") }
    var email by rememberSaveable(profile?.email) { mutableStateOf(profile?.email ?: "") }
    var timeZone by rememberSaveable(profile?.timezone) { mutableStateOf(profile?.timezone ?: "America/New_York") }
    // NEW: Currency state
    var currency by rememberSaveable(profile?.currency) { mutableStateOf(profile?.currency ?: "USD") }

    val timezones = listOf(
        "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles",
        "Europe/London", "Europe/Paris", "Asia/Kolkata", "Asia/Tokyo", "Australia/Sydney"
    )

    val currencies = listOf("USD", "INR", "EUR", "GBP", "AED")

    var tzExpanded by rememberSaveable { mutableStateOf(false) }
    var currencyExpanded by rememberSaveable { mutableStateOf(false) } // NEW: Dropdown state

    val isNameValid = name.isNotBlank() && !name.all { it.isDigit() }
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = isNameValid && isEmailValid

    // UPDATE: Track changes for currency
    val hasChanges = name != (profile?.name ?: "") ||
            email != (profile?.email ?: "") ||
            timeZone != (profile?.timezone ?: "America/New_York") ||
            currency != (profile?.currency ?: "USD")

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Profile Information", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Update your personal details and preferences", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            ProfileInputField(
                label = "Full Name",
                labelIcon = Icons.Outlined.Person,
                value = name,
                onValueChange = { name = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInputField(
                label = "Email Address",
                labelIcon = Icons.Outlined.Email,
                value = email,
                onValueChange = { email = it },
                helperText = "Email changes coming soon"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // NEW: Currency Dropdown
            Box {
                ProfileInputField(
                    label = "Currency",
                    labelIcon = Icons.Outlined.Public,
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = Icons.Default.ArrowDropDown,
                    modifier = Modifier.clickable { currencyExpanded = true }
                )
                DropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text(curr) },
                            onClick = {
                                currency = curr
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Existing Timezone Dropdown
            Box {
                ProfileInputField(
                    label = "Timezone",
                    labelIcon = Icons.Outlined.Schedule,
                    value = timeZone,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = Icons.Default.ArrowDropDown,
                    modifier = Modifier.clickable { tzExpanded = true }
                )
                DropdownMenu(
                    expanded = tzExpanded,
                    onDismissRequest = { tzExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    timezones.forEach { tz ->
                        DropdownMenuItem(
                            text = { Text(tz) },
                            onClick = {
                                timeZone = tz
                                tzExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { onSave(name, email, timeZone, currency) },
                    enabled = !isSaving && isFormValid && hasChanges,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Check, "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun ChangePasswordCard(
    isUpdating: Boolean,
    onUpdate: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var oldPassword by rememberSaveable{ mutableStateOf("") }
    var newPassword by rememberSaveable{ mutableStateOf("") }
    var confirmPassword by rememberSaveable{ mutableStateOf("") }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Change Password", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            ProfileInputField(
                label = "Current Password",
                labelIcon = Icons.Outlined.Lock,
                value = oldPassword,
                onValueChange = { oldPassword = it },
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInputField(
                label = "New Password",
                labelIcon = Icons.Outlined.Lock,
                value = newPassword,
                onValueChange = { newPassword = it },
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInputField(
                label = "Confirm New Password",
                labelIcon = Icons.Outlined.Lock,
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                isPassword = true
            )
            Spacer(modifier = Modifier.height(24.dp))


            val isFormValid = oldPassword.isNotBlank() && newPassword.length >= 6 && newPassword == confirmPassword

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        onUpdate(oldPassword, newPassword)
                        // Clear fields after sending
                        oldPassword = ""; newPassword = ""; confirmPassword = ""
                    },
                    enabled = !isUpdating && isFormValid,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Lock, "Update", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Update Password")
                    }
                }
            }
        }
    }
}

@Composable
fun DangerZoneCard(isDeleting: Boolean, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, dangerBorder),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Danger Zone", color = dangerRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDelete,
                enabled = !isDeleting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = dangerBg, contentColor = dangerRed),
                modifier = Modifier.height(48.dp)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = dangerRed, strokeWidth = 2.dp)
                } else {
                    Text("Delete Account")
                }
            }
        }
    }
}

// Helper components below remain mostly the same, just updated ProfileInputField to handle passwords/readOnly
@Composable
fun ProfileInputField(
    label: String,
    labelIcon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    trailingIcon: ImageVector? = null,
    helperText: String? = null,
    readOnly: Boolean = false,
    isPassword: Boolean = false
) {
    var passwordVisible by rememberSaveable{ mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(labelIcon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            enabled = !readOnly, // Greys out the text slightly if it's read only like Email
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(placeholder) },
            trailingIcon = {
                if (isPassword) {
                    val icon = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                } else if (trailingIcon != null) {
                    Icon(trailingIcon, null)
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (helperText != null) {
            Text(helperText, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun ProfilePhoto(
    imageUrl: String?,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(enabled = !isUploading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrEmpty()) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Default Profile",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "User Profile Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            }
        }

        if (!isUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change Photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun Chip(text: String) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}