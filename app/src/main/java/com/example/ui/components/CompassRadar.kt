package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldNature
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

data class TravelMood(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val targetStates: List<String>,
    val angle: Float
)

val TRAVEL_MOODS = listOf(
    TravelMood("himalaya", "Alpine Snow", "🏔️", listOf("Himachal Pradesh", "Jammu & Kashmir (UT)", "Uttarakhand", "Sikkim"), 0f),
    TravelMood("coastal", "Coastal Shores", "🏖️", listOf("Goa", "Kerala", "Tamil Nadu", "Gujarat", "Odisha"), 90f),
    TravelMood("temples", "Sacred Shrines", "🛕", listOf("Tamil Nadu", "Uttar Pradesh", "Bihar", "Odisha", "Telangana"), 180f),
    TravelMood("northeast", "Misty Hills", "🌿", listOf("Meghalaya", "Arunachal Pradesh", "Assam", "Mizoram", "Nagaland"), 270f),
    TravelMood("desert", "Royal Forts", "🏰", listOf("Rajasthan", "Maharashtra", "Madhya Pradesh", "Punjab"), 45f),
    TravelMood("wildlife", "Tiger Forests", "🐅", listOf("Madhya Pradesh", "Assam", "Karnataka", "West Bengal", "Chhattisgarh"), 135f)
)

@Composable
fun CompassRadar(
    onMoodSelected: (TravelMood) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf(TRAVEL_MOODS[0]) }
    val animatedAngle by animateFloatAsState(
        targetValue = selectedMood.angle,
        animationSpec = spring(stiffness = 200f),
        label = "compass_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("discovery_compass_radar"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(NavyPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Radar",
                            tint = AmberAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "India Travel Radar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Interactive geographic compass discovery",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Rotating Mini Compass Dial
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF0F172A), CircleShape)
                        .border(1.5.dp, AmberAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Needle",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(animatedAngle)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mood Pills Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TRAVEL_MOODS) { mood ->
                    val isSelected = selectedMood.id == mood.id
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        label = "pill_bg"
                    )

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable {
                                selectedMood = mood
                                onMoodSelected(mood)
                            }
                            .testTag("mood_chip_${mood.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(text = mood.iconEmoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mood.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target States Spotlight Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spotlight States:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = selectedMood.targetStates.joinToString(" • "),
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
