package com.selvaganesh7378.subtrack.ui.screens.calander

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class SubscriptionEvent(
    val id: String,
    val name: String,
    val date: LocalDate,
    val price: Double,
    val color: Color,
    val letter: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }

    val dummyEvents = remember {
        listOf(
            SubscriptionEvent("1", "Spotify", YearMonth.now().atDay(26), 9.99, Color(0xFF4CAF50), "S"),
            SubscriptionEvent("2", "AWS", YearMonth.now().atDay(28), 45.00, Color(0xFFFF9800), "A")
        )
    }

    val days = remember(currentMonth) { generateCalendarDays(currentMonth) }
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val displayMonth = currentMonth.format(formatter)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp)
    ) {
        // --- 1. TOP HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Rounded.ChevronLeft, "Previous Month", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = displayMonth,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Rounded.ChevronRight, "Next Month", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Today Button
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable {
                    currentMonth = YearMonth.now()
                    selectedDate = today
                }
            ) {
                Text(
                    text = "Today",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. SUMMARY CARDS ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(title = "Renewals", value = "2 days", modifier = Modifier.weight(1f))
            SummaryCard(title = "Month Total", value = "$54.99", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. CALENDAR CARD (Using exact Profile specs) ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                val weekDays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDays.forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val weeks = days.chunked(7)
                weeks.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Because of the padding fix in generateCalendarDays,
                        // this will always have 7 items, meaning no weird stretching!
                        week.forEach { date ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (date != null) {
                                    val isToday = date == today
                                    val isSelected = date == selectedDate
                                    val eventsForDay = dummyEvents.filter { it.date == date }

                                    CalendarDayCell(
                                        date = date,
                                        isToday = isToday,
                                        isSelected = isSelected,
                                        events = eventsForDay,
                                        onClick = {
                                            selectedDate = if (selectedDate == date) null else date
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. DETAILS CARD ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(24.dp)
        ) {
            if (selectedDate == null) {
                Text(
                    text = "Click on a day to see renewals",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                val formatterDay = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                val eventsForSelectedDay = dummyEvents.filter { it.date == selectedDate }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = selectedDate!!.format(formatterDay),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (eventsForSelectedDay.isEmpty()) {
                            Text("No renewals on this day", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        } else {
                            Text("${eventsForSelectedDay.size} renewal(s)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                    }
                    IconButton(
                        onClick = { selectedDate = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 5. MONTH RENEWALS LIST ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "March Renewals",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                dummyEvents.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(event.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(event.letter, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("Mar ${event.date.dayOfMonth}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }

                        Text("$${event.price}", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayCell(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    events: List<SubscriptionEvent>,
    onClick: () -> Unit
) {
    val hasEvents = events.isNotEmpty()

    val cellModifier = Modifier
        .width(44.dp)
        .height(56.dp)
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
        .then(
            when {
                isSelected -> Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                hasEvents && !isToday -> Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                else -> Modifier.background(Color.Transparent)
            }
        )

    Box(
        modifier = cellModifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isToday) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (isSelected || hasEvents) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = if (hasEvents) 0.dp else 4.dp)
                )
            }

            if (hasEvents) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    events.forEach { event ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(event.color)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateCalendarDays(month: YearMonth): List<LocalDate?> {
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7
    val days = mutableListOf<LocalDate?>()

    repeat(startOffset) { days.add(null) }

    for (day in 1..daysInMonth) {
        days.add(month.atDay(day))
    }

    val endPadding = (7 - (days.size % 7)) % 7
    repeat(endPadding) { days.add(null) }

    return days
}