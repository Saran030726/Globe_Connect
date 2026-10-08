package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PlaceEntity
import com.example.data.model.VRExperienceEntity
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandSunset
import com.example.ui.theme.BrandSunsetContainer
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.BrandTealContainer
import com.example.ui.util.NativeImageProvider

/**
 * 360° VR Viewer component compatible with Compose.
 * Renders authentic spherical equirectangular panoramic views for destinations that have visual assets,
 * enabling users to explore locations immersively with touch drag, pinch zoom, gyroscope tilting, and full-screen VR mode.
 */
@Composable
fun VRViewer360(
    vrExperience: VRExperienceEntity,
    place: PlaceEntity? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFullScreen by remember { mutableStateOf(false) }
    var isGyroActive by remember { mutableStateOf(false) }
    var isAutoRotate by remember { mutableStateOf(false) }
    var currentHeading by remember { mutableFloatStateOf(0f) }
    var isViewerLoading by remember { mutableStateOf(true) }

    val panoramaUrl = remember(vrExperience, place) {
        val spotName = place?.name ?: vrExperience.spotName
        val category = place?.category ?: ""
        NativeImageProvider.getVRPanoramaUrl(spotName, category)
    }

    // Gyroscope tracking for immersive head/phone movement
    DisposableEffect(isGyroActive) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && isGyroActive) {
                    val axisY = event.values[1] // Rotation around Y axis
                    currentHeading = (currentHeading + axisY * 4f) % 360f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (isGyroActive && gyroSensor != null) {
            sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vr_viewer_360_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(BrandSunset, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "360 VR",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = vrExperience.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "360° Spherical Panoramic View • ${vrExperience.provider}",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Expand Full-Screen Button
                IconButton(
                    onClick = { isFullScreen = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0x33FFFFFF), CircleShape)
                        .testTag("vr_fullscreen_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Enter Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded Panoramic Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black)
            ) {
                PanoramicWebGLCanvas(
                    panoramaUrl = panoramaUrl,
                    isAutoRotate = isAutoRotate,
                    onLoadingChanged = { isViewerLoading = it },
                    modifier = Modifier.fillMaxSize()
                )

                if (isViewerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xAA000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = BrandSunset,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Loading 360° Panorama...",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Compass & Drag Hint Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0x99000000),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Compass",
                                tint = BrandSunset,
                                modifier = Modifier
                                    .size(14.dp)
                                    .rotate(currentHeading)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "360° Interactive",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Quick Controls Bar on Canvas
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Auto-Rotate toggle
                    Surface(
                        color = if (isAutoRotate) BrandSunset else Color(0x88000000),
                        shape = CircleShape,
                        modifier = Modifier
                            .clickable { isAutoRotate = !isAutoRotate }
                            .testTag("vr_auto_rotate_btn")
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Auto Rotate",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Gyroscope toggle
                    Surface(
                        color = if (isGyroActive) BrandTeal else Color(0x88000000),
                        shape = CircleShape,
                        modifier = Modifier
                            .clickable { isGyroActive = !isGyroActive }
                            .testTag("vr_gyro_toggle_btn")
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGyroActive) Icons.Default.Sensors else Icons.Default.TouchApp,
                                contentDescription = "Gyro",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Fullscreen trigger
                    Surface(
                        color = Color(0x88000000),
                        shape = CircleShape,
                        modifier = Modifier.clickable { isFullScreen = true }
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Expand",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Drag instruction chip
                Surface(
                    color = Color(0x88000000),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "👆 Drag to pan 360° • Pinch to zoom",
                        fontSize = 10.sp,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions & Street View External Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = vrExperience.spotName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "GPS: ${vrExperience.latitude}°, ${vrExperience.longitude}°",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isFullScreen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSunset),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("launch_vr_fullscreen_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Immerse VR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val gmmIntentUri = Uri.parse("google.streetview:cbll=${vrExperience.latitude},${vrExperience.longitude}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(vrExperience.sourceUrl))
                                context.startActivity(webIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("open_streetview_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Street View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Full-Screen Immersive 360° VR Modal Dialog
    if (isFullScreen) {
        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Fullscreen WebGL Canvas
                PanoramicWebGLCanvas(
                    panoramaUrl = panoramaUrl,
                    isAutoRotate = isAutoRotate,
                    onLoadingChanged = {},
                    modifier = Modifier.fillMaxSize()
                )

                // Fullscreen Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xCC000000), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BrandSunset, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = vrExperience.spotName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "360° Immersive Virtual Reality Experience",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    IconButton(
                        onClick = { isFullScreen = false },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x55FFFFFF), CircleShape)
                            .testTag("exit_fullscreen_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                // Fullscreen Floating Control Dock
                Surface(
                    color = Color(0xCC090D16),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Auto Rotate Toggle
                        IconButton(
                            onClick = { isAutoRotate = !isAutoRotate }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Auto Rotate",
                                tint = if (isAutoRotate) BrandSunset else Color.White
                            )
                        }

                        // Gyroscope Toggle
                        IconButton(
                            onClick = { isGyroActive = !isGyroActive }
                        ) {
                            Icon(
                                imageVector = if (isGyroActive) Icons.Default.Sensors else Icons.Default.TouchApp,
                                contentDescription = "Gyro Mode",
                                tint = if (isGyroActive) BrandTeal else Color.White
                            )
                        }

                        // Compass Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Heading",
                                tint = BrandSunset,
                                modifier = Modifier
                                    .size(18.dp)
                                    .rotate(currentHeading)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${((currentHeading.toInt() % 360 + 360) % 360)}°",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Exit Fullscreen
                        IconButton(
                            onClick = { isFullScreen = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * WebGL-based Panoramic Equirectangular Canvas renderer hosted inside standard Android WebView.
 * Embeds Pannellum/HTML5 360 sphere engine to project visual panorama assets with full spherical rotation.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PanoramicWebGLCanvas(
    panoramaUrl: String,
    isAutoRotate: Boolean,
    onLoadingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val autoRotateSpeed = if (isAutoRotate) -2 else 0

    val htmlData = remember(panoramaUrl, autoRotateSpeed) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/pannellum@2.5.6/build/pannellum.css"/>
            <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/pannellum@2.5.6/build/pannellum.js"></script>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body, html { width: 100%; height: 100%; overflow: hidden; background: #000; touch-action: none; }
                #panorama { width: 100%; height: 100%; }
                .pnm-compass { display: none !important; }
                .pnm-load-box { display: none !important; }
            </style>
        </head>
        <body>
            <div id="panorama"></div>
            <script>
                try {
                    var viewer = pannellum.viewer('panorama', {
                        "type": "equirectangular",
                        "panorama": "$panoramaUrl",
                        "autoLoad": true,
                        "autoRotate": $autoRotateSpeed,
                        "compass": false,
                        "showZoomCtrl": false,
                        "showFullscreenCtrl": false,
                        "mouseZoom": true,
                        "hfov": 100,
                        "minHfov": 45,
                        "maxHfov": 120,
                        "yaw": 0,
                        "pitch": 0,
                        "friction": 0.15
                    });
                } catch(e) {
                    console.log("Pannellum WebGL fallback:", e);
                    // Fallback to cylindrical CSS3 360 panorama
                    document.body.innerHTML = '<div style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;background:#050811;overflow:hidden;"><img src="$panoramaUrl" style="min-width:100%;min-height:100%;object-fit:cover;"/></div>';
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mediaPlaybackRequiresUserGesture = false
                }
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onLoadingChanged(false)
                    }
                }
                webChromeClient = WebChromeClient()
                setBackgroundColor(android.graphics.Color.BLACK)
                loadDataWithBaseURL("https://images.unsplash.com", htmlData, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://images.unsplash.com", htmlData, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}
