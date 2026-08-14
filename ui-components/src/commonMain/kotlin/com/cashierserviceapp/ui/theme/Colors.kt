package com.cashierserviceapp.ui.theme

import androidx.compose.ui.graphics.Color
import com.cashierserviceapp.ui.theme.Brand.blue100
import com.cashierserviceapp.ui.theme.Brand.blueTextDark
import com.cashierserviceapp.ui.theme.Brand.green100
import com.cashierserviceapp.ui.theme.Brand.greenTextDark
import com.cashierserviceapp.ui.theme.Brand.magenta100
import com.cashierserviceapp.ui.theme.Brand.magentaTextDark
import com.cashierserviceapp.ui.theme.Brand.orange
import com.cashierserviceapp.ui.theme.Brand.orangeTextDark
import com.cashierserviceapp.ui.theme.Brand.pink100
import com.cashierserviceapp.ui.theme.Brand.pinkTextDark
import com.cashierserviceapp.ui.theme.Brand.purple100
import com.cashierserviceapp.ui.theme.Brand.purpleTextDark
import com.cashierserviceapp.ui.theme.Brand.red100
import com.cashierserviceapp.ui.theme.Brand.redBackgroundDark
import com.cashierserviceapp.ui.theme.Brand.redTextDark
import com.cashierserviceapp.ui.theme.UI.black05
import com.cashierserviceapp.ui.theme.UI.black100
import com.cashierserviceapp.ui.theme.UI.black15
import com.cashierserviceapp.ui.theme.UI.black30
import com.cashierserviceapp.ui.theme.UI.black40
import com.cashierserviceapp.ui.theme.UI.black60
import com.cashierserviceapp.ui.theme.UI.black70
import com.cashierserviceapp.ui.theme.UI.black80
import com.cashierserviceapp.ui.theme.UI.grey100
import com.cashierserviceapp.ui.theme.UI.grey400
import com.cashierserviceapp.ui.theme.UI.grey500
import com.cashierserviceapp.ui.theme.UI.grey900
import com.cashierserviceapp.ui.theme.UI.white05
import com.cashierserviceapp.ui.theme.UI.white10
import com.cashierserviceapp.ui.theme.UI.white100
import com.cashierserviceapp.ui.theme.UI.white20
import com.cashierserviceapp.ui.theme.UI.white40
import com.cashierserviceapp.ui.theme.UI.white50
import com.cashierserviceapp.ui.theme.UI.white70
import com.cashierserviceapp.ui.theme.UI.white80

data class Colors(
    val isDark: Boolean,

    val mainBackground: Color,
    val mainBackgroundInverted: Color,
    val primaryBackground: Color,
    val tileBackground: Color,
    val tooltipBackground: Color,

    val cardBackgroundPast: Color,

    val strokeFull: Color,
    val strokeAccent: Color,
    val strokeInputFocus: Color,
    val strokeHalf: Color,
    val strokePale: Color,

    val accentText: Color,

    /**
     * Something has gone wrong, or is about to: validation messages, and the label on a
     * destructive action.
     *
     * A role, not a hue — reach for this rather than [redText] so the intent is legible at the call
     * site, and so danger can be retuned without touching the chip palette.
     */
    val dangerText: Color,

    /** Fill behind [primaryTextWhiteFixed] on a destructive button. */
    val dangerBackground: Color,

    val longText: Color,
    val noteText: Color,
    val placeholderText: Color,
    val primaryText: Color,
    val primaryTextInverted: Color,
    val primaryTextWhiteFixed: Color,
    val secondaryText: Color,

    val purpleText: Color,
    val magentaText: Color,
    val pinkText: Color,
    val orangeText: Color,
    val blueText: Color,
    val greenText: Color,
    val redText: Color,

    val toggleOn: Color,
    val toggleOff: Color,
)

val CashierServiceLightColors = Colors(
    isDark = false,

    mainBackground = white100,
    mainBackgroundInverted = black100,
    primaryBackground = magenta100,
    tileBackground = black05,
    tooltipBackground = grey900,

    cardBackgroundPast = black05,

    strokeFull = black100,
    strokeAccent = purple100,
    strokeInputFocus = black80,
    strokeHalf = black40,
    strokePale = black15,

    accentText = magenta100,
    dangerText = red100,
    dangerBackground = red100,
    longText = black70,
    noteText = black40,
    placeholderText = black30,
    primaryText = black100,
    primaryTextInverted = white100,
    primaryTextWhiteFixed = white100,
    secondaryText = black60,

    purpleText = purple100,
    magentaText = magenta100,
    pinkText = pink100,
    orangeText = orange,
    blueText = blue100,
    greenText = green100,
    redText = red100,

    toggleOff = grey400,
    toggleOn = purple100,
)

val CashierServiceDarkColors = Colors(
    isDark = true,

    mainBackground = black100,
    mainBackgroundInverted = white100,
    primaryBackground = magenta100,
    tileBackground = white10,
    tooltipBackground = grey100,

    cardBackgroundPast = white05,

    strokeFull = white100,
    strokeAccent = purpleTextDark,
    strokeInputFocus = white80,
    strokeHalf = white50,
    strokePale = white20,

    accentText = magentaTextDark,
    dangerText = redTextDark,
    dangerBackground = redBackgroundDark,
    longText = white70,
    noteText = white50,
    placeholderText = white40,
    primaryText = white100,
    primaryTextInverted = black100,
    primaryTextWhiteFixed = white100,
    secondaryText = white70,

    purpleText = purpleTextDark,
    magentaText = magentaTextDark,
    pinkText = pinkTextDark,
    orangeText = orangeTextDark,
    blueText = blueTextDark,
    greenText = greenTextDark,
    redText = redTextDark,

    toggleOff = grey500,
    toggleOn = purpleTextDark,
)