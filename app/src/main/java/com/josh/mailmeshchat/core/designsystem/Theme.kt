package com.josh.mailmeshchat.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = MailMeshChatBlue,
    primaryContainer = MailMeshChatBlue30,
    onPrimary = MailMeshChatBlack,
    background = MailMeshChatBlack,
    onBackground = MailMeshChatWhite,
    surface = MailMeshChatDarkGray,
    onSurface = MailMeshChatWhite,
    onSurfaceVariant = MailMeshChatGray,
    secondary = MailMeshChatWhite,
    tertiary = MailMeshChatWhite,
    error = MailMeshChatDarkRed,
)

/*
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
*/


/**
 *  Dynamic color is available on Android 12+
 */
@Composable
fun MailMeshChatTheme(
    // darkTheme: Boolean = isSystemInDarkTheme(),
    // dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /*
    val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }
    */

    MaterialTheme(
      colorScheme = DarkColorScheme,
      typography = Typography,
      content = content
    )
}