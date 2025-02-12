package com.eeos.rocatrun.closet

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.eeos.rocatrun.closet.api.ClosetViewModel

// 이미지 저장 함수 (폰에 저장됨)
fun saveImageToDownloads(
    context: Context,
    imageBitmap: ImageBitmap,
    viewModel: ClosetViewModel,
    auth: String?
) {
    val bitmap = imageBitmap.asAndroidBitmap()
    val fileName = "captured_image_${System.currentTimeMillis()}.png"

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    Log.d("closet", "$uri")

    if (uri != null) {
        resolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            val filePath = getRealPathFromURI(context, uri)
            Log.d("closet", "$filePath")

            // api 를 호출해서 서버로 전송
            if (filePath != null) {
                viewModel.sendCharacterImage(auth, filePath)
            }


            Toast.makeText(context, "Pictures 폴더에 저장됨: $fileName", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(context, "파일 저장 실패 (OutputStream null)", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "파일 저장 실패 (URI null)", Toast.LENGTH_SHORT).show()
    }
}

// URI에서 실제 파일 경로 얻기
fun getRealPathFromURI(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.moveToFirst()
    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    val filePath = columnIndex?.let { cursor.getString(it) }
    cursor?.close()
    return filePath
}