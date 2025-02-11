package com.eeos.rocatrun.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.ui.theme.MyFontFamily

@Composable
fun ModalCustomButton(
    text: String,
    borderColor: Color,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .border(2.dp, borderColor, RoundedCornerShape(15.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = MyFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else Color(0xFF5F5F5F)
            )
        )
    }
}
