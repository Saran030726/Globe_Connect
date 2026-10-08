package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldNature
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

@Composable
fun BoardingPassCard(
    booking: BookingEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("boarding_pass_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Upper Boarding Pass Header (Navy Theme)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyPrimary)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(AmberAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AirplaneTicket,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GLOBE CONNECT EXPEDITION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Official Tourism Board Verified Pass",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Surface(
                        color = Color(0x33FFFFFF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = booking.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Route & Flight/Transit Banner
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "DESTINATION", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Text(
                            text = booking.stateName.uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(24.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "DATE", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Text(
                            text = booking.travelDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Details Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PassField("PASSENGERS", "${booking.travelersCount} Travelers")
                    PassField("TRANSIT MODE", booking.travelMode.take(18))
                    PassField("BOOKING REF", booking.bookingRef)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Included Highlights
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "TOUR CIRCUIT ITINERARY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = booking.touristSpotNames,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavyPrimary,
                            maxLines = 2
                        )

                        if (booking.hiddenSpotNames.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💎 INCLUDED HIDDEN GEMS: ${booking.hiddenSpotNames}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNature,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🏨 ACCOMMODATION: ${booking.hotelName}",
                            fontSize = 11.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }

            // Perforation Tear Line with Left/Right notches
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(24f, size.height / 2),
                        end = Offset(size.width - 24f, size.height / 2),
                        strokeWidth = 2f,
                        pathEffect = pathEffect
                    )
                }
            }

            // Lower Boarding Pass Stub with Simulated Barcode / QR Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Procedural Barcode
                Column {
                    Canvas(modifier = Modifier.width(160.dp).height(38.dp)) {
                        val barWidths = floatArrayOf(2f, 4f, 1f, 3f, 5f, 2f, 1f, 4f, 2f, 5f, 1f, 3f, 2f, 4f, 1f, 3f, 2f, 5f, 3f, 1f, 4f, 2f, 3f)
                        var currentX = 0f
                        for (w in barWidths) {
                            drawRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(currentX, 0f),
                                size = androidx.compose.ui.geometry.Size(w * 1.8f, size.height)
                            )
                            currentX += (w * 1.8f) + 3f
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "e-Ticket: ${booking.bookingRef}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "TOTAL PAID", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    Text(
                        text = "₹${booking.totalAmount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun PassField(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
    }
}
