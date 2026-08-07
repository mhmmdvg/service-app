package com.cashierserviceapp.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.ui.generated.resources.UiRes
import com.cashierserviceapp.ui.generated.resources.inter_18pt_bold
import com.cashierserviceapp.ui.generated.resources.inter_18pt_regular
import com.cashierserviceapp.ui.generated.resources.inter_18pt_semibold
import org.jetbrains.compose.resources.Font


class Typography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val text1: TextStyle,
    val text2: TextStyle,
)

internal val CashierServiceTypography: Typography
    @Composable
    get() = Typography(
        h1 = TextStyle(
            fontFamily = InterSans,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 32.sp,
        ),
        h2 = TextStyle(
            fontFamily = InterSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        h3 = TextStyle(
            fontFamily = InterSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        h4 = TextStyle(
            fontFamily = InterSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        ),
        text1 = TextStyle(
            fontFamily = InterSans,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        text2 = TextStyle(
            fontFamily = InterSans,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    )


internal val InterSans: FontFamily
    @Composable
    get() = FontFamily(
        Font(UiRes.font.inter_18pt_bold, FontWeight.Bold, FontStyle.Normal),
        Font(UiRes.font.inter_18pt_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(UiRes.font.inter_18pt_regular, FontWeight.Normal, FontStyle.Normal)
    )