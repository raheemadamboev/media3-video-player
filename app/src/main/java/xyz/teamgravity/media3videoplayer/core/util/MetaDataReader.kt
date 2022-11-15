package xyz.teamgravity.media3videoplayer.core.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import xyz.teamgravity.media3videoplayer.data.model.MetaDataModel

class MetaDataReader(
    private val context: Context,
) {

    fun getMetaData(contentUri: Uri): MetaDataModel? {
        if (contentUri.scheme != "content") return null

        val name = context.contentResolver
            .query(
                contentUri,
                arrayOf(MediaStore.Video.VideoColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                val index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                if (index == -1) return null
                cursor.moveToFirst()
                cursor.getString(index)
            }

        return name?.let { MetaDataModel(name = Uri.parse(it).lastPathSegment ?: return null) }
    }
}