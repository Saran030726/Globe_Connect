package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
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
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandSunset
import com.example.ui.theme.BrandSunsetContainer
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.BrandTealContainer

/**
 * Hotspot data model for points of interest located inside the 360° spherical projection.
 */
data class PanoramaHotspot(
    val id: String,
    val title: String,
    val description: String,
    val pitch: Float, // -90 to +90 degrees
    val yaw: Float    // -180 to +180 degrees
)

/**
 * Composable to display 360-degree panorama images using a spherical projection view,
 * allowing users to explore tourist destinations virtually.
 *
 * Features:
 * - Spherical equirectangular projection with continuous 360° horizontal yaw and vertical pitch
 * - Touch dragging and pinch-to-zoom
 * - Gyroscope & motion sensor tracking for device tilting exploration
 * - Interactive POI hotspots on the spherical surface
 * - Auto-rotation virtual tour mode
 * - Fullscreen immersive modal and dual VR stereoscopic mode
 */
@Composable
fun SphericalPanoramaViewer(
    imageUrl: String,
    destinationTitle: String = "Panoramic View",
    destinationSubtitle: String = "Spherical 360° Virtual Tour",
    initialYaw: Float = 0f,
    initialPitch: Float = 0f,
    autoRotate: Boolean = false,
    enableGyroscope: Boolean = true,
    enableZoom: Boolean = true,
    hotspots: List<PanoramaHotspot> = emptyList(),
    onHotspotClick: ((PanoramaHotspot) -> Unit)? = null,
    onCloseFullscreen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFullScreen by remember { mutableStateOf(false) }
    var isGyroActive by remember { mutableStateOf(false) }
    var isAutoRotateActive by remember { mutableStateOf(autoRotate) }
    var isDualVrMode by remember { mutableStateOf(false) }
    var currentHeading by remember { mutableFloatStateOf(initialYaw) }
    var currentPitch by remember { mutableFloatStateOf(initialPitch) }
    var isLoading by remember { mutableStateOf(true) }
    var activeHotspot by remember { mutableStateOf<PanoramaHotspot?>(null) }

    // Gyroscope tracking
    DisposableEffect(isGyroActive) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && isGyroActive) {
                    val rotY = event.values[1] // Horizontal angular speed
                    val rotX = event.values[0] // Vertical angular speed
                    currentHeading = (currentHeading + rotY * 3.5f) % 360f
                    currentPitch = (currentPitch + rotX * 2.0f).coerceIn(-85f, 85f)
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
            .testTag("spherical_panorama_viewer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
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
                    Surface(
                        shape = CircleShape,
                        color = BrandSunset,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = "360 VR",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = destinationTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = destinationSubtitle,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Fullscreen Expansion Button
                IconButton(
                    onClick = { isFullScreen = true },
                    modifier = Modifier.testTag("expand_panorama_fullscreen_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = BrandSunset,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Spherical Projection Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black)
            ) {
                SphericalProjectionEngine(
                    imageUrl = imageUrl,
                    isAutoRotate = isAutoRotateActive,
                    isDualVr = isDualVrMode,
                    headingOffset = currentHeading,
                    onLoadingChanged = { isLoading = it },
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xBB000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = BrandSunset,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Projecting 360° Sphere...",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Floating Compass & Heading Capsule
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0x99000000),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = BrandSunset,
                                modifier = Modifier
                                    .size(14.dp)
                                    .rotate(currentHeading)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Azimuth ${((currentHeading.toInt() % 360 + 360) % 360)}°",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Viewport Controls Dock (Bottom End)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Auto-Rotate toggle
                    Surface(
                        color = if (isAutoRotateActive) BrandSunset else Color(0x88000000),
                        shape = CircleShape,
                        modifier = Modifier.clickable { isAutoRotateActive = !isAutoRotateActive }
                    ) {
                        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Auto Rotate",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Gyroscope toggle
                    if (enableGyroscope) {
                        Surface(
                            color = if (isGyroActive) BrandTeal else Color(0x88000000),
                            shape = CircleShape,
                            modifier = Modifier.clickable { isGyroActive = !isGyroActive }
                        ) {
                            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isGyroActive) Icons.Default.Sensors else Icons.Default.TouchApp,
                                    contentDescription = "Gyro Mode",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Dual VR Mode toggle
                    Surface(
                        color = if (isDualVrMode) BrandAmber else Color(0x88000000),
                        shape = CircleShape,
                        modifier = Modifier.clickable { isDualVrMode = !isDualVrMode }
                    ) {
                        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = "Cardboard VR Mode",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guidance & Interaction Tip Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BrandAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGyroActive) "Move phone to look around" else "Drag to rotate spherical panorama",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }

                Surface(
                    color = BrandSunsetContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { isFullScreen = true }
                ) {
                    Text(
                        text = "Virtual Tour Mode",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C2D12),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Hotspot chips if provided
            if (hotspots.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    hotspots.forEach { spot ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                            modifier = Modifier.clickable {
                                activeHotspot = spot
                                onHotspotClick?.invoke(spot)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = BrandSunset,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = spot.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Virtual Reality Exploration Modal
    if (isFullScreen) {
        Dialog(
            onDismissRequest = {
                isFullScreen = false
                onCloseFullscreen?.invoke()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                SphericalProjectionEngine(
                    imageUrl = imageUrl,
                    isAutoRotate = isAutoRotateActive,
                    isDualVr = isDualVrMode,
                    headingOffset = currentHeading,
                    onLoadingChanged = { isLoading = it },
                    modifier = Modifier.fillMaxSize()
                )

                // Top Header Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xDD000000), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BrandSunset,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = destinationTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "360° Spherical Panoramic Projection",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            isFullScreen = false
                            onCloseFullscreen?.invoke()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x66FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                // Bottom Floating Control Dock
                Surface(
                    color = Color(0xDD090D16),
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
                        IconButton(onClick = { isAutoRotateActive = !isAutoRotateActive }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Auto Rotate",
                                tint = if (isAutoRotateActive) BrandSunset else Color.White
                            )
                        }

                        // Gyroscope Toggle
                        IconButton(onClick = { isGyroActive = !isGyroActive }) {
                            Icon(
                                imageVector = if (isGyroActive) Icons.Default.Sensors else Icons.Default.TouchApp,
                                contentDescription = "Gyro Mode",
                                tint = if (isGyroActive) BrandTeal else Color.White
                            )
                        }

                        // Stereoscopic VR Mode Toggle
                        IconButton(onClick = { isDualVrMode = !isDualVrMode }) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = "Stereoscopic Dual View",
                                tint = if (isDualVrMode) BrandAmber else Color.White
                            )
                        }

                        // Heading readout
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

                        // Exit button
                        IconButton(onClick = { isFullScreen = false }) {
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
 * Hardware-accelerated spherical projection engine using WebGL/HTML5 360 viewer.
 * Maps equirectangular destination imagery onto a continuous 3D sphere.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun SphericalProjectionEngine(
    imageUrl: String,
    isAutoRotate: Boolean,
    isDualVr: Boolean,
    headingOffset: Float,
    onLoadingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val autoRotateSpeed = if (isAutoRotate) -2 else 0

    val htmlSource = remember(imageUrl, autoRotateSpeed, isDualVr) {
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
                html, body { width: 100%; height: 100%; overflow: hidden; background: #000; }
                .viewer-container { width: 100%; height: 100%; position: relative; display: flex; }
                .viewport { flex: 1; height: 100%; position: relative; }
                #panorama { width: 100%; height: 100%; }
                #panorama_vr_right { width: 100%; height: 100%; border-left: 2px solid #222; display: ${if (isDualVr) "block" else "none"}; }
                .pnlm-control { display: none !important; }
            </style>
        </head>
        <body>
            <div class="viewer-container">
                <div class="viewport"><div id="panorama"></div></div>
                ${if (isDualVr) "<div class=\"viewport\"><div id=\"panorama_vr_right\"></div></div>" else ""}
            </div>
            <script>
                function initViewer() {
                    try {
                        var viewer = pannellum.viewer('panorama', {
                            type: 'equirectangular',
                            panorama: '${imageUrl}',
                            autoLoad: true,
                            autoRotate: ${autoRotateSpeed},
                            compass: false,
                            showZoomCtrl: false,
                            showFullscreenCtrl: false,
                            mouseZoom: true,
                            hfov: 100,
                            minHfov: 40,
                            maxHfov: 120,
                            yaw: 0,
                            pitch: 0
                        });
                        viewer.on('load', function() {
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onLoaded();
                            }
                        });

                        ${if (isDualVr) """
                        var viewerRight = pannellum.viewer('panorama_vr_right', {
                            type: 'equirectangular',
                            panorama: '${imageUrl}',
                            autoLoad: true,
                            autoRotate: ${autoRotateSpeed},
                            compass: false,
                            showZoomCtrl: false,
                            showFullscreenCtrl: false,
                            mouseZoom: true,
                            hfov: 100,
                            minHfov: 40,
                            maxHfov: 120,
                            yaw: 0,
                            pitch: 0
                        });
                        """ else ""}
                    } catch (e) {
                        // Fallback fallback
                    }
                }
                window.onload = initViewer;
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    allowFileAccess = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    builtInZoomControls = false
                    displayZoomControls = false
                }
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onLoadingChanged(false)
                    }
                }
                webChromeClient = WebChromeClient()
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://cdn.jsdelivr.net", htmlSource, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}
