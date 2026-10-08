package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.outlined.CardTravel
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandSunset
import com.example.ui.theme.BrandSunsetContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppScreen {
    HOME,
    STATES,
    STATE_DETAIL,
    PLACE_DETAIL,
    TRIP_BUILDER,
    VR_EXPLORE,
    GLOBAL_SEARCH,
    ACCOUNT_ADMIN,
    BOOKING_SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobeTopAppBar(
    currentScreen: AppScreen,
    title: String,
    subtitle: String? = null,
    isAdmin: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onSearchClick: () -> Unit,
    onAdminToggle: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            actions = {
                // Global Search Icon
                if (currentScreen != AppScreen.GLOBAL_SEARCH) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.testTag("top_bar_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Quick Role Switch Badge (Traveler / Admin)
                Surface(
                    color = if (isAdmin) BrandSunsetContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("role_switch_badge")
                ) {
                    IconButton(
                        onClick = onAdminToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = "Switch Role",
                            tint = if (isAdmin) BrandSunset else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun GlobeBottomNavigation(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    tripItemsCount: Int = 0,
    isAdmin: Boolean = false
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            val navColors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandSunset,
                selectedTextColor = BrandSunset,
                indicatorColor = BrandSunsetContainer,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )

            // Tab 1: Home
            NavigationBarItem(
                selected = currentScreen == AppScreen.HOME,
                onClick = { onTabSelected(AppScreen.HOME) },
                icon = {
                    Icon(
                        if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("Explore", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Medium) },
                colors = navColors,
                modifier = Modifier.testTag("nav_tab_home")
            )

            // Tab 2: States
            NavigationBarItem(
                selected = currentScreen == AppScreen.STATES,
                onClick = { onTabSelected(AppScreen.STATES) },
                icon = {
                    Icon(
                        if (currentScreen == AppScreen.STATES) Icons.Filled.Explore else Icons.Outlined.Explore,
                        contentDescription = "States"
                    )
                },
                label = { Text("States", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.STATES) FontWeight.Bold else FontWeight.Medium) },
                colors = navColors,
                modifier = Modifier.testTag("nav_tab_states")
            )

            // Tab 3: Trip Builder
            NavigationBarItem(
                selected = currentScreen == AppScreen.TRIP_BUILDER,
                onClick = { onTabSelected(AppScreen.TRIP_BUILDER) },
                icon = {
                    if (tripItemsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = BrandSunset,
                                    contentColor = Color.White
                                ) {
                                    Text("$tripItemsCount")
                                }
                            }
                        ) {
                            Icon(
                                if (currentScreen == AppScreen.TRIP_BUILDER) Icons.Filled.CardTravel else Icons.Outlined.CardTravel,
                                contentDescription = "Trip Builder"
                            )
                        }
                    } else {
                        Icon(
                            if (currentScreen == AppScreen.TRIP_BUILDER) Icons.Filled.CardTravel else Icons.Outlined.CardTravel,
                            contentDescription = "Trip Builder"
                        )
                    }
                },
                label = { Text("Plan Trip", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.TRIP_BUILDER) FontWeight.Bold else FontWeight.Medium) },
                colors = navColors,
                modifier = Modifier.testTag("nav_tab_trip_builder")
            )

            // Tab 4: 360° VR
            NavigationBarItem(
                selected = currentScreen == AppScreen.VR_EXPLORE,
                onClick = { onTabSelected(AppScreen.VR_EXPLORE) },
                icon = {
                    Icon(
                        if (currentScreen == AppScreen.VR_EXPLORE) Icons.Filled.ViewInAr else Icons.Outlined.ViewInAr,
                        contentDescription = "360° VR"
                    )
                },
                label = { Text("360° VR", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.VR_EXPLORE) FontWeight.Bold else FontWeight.Medium) },
                colors = navColors,
                modifier = Modifier.testTag("nav_tab_vr")
            )

            // Tab 5: Account / Admin
            NavigationBarItem(
                selected = currentScreen == AppScreen.ACCOUNT_ADMIN,
                onClick = { onTabSelected(AppScreen.ACCOUNT_ADMIN) },
                icon = {
                    Icon(
                        if (isAdmin) Icons.Default.AdminPanelSettings else if (currentScreen == AppScreen.ACCOUNT_ADMIN) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile"
                    )
                },
                label = { Text(if (isAdmin) "Admin" else "Profile", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.ACCOUNT_ADMIN) FontWeight.Bold else FontWeight.Medium) },
                colors = navColors,
                modifier = Modifier.testTag("nav_tab_account")
            )
        }
    }
}
