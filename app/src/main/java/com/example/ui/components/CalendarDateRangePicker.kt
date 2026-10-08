package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandSunset
import com.example.ui.theme.BrandSunsetContainer
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.BrandTealContainer
import com.example.ui.theme.CardBorder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Calendar Date-Range Picker Dialog.
 * Calculates duration between start and end dates and strictly validates
 * that start date is before end date.
 */
@Composable
fun CalendarDateRangePickerDialog(
    initialStartDate: String,
    initialEndDate: String,
    onDismissRequest: () -> Unit,
    onDateRangeSelected: (startDate: String, endDate: String, durationDays: Int) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.US) }
    val monthYearFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.US) }

    // Parse initial dates or fallback
    val parsedStartCal = remember(initialStartDate) {
        Calendar.getInstance().apply {
            try {
                val parsed = dateFormatter.parse(initialStartDate)
                if (parsed != null) time = parsed
            } catch (e: Exception) {
                add(Calendar.DAY_OF_MONTH, 14)
            }
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val parsedEndCal = remember(initialEndDate) {
        Calendar.getInstance().apply {
            try {
                val parsed = dateFormatter.parse(initialEndDate)
                if (parsed != null) time = parsed
            } catch (e: Exception) {
                add(Calendar.DAY_OF_MONTH, 21)
            }
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    var selectedStartMillis by remember { mutableStateOf<Long?>(parsedStartCal.timeInMillis) }
    var selectedEndMillis by remember { mutableStateOf<Long?>(parsedEndCal.timeInMillis) }

    // Calendar month navigator state (year and month)
    var currentDisplayCal by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                if (selectedStartMillis != null) {
                    timeInMillis = selectedStartMillis!!
                }
                set(Calendar.DAY_OF_MONTH, 1)
            }
        )
    }

    // Validation & duration calculation
    val isRangeSelected = selectedStartMillis != null && selectedEndMillis != null
    val isValidRange = isRangeSelected && selectedStartMillis!! < selectedEndMillis!!
    val isSameDayInvalid = isRangeSelected && selectedStartMillis!! == selectedEndMillis!!
    val isEndBeforeStartInvalid = isRangeSelected && selectedStartMillis!! > selectedEndMillis!!

    val calculatedDurationDays = remember(selectedStartMillis, selectedEndMillis) {
        if (selectedStartMillis != null && selectedEndMillis != null && selectedEndMillis!! > selectedStartMillis!!) {
            val diffMillis = selectedEndMillis!! - selectedStartMillis!!
            val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
            days.coerceAtLeast(1)
        } else {
            0
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = BrandSunset,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Trip Travel Dates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Trip Duration & Validation Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isValidRange -> BrandTealContainer
                            isSameDayInvalid || isEndBeforeStartInvalid -> Color(0xFFFEE2E2)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when {
                                        isValidRange -> Icons.Default.CheckCircle
                                        isSameDayInvalid || isEndBeforeStartInvalid -> Icons.Default.Error
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isValidRange -> BrandTeal
                                        isSameDayInvalid || isEndBeforeStartInvalid -> Color(0xFFEF4444)
                                        else -> BrandSunset
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        isValidRange -> "Trip Duration: $calculatedDurationDays Days / ${calculatedDurationDays - 1} Nights"
                                        isSameDayInvalid -> "Error: Same departure & return date"
                                        isEndBeforeStartInvalid -> "Error: Return date is before departure"
                                        selectedStartMillis != null && selectedEndMillis == null -> "Departure selected • Pick return date"
                                        else -> "Select departure & return dates"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isValidRange -> Color(0xFF042F2E)
                                        isSameDayInvalid || isEndBeforeStartInvalid -> Color(0xFF991B1B)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Date Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Departure: ${selectedStartMillis?.let { dateFormatter.format(Date(it)) } ?: "Not set"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Return: ${selectedEndMillis?.let { dateFormatter.format(Date(it)) } ?: "Not set"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!isValidRange && isRangeSelected) {
                            Text(
                                text = "Validation rule: Start date must be strictly before end date.",
                                fontSize = 10.sp,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Month Navigator Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newCal = (currentDisplayCal.clone() as Calendar).apply {
                                add(Calendar.MONTH, -1)
                            }
                            currentDisplayCal = newCal
                        },
                        modifier = Modifier.testTag("calendar_prev_month")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        text = monthYearFormatter.format(currentDisplayCal.time),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = {
                            val newCal = (currentDisplayCal.clone() as Calendar).apply {
                                add(Calendar.MONTH, 1)
                            }
                            currentDisplayCal = newCal
                        },
                        modifier = Modifier.testTag("calendar_next_month")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }

                // Day of Week Headers (Su Mo Tu We Th Fr Sa)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                // Days Grid for Current Month
                val daysInMonth = remember(currentDisplayCal) {
                    val cal = (currentDisplayCal.clone() as Calendar).apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
                    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val daysList = mutableListOf<CalendarDay?>()

                    // Empty padding before 1st of month
                    for (i in 0 until firstDayOfWeek) {
                        daysList.add(null)
                    }
                    for (d in 1..maxDays) {
                        val dayCal = (cal.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, d)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        daysList.add(
                            CalendarDay(
                                dayNumber = d,
                                timeMillis = dayCal.timeInMillis
                            )
                        )
                    }
                    daysList
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(daysInMonth) { day ->
                        if (day == null) {
                            Box(modifier = Modifier.size(36.dp))
                        } else {
                            val isStart = selectedStartMillis == day.timeMillis
                            val isEnd = selectedEndMillis == day.timeMillis
                            val isInRange = selectedStartMillis != null &&
                                    selectedEndMillis != null &&
                                    day.timeMillis > selectedStartMillis!! &&
                                    day.timeMillis < selectedEndMillis!!

                            val backgroundColor = when {
                                isStart || isEnd -> BrandSunset
                                isInRange -> BrandSunsetContainer
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isStart || isEnd -> Color.White
                                isInRange -> Color(0xFF7C2D12)
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(
                                        when {
                                            isStart || isEnd -> CircleShape
                                            isInRange -> RoundedCornerShape(4.dp)
                                            else -> CircleShape
                                        }
                                    )
                                    .background(backgroundColor)
                                    .clickable {
                                        // Interactive selection logic
                                        if (selectedStartMillis == null || (selectedStartMillis != null && selectedEndMillis != null)) {
                                            selectedStartMillis = day.timeMillis
                                            selectedEndMillis = null
                                        } else {
                                            // 2nd tap: set end date
                                            selectedEndMillis = day.timeMillis
                                        }
                                    }
                                    .testTag("day_${day.dayNumber}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${day.dayNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isStart || isEnd || isInRange) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                // Quick Duration Presets Strip
                Text(
                    text = "Quick Presets (Auto-validates range):",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        PresetDuration("3 Days", 3),
                        PresetDuration("7 Days", 7),
                        PresetDuration("10 Days", 10),
                        PresetDuration("14 Days", 14)
                    ).forEach { preset ->
                        FilterChip(
                            selected = calculatedDurationDays == preset.days && isValidRange,
                            onClick = {
                                val baseMillis = selectedStartMillis ?: Calendar.getInstance().apply {
                                    add(Calendar.DAY_OF_MONTH, 7)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }.timeInMillis

                                selectedStartMillis = baseMillis
                                selectedEndMillis = baseMillis + (preset.days.toLong() * 24L * 60L * 60L * 1000L)
                            },
                            label = { Text(preset.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandSunset,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValidRange && selectedStartMillis != null && selectedEndMillis != null) {
                        val startStr = dateFormatter.format(Date(selectedStartMillis!!))
                        val endStr = dateFormatter.format(Date(selectedEndMillis!!))
                        onDateRangeSelected(startStr, endStr, calculatedDurationDays)
                        onDismissRequest()
                    }
                },
                enabled = isValidRange, // Strictly enforce that start is before end date!
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandSunset,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier.testTag("calendar_confirm_button")
            ) {
                Text(
                    text = if (isValidRange) "Apply Dates ($calculatedDurationDays Days)" else "Select Valid Range",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("calendar_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

data class CalendarDay(
    val dayNumber: Int,
    val timeMillis: Long
)

data class PresetDuration(
    val label: String,
    val days: Int
)
