package com.example.firebaseimageandfileuploadanddownload

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    private lateinit var imageBtn: Button
    private lateinit var fileBtn: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var notificationImage: TextView
    private val storage: FirebaseStorage = Firebase.storage
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Storage reference
        storageReference = storage.reference.child("images") // Replace with your actual image path
        notificationImage=findViewById(R.id.notificationImage)

        imageBtn = findViewById(R.id.imageBtn)
        imageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)

        }

    }
    private fun uploadImage(uri: Uri) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading Image")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = false
        progressDialog.setCancelable(false)
        progressDialog.max = 100
        progressDialog.show()

        // Generate a unique file name for the uploaded image
        val fileName = "image_" + System.currentTimeMillis() + ".jpg"
        val imageRef = storageReference.child("images/$fileName")

        val uploadTask: UploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image upload successful
            progressDialog.dismiss()
            notificationImage.text = "Image uploaded successfully"
        }.addOnFailureListener {
            // Handle failure
            progressDialog.dismiss()
            notificationImage.text = "Image upload failed"
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            progressDialog.progress = progress
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data!!
            uploadImage(uri)
        }
    }
}