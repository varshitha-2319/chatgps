package com.example.compose.jetchat.gps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.compose.jetchat.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GpsTrackerFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var locationTextView: TextView
    private lateinit var captureButton: Button
    private lateinit var galleryButton: Button
    private lateinit var backButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var imageUri: Uri? = null
    private lateinit var photoFile: File

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_IMAGE_CAPTURE = 102
        private const val REQUEST_IMAGE_PICK = 103
        private const val REQUEST_GALLERY_PERMISSION = 104
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gps_tracker, container, false)

        imageView = view.findViewById(R.id.capturedImage)
        locationTextView = view.findViewById(R.id.locationText)
        captureButton = view.findViewById(R.id.takePictureBtn)
        galleryButton = view.findViewById(R.id.selectFromGalleryBtn)
        backButton = view.findViewById(R.id.backToHomeBtn)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        captureButton.setOnClickListener {
            if (hasCameraAndLocationPermissions()) {
                takePicture()
            } else {
                requestCameraAndLocationPermissions()
            }
        }

        galleryButton.setOnClickListener {
            if (hasGalleryPermission()) {
                pickImageFromGallery()
            } else {
                requestGalleryPermission()
            }
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.nav_home) // Adjust if different destination
        }

        return view
    }

    private fun hasCameraAndLocationPermissions(): Boolean {
        val context = requireContext()
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraAndLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun hasGalleryPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        requestPermissions(arrayOf(permission), REQUEST_GALLERY_PERMISSION)
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            photoFile = createImageFile() ?: return
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.provider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    imageView.setImageURI(imageUri)
                    fetchLocation()
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        imageUri = selectedImageUri
                        imageView.setImageURI(imageUri)
                        fetchLocation()
                    } else {
                        locationTextView.text = "No image selected"
                    }
                }
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                val address = getAddressFromLocation(lat, lon)
                locationTextView.text = "Latitude: $lat\nLongitude: $lon\n$address"
            } else {
                locationTextView.text = "Location not available"
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) ?: return ""
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                buildString {
                    append("Address:\n")
                    appendLine(address.getAddressLine(0))
                }
            } else ""
        } catch (e: IOException) {
            "Address not available"
        }
    }
}
