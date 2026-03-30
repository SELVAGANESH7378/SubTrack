package com.selvaganesh7378.subtrack.ui.screens.subscription.addeditsubscription

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.selvaganesh7378.subtrack.ui.theme.ColorSuccess
import com.selvaganesh7378.subtrack.ui.theme.dangerBg
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: AddEditSubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showRenewalDatePicker by remember { mutableStateOf(false) }

    val categories = listOf("Entertainment", "Development", "Design", "Productivity", "Communication", "Storage", "Marketing", "Finance", "Other")
    val currencies = listOf("USD", "INR", "EUR", "GBP", "CAD", "AUD", "JPY")
    val paymentMethods = listOf("Credit Card", "Debit Card", "PayPal", "Bank Transfer", "UPI", "Other")

    // Dynamic UI Text based on Edit/Add mode
    val screenTitle = if (uiState.isEditMode) "Edit Subscription" else "Add New Subscription"
    val screenSubtitle = if (uiState.isEditMode) "Update the details of your subscription" else "Fill in the details to track a new subscription"
    val primaryButtonText = if (uiState.isEditMode) "Update Subscription" else "Add Subscription"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Back to Subscriptions", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        Text(screenTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(screenSubtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(24.dp))

                        FormLabel("Service Name", isRequired = true)
                        CustomTextField(
                            value = uiState.serviceName,
                            onValueChange = { viewModel.updateServiceName(it) },
                            placeholderText = "e.g. Netflix, AWS"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FormLabel("Category")
                        CustomDropdown(value = uiState.category, onValueChange = { viewModel.updateCategory(it) }, options = categories)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                FormLabel("Cost", isRequired = true)
                                CustomTextField(
                                    value = uiState.cost,
                                    onValueChange = { viewModel.updateCost(it) },
                                    placeholderText = "0.00",
                                    leadingIcon = { Text("$", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp) }
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                FormLabel("Currency")
                                CustomDropdown(value = uiState.currency, onValueChange = { viewModel.updateCurrency(it) }, options = currencies)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FormLabel("Billing Cycle")
                        ToggleGroup(
                            options = listOf("Monthly", "Yearly"),
                            selectedOption = uiState.billingCycle,
                            onOptionSelect = { viewModel.updateBillingCycle(it) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FormLabel("Payment Method")
                        CustomDropdown(value = uiState.paymentMethod, onValueChange = { viewModel.updatePaymentMethod(it) }, options = paymentMethods)

                        Spacer(modifier = Modifier.height(16.dp))

                        FormLabel("Start Date", isRequired = true)
                        Box(modifier = Modifier.clickable { showStartDatePicker = true }) {
                            CustomTextField(
                                value = uiState.startDate.format(dateFormatter),
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }
                            )
                        }
                        if (showStartDatePicker) {
                            DatePickerDialogComponent(
                                initialDate = uiState.startDate,
                                onDateSelected = { viewModel.updateStartDate(it) },
                                onDismiss = { showStartDatePicker = false }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FormLabel("Next Renewal Date", isRequired = true)
                        Box(modifier = Modifier.clickable { showRenewalDatePicker = true }) {
                            CustomTextField(
                                value = uiState.renewalDate.format(dateFormatter),
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }
                            )
                        }
                        if (showRenewalDatePicker) {
                            DatePickerDialogComponent(
                                initialDate = uiState.renewalDate,
                                onDateSelected = { viewModel.updateRenewalDate(it) },
                                onDismiss = { showRenewalDatePicker = false }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        FormLabel("Remind me (days before)")
                        RemindMeGroup(
                            options = listOf("1d", "3d", "7d", "14d", "30d"),
                            selectedOption = uiState.remindMe,
                            onOptionSelect = { viewModel.updateRemindMe(it) }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FormLabel("Status")
                        ToggleGroup(
                            options = listOf("Active", "Cancelled"),
                            selectedOption = uiState.status,
                            onOptionSelect = { viewModel.updateStatus(it) },
                            isStatusToggle = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FormLabel("Brand Color")
                        ColorPicker(selectedColor = Color(uiState.brandColor), onColorSelect = { viewModel.updateBrandColor(it.value.toLong()) })

                        Spacer(modifier = Modifier.height(24.dp))

                        FormLabel("Notes (optional)")
                        CustomTextField(
                            value = uiState.notes,
                            onValueChange = { viewModel.updateNotes(it) },
                            placeholderText = "Any additional notes...",
                            minLines = 3,
                            singleLine = false
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        SubscriptionPreviewCard(
                            name = uiState.serviceName,
                            category = uiState.category,
                            cost = uiState.cost,
                            currency = uiState.currency,
                            billingCycle = uiState.billingCycle,
                            color = Color(uiState.brandColor),
                            status = uiState.status
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = onNavigateBack,
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    viewModel.saveSubscription()
                                    onNavigateBack() // Go back after saving
                                },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(primaryButtonText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================
// 3. HELPER COMPONENTS
// ==========================================

@Composable
fun FormLabel(text: String, isRequired: Boolean = false) {
    Text(
        text = buildAnnotatedString {
            append(text)
            if (isRequired) {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) { append(" *") }
            }
        },
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    minLines: Int = 1,
    singleLine: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        readOnly = readOnly,
        enabled = !readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        minLines = minLines,
        singleLine = singleLine
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
            trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        ) {
            options.forEach { option ->
                val isSelected = option == value
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Spacer(modifier = Modifier.width(26.dp))
                            }
                            Text(
                                text = option,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(selected)
                }
                onDismiss()
            }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                headlineContentColor = MaterialTheme.colorScheme.onSurface,
                weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayContentColor = MaterialTheme.colorScheme.primary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun ToggleGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit,
    isStatusToggle: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            val containerColor = when {
                !isSelected -> MaterialTheme.colorScheme.surfaceVariant
                isStatusToggle && option == "Cancelled" -> dangerBg
                isStatusToggle && option == "Active" -> ColorSuccess.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.primaryContainer
            }

            val contentColor = when {
                !isSelected -> MaterialTheme.colorScheme.onSurfaceVariant
                isStatusToggle && option == "Cancelled" -> MaterialTheme.colorScheme.error
                isStatusToggle && option == "Active" -> ColorSuccess
                else -> MaterialTheme.colorScheme.primary
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerColor)
                    .border(1.dp, if (isSelected) containerColor else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable { onOptionSelect(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = option, color = contentColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RemindMeGroup(options: List<String>, selectedOption: String, onOptionSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable { onOptionSelect(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(selectedColor: Color, onColorSelect: (Color) -> Unit) {
    // These remain hardcoded hex values because they represent actual brand/logo colors,
    // not UI theme colors. They should look identical in light and dark mode.
    val brandColors = listOf(
        Color(0xFF7986CB), Color(0xFF9575CD), Color(0xFFF06292), Color(0xFFE53935),
        Color(0xFFFFB74D), Color(0xFF81C784), Color(0xFF4DD0E1), Color(0xFF64B5F6),
        Color(0xFFFF8A65), Color(0xFF4CAF50), Color(0xFFD32F2F), Color(0xFFFFA000),
        Color(0xFF512DA8), Color(0xFF448AFF), Color(0xFF7E57C2)
    )

    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        brandColors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelect(color) }
                    .then(if (isSelected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier)
            )
        }
    }
}

@Composable
fun SubscriptionPreviewCard(
    name: String,
    category: String,
    cost: String,
    currency: String,
    billingCycle: String,
    color: Color,
    status: String
) {
    val displayCost = if (cost.isBlank()) "0" else cost
    val displayName = if (name.isBlank()) "Subscription name" else name
    val displayIcon = if (name.isBlank()) "?" else name.take(1).uppercase()
    val currencySymbol = if (currency == "USD") "$" else if (currency == "INR") "₹" else currency

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = displayIcon, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = displayName, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$category · $currencySymbol$displayCost / $billingCycle", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }

            val statusBgColor = if (status == "Cancelled") dangerBg else ColorSuccess.copy(alpha = 0.15f)
            val statusTextColor = if (status == "Cancelled") MaterialTheme.colorScheme.error else ColorSuccess

            Surface(color = statusBgColor, shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = status,
                    color = statusTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Add/Edit Subscription Screen",
    showBackground = true,
    backgroundColor = 0xFF080812, // Using your DarkBackground from Color.kt
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddEditSubscriptionPreview() {
    MaterialTheme {
        AddEditSubscriptionScreen()
    }
}