package com.eeos.rocatrun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade

// gif 불러오는 함수
@Composable
fun GifImage(modifier: Modifier = Modifier, gifUrl: String) {

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gifUrl)
            .crossfade(false)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = "GIF Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}