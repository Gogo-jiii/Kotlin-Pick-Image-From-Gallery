package com.example.pickimagefromgallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pickimagefromgallery.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var permissionManager: PermissionManager? = null
    private val permissions = arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private val PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION = 100
    private val PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION = 200
    private val PICK_SINGLE_IMAGE_FROM_GALLERY_REQUEST_CODE = 300
    private val PICK_MULTIPLE_IMAGES_FROM_GALLERY_REQUEST_CODE = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        permissionManager = PermissionManager.getInstance(this)

        binding.btnPickImageFromGallery.setOnClickListener {
            if (!permissionManager!!.checkPermissions(permissions)) {
                permissionManager!!.askPermissions(
                    this@MainActivity, permissions,
                    PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION
                )
            } else {
                pickSingleImageFromGallery()
            }
        }

        binding.btnMultipleImagesFromGallery.setOnClickListener {
            if (!permissionManager!!.checkPermissions(permissions)) {
                permissionManager!!.askPermissions(
                    this@MainActivity, permissions,
                    PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION
                )
            } else {
                pickMultipleImagesFromGallery()
            }
        }
    }

    private fun pickMultipleImagesFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_MULTIPLE_IMAGES_FROM_GALLERY_REQUEST_CODE
        )
    }

    private fun pickSingleImageFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_SINGLE_IMAGE_FROM_GALLERY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == PICK_SINGLE_IMAGE_FROM_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            binding.imageView.setImageURI(uri)
        } else if (requestCode == PICK_MULTIPLE_IMAGES_FROM_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val uri: Uri = data.clipData!!.getItemAt(i).uri
                    val file = uri.path?.let { File(it) }
                    file?.name?.let { Log.d("TAG_URI: ", it) }
                    binding.imageView.setImageURI(data.clipData!!.getItemAt(0).uri)
                }
            } else {
                val uri: Uri? = data.data
                val file = uri?.path?.let { File(it) }
                file?.name?.let { Log.d("TAG_PATH: ", it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION && permissionManager!!.handlePermissionResult(
                this@MainActivity,
                grantResults
            )
        ) {
            pickSingleImageFromGallery()
        } else if (requestCode == PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION && permissionManager!!.handlePermissionResult(
                this@MainActivity,
                grantResults
            )
        ) {
            pickMultipleImagesFromGallery()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}