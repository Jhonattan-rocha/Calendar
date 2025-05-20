package com.example.calendar.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@SuppressLint("ResourceType")
@Composable
fun CalendarView(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês anterior")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo Mẽs")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOfMonth = currentMonth.withDayOfMonth(1)
        val firstDayOfWeek = firstDayOfMonth.get(WeekFields.of(Locale.getDefault()).dayOfWeek())
        val daysInMonth = currentMonth.lengthOfMonth()

        val startOffset = (firstDayOfWeek - 1 + 7) % 7

        for (i in 0 until 6){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (j in 0 until 7){
                    val dayOfMonth = (i * 7 + j) - startOffset + 1
                    val date = if (dayOfMonth in 1..daysInMonth){
                        currentMonth.withDayOfMonth(dayOfMonth)
                    }else{
                        null
                    }

                    Box (
                        modifier = Modifier.size(40.dp).clickable(enabled = date != null) { date?.let { onDateSelected(it) } },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null){
                            val isSelected = date == selectedDate
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = if (isSelected) MaterialTheme.shapes.small else MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (date == LocalDate.now()) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}