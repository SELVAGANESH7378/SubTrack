package com.selvaganesh7378.subtrack.ui.screens.calander

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val days = remember(currentMonth) {
        generateCalendarDays(currentMonth)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔥 HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = currentMonth.minusMonths(1)
            }) {
                Icon(Icons.Default.ArrowBack, null)
            }

            Text(
                text = "${currentMonth.month.name} ${currentMonth.year}",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )

            IconButton(onClick = {
                currentMonth = currentMonth.plusMonths(1)
            }) {
                Icon(Icons.Default.ArrowForward, null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val weekDays = listOf("SUN","MON","TUE","WED","THU","FRI","SAT")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(7)) {

            items(days.size) { index ->

                val date = days[index]

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                date == selectedDate -> Color(0xFF5B5BFF)
                                else -> Color.Transparent
                            }
                        )
                        .clickable {
                            if (date != null) selectedDate = date
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (date != null) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (date == selectedDate) Color.White else Color.Gray
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

    val startOffset = firstDay.dayOfWeek.value % 7 // Sunday start

    val days = mutableListOf<LocalDate?>()

    // empty cells before 1st
    repeat(startOffset) { days.add(null) }

    // actual days
    for (day in 1..daysInMonth) {
        days.add(month.atDay(day))
    }

    return days
}