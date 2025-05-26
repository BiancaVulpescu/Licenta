package com.example.drivocare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
fun CalendarPage(
    month: YearMonth,
    eventDates: List<LocalDate>,
    warningLightTitles: List<String>,
    eventTitleMap: Map<LocalDate, String>,
    onMonthChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate
) {
    val firstDay = month.atDay(1).dayOfWeek.value % 7 // start from Sunday
    val daysInMonth = month.lengthOfMonth()

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { onMonthChange("prev") }) {
                Text("<", fontSize = 25.sp)
            }
            Text("${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}", fontSize = 28.sp)
            TextButton(onClick = { onMonthChange("next") }) {
                Text(">", fontSize = 25.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)
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
                        val title = eventTitleMap[date]
                        val isEvent = title != null
                        val isWarningLight = warningLightTitles.contains(title)
                        val isSelected = date == selectedDate

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    color= when {
                                        isSelected -> Color(0xFFB0BEC5)
                                        isEvent && isWarningLight -> Color(0xFF479195)
                                        isEvent ->  Color(0xFF9C141E)
                                        else -> Color.Transparent
                                    },
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$day",
                                color = when {
                                    isSelected || isEvent -> Color.White
                                    else -> Color.Black
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
