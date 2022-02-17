package com.example.collection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.collection.custom_contract.GetPicturesContract
import com.example.collection.databinding.EditActivityBinding
import com.example.collection.db.MyDbManager
import com.example.collection.db.MyItemConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class EditActivity : AppCompatActivity() {

    private lateinit var bindingClass: EditActivityBinding
    private var latestTmpUri = "empty"
    private var id = 0
    private var isEditState = false
    private val myDbManager = MyDbManager(this)


    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private val selectImageFromGalleryResult =
        registerForActivityResult(GetPicturesContract()) { uri: Uri? ->
            uri?.let {
                bindingClass.imMyImage.setImageURI(uri)
                latestTmpUri = uri.toString()
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = EditActivityBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        getMyIntent()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickAddPhoto(view: View) {
        bindingClass.mainImageLayout.visibility = View.VISIBLE
        bindingClass.fbAddImage.visibility = View.GONE
    }

    fun onClickSave(view: View) {
        val myTitle = bindingClass.edTitle.text.toString()
        val myDisc = bindingClass.edDisc.text.toString()

        if (myTitle != "" && myDisc != "") {

            CoroutineScope(Dispatchers.Main).launch {

                if (isEditState) {
                    myDbManager.updateItem(myTitle, myDisc, latestTmpUri, id, getCurrentTIme())
                } else {
                    myDbManager.insertTODb(myTitle, myDisc, latestTmpUri, getCurrentTIme())
                }
                finish()
            }
        }
    }

    fun onClickDeletePhoto(view: View) {
        bindingClass.mainImageLayout.visibility = View.GONE
        bindingClass.fbAddImage.visibility = View.VISIBLE
        latestTmpUri = "empty"
    }

    fun onClickChooseImage(view: View) {
        selectImageFromGallery()
    }

    fun onEditEnable(view: View) {
        bindingClass.edTitle.isEnabled = true
        bindingClass.edDisc.isEnabled = true
        bindingClass.fbEdit.visibility = View.GONE
        bindingClass.fbAddImage.visibility = View.VISIBLE

        if (latestTmpUri == "empty") return
        bindingClass.imButtonEditImage.visibility = View.VISIBLE
        bindingClass.imButtonDeleteImage.visibility = View.VISIBLE
    }


    private fun getMyIntent() {

        bindingClass.fbEdit.visibility = View.GONE
        val i = intent

        if (i != null) {
            if (i.getStringExtra(MyItemConstants.I_TITLE_KEY) != null) {

                bindingClass.fbAddImage.visibility = View.GONE
                isEditState = true
                bindingClass.edTitle.isEnabled = false
                bindingClass.edDisc.isEnabled = false
                bindingClass.fbEdit.visibility = View.VISIBLE

                bindingClass.edTitle.setText(i.getStringExtra(MyItemConstants.I_TITLE_KEY))
                bindingClass.edDisc.setText(i.getStringExtra(MyItemConstants.I_DESC_KEY))
                id = i.getIntExtra(MyItemConstants.I_ID_KEY, 0)

                if (i.getStringExtra(MyItemConstants.I_URI_KEY) != "empty") {

                    bindingClass.mainImageLayout.visibility = View.VISIBLE
                    latestTmpUri = i.getStringExtra(MyItemConstants.I_URI_KEY)!!
                    bindingClass.imMyImage.setImageURI(
                        Uri.parse(latestTmpUri)
                    )
                    bindingClass.imButtonDeleteImage.visibility = View.GONE
                    bindingClass.imButtonEditImage.visibility = View.GONE
                }
            }
        }
    }

    private fun getCurrentTIme(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)

    }

}