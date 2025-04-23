package com.example.drivocare.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarViewModel(
    month: YearMonth,
    eventDates: List<LocalDate>,
    onMonthChange: (String) -> Unit
) {
    val firstDay = month.atDay(1).dayOfWeek.value % 7 // start from Sunday
    val daysInMonth = month.lengthOfMonth()

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { onMonthChange("prev") }) {
                Text("<", fontSize = 20.sp)
            }
            Text("${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}", fontSize = 20.sp)
            TextButton(onClick = { onMonthChange("next") }) {
                Text(">", fontSize = 20.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        val totalBoxes = firstDay + daysInMonth
        val weeks = (0 until totalBoxes).chunked(7)

        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { dayIndex ->
                    val day = dayIndex - firstDay + 1
                    if (dayIndex < firstDay || day > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).height(40.dp)) {}
                    } else {
                        val date = month.atDay(day)
                        val isEvent = eventDates.contains(date)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(if (isEvent) Color.Red else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$day", color = if (isEvent) Color.White else Color.Black)
                        }
                    }
                }
            }
        }
    }
}
