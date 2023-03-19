package com.example.receiptwarehouse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.receiptwarehouse.databinding.FragmentCameraBinding
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.Line
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class CameraFragment : Fragment() {
    private var photoFile: File? = null
    private var mCurrentPhotoPath: String? = null
    private val captureImageRequest = 1
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button).setOnClickListener {
            Log.i("BUTTON", "CLICKED!")
        }

        captureImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun captureImage() {

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
            }
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    // Continue only if the File was successfully created

                    val photoURI = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.example.myapplication.fileproviderx",
                        photoFile!!
                    )
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, captureImageRequest)

                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    activity?.let { displayMessage(it.baseContext, ex.message.toString()) }
                }

            } else {
                activity?.let { displayMessage(it.baseContext, "Null") }
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == captureImageRequest && resultCode == Activity.RESULT_OK) {
            var myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            myBitmap = rotateBitmap(myBitmap, 90F)

            processBitmap(myBitmap)

            activity?.findViewById<ImageView>(R.id.imageView)?.setImageBitmap(myBitmap)
        } else {
            activity?.let { displayMessage(it.baseContext, "Request cancelled or something went wrong.") }
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener {visionText ->
                processTextBlock(visionText)
            }
            .addOnFailureListener {
                activity?.let { displayMessage(it.baseContext, it.toString()) }
            }
    }

    private fun processTextBlock(result: Text) {
        //val resultText = result.text
        val blocksList: RealmList<BlockItem> = realmListOf()


        for (block in result.textBlocks) {
            val linesList: RealmList<LineItem> = realmListOf()
            //val blockText = block.text
            //val blockCornerPoints = block.cornerPoints
            //val blockFrame = block.boundingBox
            for (line in block.lines) {
                val words: RealmList<String> = realmListOf()
                //val lineText = line.text
                //val lineCornerPoints = line.cornerPoints
                // val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                    words.add(elementText)
                }
                linesList.add(LineItem(words))
            }
            blocksList.add(BlockItem(linesList))
        }

        Database.write("buhaha", blocksList)
        Database.query()

    }
}