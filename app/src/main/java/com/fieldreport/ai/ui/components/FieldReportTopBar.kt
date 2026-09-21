package com.fieldreport.ai.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.fieldreport.ai.ui.theme.Slate50
import com.fieldreport.ai.ui.theme.Slate900
import com.fieldreport.ai.ui.theme.Teal600

enum class TopBarThemeStyle {
    DARK,      // Slate900 Dark background with White status bar clock & icons
    BRAND,     // Deep Teal600 background with White status bar clock & icons
    LIGHT      // Crisp Slate50 Light background with Dark status bar clock & icons
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldReportTopBar(
    title: String,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    style: TopBarThemeStyle = TopBarThemeStyle.DARK,
    accentColor: Color = Teal600
) {
    val backgroundColor = when (style) {
        TopBarThemeStyle.DARK -> Slate900
        TopBarThemeStyle.BRAND -> Teal600
        TopBarThemeStyle.LIGHT -> Slate50
    }

    val contentColor = when (style) {
        TopBarThemeStyle.DARK, TopBarThemeStyle.BRAND -> Color.White
        TopBarThemeStyle.LIGHT -> Slate900
    }

    val isDarkBackground = style != TopBarThemeStyle.LIGHT

    // Configure System Status Bar Icons (Clock, Battery, Signal) Legibility
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // If background is dark, status bar icons MUST be white (isAppearanceLightStatusBars = false)
                // If background is light, status bar icons MUST be dark (isAppearanceLightStatusBars = true)
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkBackground
            }
        }
    }

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Surface(
        color = backgroundColor,
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Fill behind status bar clock & icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight)
                    .background(backgroundColor)
            )

            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            fontSize = 19.sp
                        )
                    )
                },
                navigationIcon = {
                    navigationIcon?.invoke()
                },
                actions = {
                    actions?.invoke(this)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor,
                    actionIconContentColor = contentColor
                )
            )

            // Accent brand line at bottom of topbar
            val lineBrush = if (style == TopBarThemeStyle.LIGHT) {
                Brush.horizontalGradient(
                    colors = listOf(
                        accentColor,
                        accentColor.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(
                        accentColor,
                        accentColor.copy(alpha = 0.6f),
                        Color.Transparent
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(lineBrush)
            )
        }
    }
}
