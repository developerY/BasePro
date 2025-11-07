package com.ylabz.basepro.applications.photodo.features.home.ui

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
// import kotlinx.coroutines.guava.await // <-- No longer needed
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) } // <-- We don't need the future anymore

    // This state will hold the SurfaceRequest from the Preview's SurfaceProvider
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var savedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Track rotation using OrientationEventListener
    val orientationEventListener = remember {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture?.targetRotation = rotation
            }
        }
    }

    // Accompanist permission state for CAMERA
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        // Initializing CameraX Preview and ImageCapture
        LaunchedEffect(lifecycleOwner) { // <-- No longer need cameraProviderFuture
            // --- THIS IS THE CHANGE ---
            // Use the built-in suspend function instead of the Guava future
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)
            // --------------------------

            // Build the Preview use case
            val preview = Preview.Builder().build()

            // Set the SurfaceProvider on the Preview use case, using the main executor.
            preview.setSurfaceProvider(ContextCompat.getMainExecutor(context)) { request ->
                surfaceRequest = request
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            imageCapture = ImageCapture.Builder().build()

            // Bind the use cases to the camera
            cameraProvider.unbindAll() // Ensure no other use cases are bound
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview, // The Preview use case
                imageCapture // The ImageCapture use case
            )
        }

        // Enable/Disable OrientationEventListener
        DisposableEffect(Unit) {
            orientationEventListener.enable()
            onDispose {
                orientationEventListener.disable()
            }
        }

        Column(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                // Use the new CameraXViewfinder Composable
                surfaceRequest?.let { request ->
                    CameraXViewfinder(
                        surfaceRequest = request,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Capture Button
            Button(
                onClick = {
                    imageCapture?.let { capture ->
                        val photoFile = createFile(context)
                        val outputOptions =
                            ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        capture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    savedImageUri = Uri.fromFile(photoFile)
                                    Log.d("CameraCapture", "Image saved in: $savedImageUri")
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraCapture", "Image capture failed", exception)
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Take photo")
            }

            // Display the captured image if it exists
            savedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                CapturedImagePreview(imageUri = uri)
            }
        }
    } else {
        // Show message if permission is not granted
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Camera permission is required to use the camera")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}

// Function to create a file in external storage
private fun createFile(context: Context): File {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: context.filesDir

    val outputDir = File(mediaDir, "CameraX").apply { mkdirs() }

    val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis()) + ".jpg"

    return File(outputDir, fileName)
}

@Composable
fun CapturedImagePreview(imageUri: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Load the image from the file
    LaunchedEffect(imageUri) {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    bitmap?.let {
        Image(
            painter = BitmapPainter(it.asImageBitmap()),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            contentScale = ContentScale.Crop
        )
    }
}