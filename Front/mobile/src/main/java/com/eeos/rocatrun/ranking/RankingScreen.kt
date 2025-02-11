package com.eeos.rocatrun.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ranking.api.Ranking
import com.eeos.rocatrun.ranking.api.RankingData
import com.eeos.rocatrun.ranking.api.RankingResponse
import com.eeos.rocatrun.ui.components.ModalCustomButton
import com.eeos.rocatrun.ui.theme.MyFontFamily


@Composable
fun RankingDialog(onDismiss: () -> Unit, rankingData: RankingResponse?) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(650.dp)
                .padding(0.dp),
            contentAlignment = Alignment.Center,
        ) {
            // 모달 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.home_bg_modal),
                contentDescription = "Modal Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 모달 Title
                Text(text = "랭킹", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(20.dp))

                // 현재 유저 정보
                rankingData?.data?.myRanking?.let { RankingItem(rankData = it, highlight = true) }
                Spacer(modifier = Modifier.height(8.dp))

                // 전체 유저 정보
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    rankingData?.data?.rankings?.forEach { item ->
                        RankingItem(rankData = item, highlight = false)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 닫기 버튼
                ModalCustomButton(
                    text = "닫기",
                    borderColor = Color(0xFF00FFCC),
                    enabled = true,
                    onClick = { onDismiss() },
                )
            }

        }
    }
}

// 화면 구성 일부
@Composable
fun RankingItem(rankData: Ranking, highlight: Boolean) {
    val backgroundColor = if (highlight) Color(0xDAFDFDFD) else Color.Transparent
    val textColor = if (highlight) Color.Black else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${rankData.rank}위", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.width(8.dp))

        val painter = rememberAsyncImagePainter(rankData.characterImage)
        Image(
            painter = painterResource(id = R.drawable.all_img_whitecat),
            contentDescription = "Profile Image",
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(text = rankData.nickname, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.weight(1f))

        Text(text = rankData.level.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFC107))
    }

    if (!highlight) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color.White)
        )
    }
}
