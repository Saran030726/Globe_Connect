package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldNature
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

data class SoundscapeTrack(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val region: String,
    val gradientColors: List<Color>
)

val NATIVE_SOUNDSCAPES = listOf(
    SoundscapeTrack(
        id = "temple",
        title = "Sacred Bells & Chants",
        subtitle = "Varanasi Ghats & Madurai Morning",
        emoji = "🛕",
        region = "Uttar Pradesh & Tamil Nadu",
        gradientColors = listOf(Color(0xFF7C2D12), Color(0xFFC2410C))
    ),
    SoundscapeTrack(
        id = "himalaya",
        title = "Himalayan Ridge Breeze",
        subtitle = "Spiti Pines & Singing Bowls",
        emoji = "🏔️",
        region = "Himachal & Ladakh",
        gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF0284C7))
    ),
    SoundscapeTrack(
        id = "ocean",
        title = "Arabian Sea Waves",
        subtitle = "Goa Shorelines & Palm Sway",
        emoji = "🌊",
        region = "Goa & Kerala Coast",
        gradientColors = listOf(Color(0xFF065F46), Color(0xFF0D9488))
    ),
    SoundscapeTrack(
        id = "jungle",
        title = "Monsoon Rainforest",
        subtitle = "Western Ghats & Kaziranga Flora",
        emoji = "🌿",
        region = "Assam & Western Ghats",
        gradientColors = listOf(Color(0xFF14532D), Color(0xFF15803D))
    )
)

@Composable
fun AmbientSoundscapePlayer(
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var selectedTrack by remember { mutableStateOf(NATIVE_SOUNDSCAPES[0]) }

    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    val barHeight1 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar1"
    )
    val barHeight2 by infiniteTransition.animateFloat(
        initialValue = 18f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar2"
    )
    val barHeight3 by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 26f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar3"
    )
    val barHeight4 by infiniteTransition.animateFloat(
        initialValue = 24f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar4"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ambient_soundscape_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Audio Icon + Title + Headphones Badge
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
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Soundscape",
                            tint = AmberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Native Indian Soundscapes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Immersive Audio Atmosphere",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Surface(
                    color = if (isPlaying) EmeraldNature.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isPlaying) "PLAYING" else "3D AUDIO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlaying) EmeraldNature else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Soundscape Track Card with Animated Waves
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(selectedTrack.gradientColors))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = selectedTrack.emoji,
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedTrack.title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = selectedTrack.subtitle,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0)
                            )
                            Text(
                                text = "📍 ${selectedTrack.region}",
                                fontSize = 10.sp,
                                color = AmberAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Equalizer wave bars & Play/Pause button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPlaying) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .padding(end = 10.dp)
                            ) {
                                Box(modifier = Modifier.width(3.dp).height(barHeight1.dp).background(Color.White, CircleShape))
                                Box(modifier = Modifier.width(3.dp).height(barHeight2.dp).background(AmberAccent, CircleShape))
                                Box(modifier = Modifier.width(3.dp).height(barHeight3.dp).background(Color.White, CircleShape))
                                Box(modifier = Modifier.width(3.dp).height(barHeight4.dp).background(AmberAccent, CircleShape))
                            }
                        }

                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, CircleShape)
                                .testTag("soundscape_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = NavyDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Soundscape track choices selector row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NATIVE_SOUNDSCAPES) { track ->
                    val isSelected = selectedTrack.id == track.id
                    Surface(
                        color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable {
                                selectedTrack = track
                                isPlaying = true
                            }
                            .testTag("soundscape_track_${track.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = track.emoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = track.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
