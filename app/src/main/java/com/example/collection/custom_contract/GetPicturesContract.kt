package com.example.collection.custom_contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract


class GetPicturesContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input)

    }

    override fun getSynchronousResult(
        context: Context,
        input: String
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }

}


