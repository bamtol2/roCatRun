package com.eeos.rocatrun.shop

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.closet.CharacterWithItems
import com.eeos.rocatrun.closet.CustomTabRow
import com.eeos.rocatrun.closet.GradeInfoScreen
import com.eeos.rocatrun.closet.ItemCard
import com.eeos.rocatrun.closet.ItemInfoScreen
import com.eeos.rocatrun.closet.SaveCheckScreen
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.ui.components.StrokedText
import dev.shreyaspatil.capturable.capturable

@Composable
fun ShopScreen() {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.shop_bg_image),
            contentDescription = "shop background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // 홈 버튼
        Button(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 20.dp, y = 70.dp)
                .padding(10.dp),
            onClick = {
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
                      },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.stats_icon_home),
                contentDescription = "Home Icon",
                modifier = Modifier.size(50.dp)
            )
        }


        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            StrokedText(
                text = "coming soon..",
                color = Color.White,
                strokeWidth = 15f,
                strokeColor = Color(0xFFE81462),
                fontSize = 50,
            )
        }

    }
}