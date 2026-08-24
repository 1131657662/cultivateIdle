package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// High Density Design Theme Colors
val HdBackground = Color(0xFFFCF8F7)
val HdSurface = Color(0xFFFFFFFF)
val HdSurfaceVariant = Color(0xFFF3EDF7)
val HdSurfaceElevated = Color(0xFFEDE7F2)
val HdBorder = Color(0xFFE7E0EC)
val HdBorderStrong = Color(0xFFCAC4D0)

// High Density Brand & Accent Colors
val HdPurplePrimary = Color(0xFF6750A4)
val HdPurpleContainer = Color(0xFFE8DEF8)
val HdPurpleOnContainer = Color(0xFF21005D)
val HdPurpleLight = Color(0xFFD0BCFF)

val HdGoldPrimary = Color(0xFFB58300)
val HdGoldLight = Color(0xFFFFF3CD)
val HdGoldDark = Color(0xFF785900)

val HdJade = Color(0xFF2E7D32)
val HdJadeLight = Color(0xFFE8F5E9)
val HdJadeText = Color(0xFF1B5E20)

val HdAzure = Color(0xFF1565C0)
val HdAzureLight = Color(0xFFE3F2FD)

val HdCrimson = Color(0xFFB3261E)
val HdCrimsonLight = Color(0xFFF9DEDC)
val HdCrimsonText = Color(0xFF601410)

// High Density Text Palette
val HdTextPrimary = Color(0xFF1D1B1E)
val HdTextSecondary = Color(0xFF49454F)
val HdTextMuted = Color(0xFF79747E)
val HdTextWhite = Color(0xFFFFFFFF)

// High Density Terminal / Cultivation Log Dark Card
val HdConsoleBackground = Color(0xFF1D1B1E)
val HdConsoleHeader = Color(0xFFD0BCFF)
val HdConsoleContent = Color(0xFFE6E1E5)
val HdConsoleAlert = Color(0xFFFFB4AB)
val HdConsoleMuted = Color(0x66FFFFFF)

// Theme compatibility mapping (maps Dao* variables to High Density theme)
val DaoDarkBackground = HdBackground
val DaoDarkSurface = HdSurface
val DaoDarkSurfaceElevated = HdSurfaceVariant
val DaoDarkCard = HdSurfaceElevated
val DaoDarkBorder = HdBorder

val DaoGoldPrimary = HdPurplePrimary
val DaoGoldLight = HdPurpleOnContainer
val DaoGoldDark = HdPurplePrimary

val DaoJade = HdJade
val DaoJadeLight = HdJadeText

val DaoAzure = HdAzure
val DaoAzureLight = HdAzure

val DaoPurple = HdPurplePrimary
val DaoPurpleLight = HdPurpleContainer

val DaoCrimson = HdCrimson
val DaoCrimsonLight = HdCrimsonText

val TextPrimary = HdTextPrimary
val TextSecondary = HdTextSecondary
val TextMuted = HdTextMuted
