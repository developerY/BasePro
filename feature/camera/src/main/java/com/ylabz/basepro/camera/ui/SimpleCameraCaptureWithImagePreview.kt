package com.ylabz.basepro.camera.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.data.mapper.BaseProEntity
import java.nio.file.Files.createFile

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimpleCameraCaptureWithImagePreview(
    paddingValues: PaddingValues,
    viewModel: CamViewModel = hiltViewModel(),
    // onEvent: (CamEvent) -> Unit
    )
{
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var previewView by remember { mutableStateOf<androidx.camera.view.PreviewView?>(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var savedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Accompanist permission state for CAMERA
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        // Initializing CameraX Preview and ImageCapture
        LaunchedEffect(cameraProviderFuture) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val lifecycleOwner = context as LifecycleOwner

            imageCapture = ImageCapture.Builder().build()

            previewView?.let {
                preview.setSurfaceProvider(it.surfaceProvider)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            }
        }

        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    factory = { ctx ->
                        androidx.camera.view.PreviewView(ctx).also {
                            previewView = it
                        }
                    },
                    modifier = Modifier.padding(paddingValues).fillMaxSize()
                )
            }

            // Capture Button
            Button(onClick = {
                imageCapture?.let { capture ->
                    val photoFile = createFile(context)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                savedImageUri = Uri.fromFile(photoFile)
                                Log.d("CameraCapture", "Image saved in: $savedImageUri")
                                viewModel.onEvent(CamEvent.AddItem(
                                        name = "Photo",
                                        description = "Captured Image",
                                        imgPath = savedImageUri.toString() // Store the path
                                ))
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraCapture", "Image capture failed", exception)
                            }
                        }
                    )
                }
            }, modifier = Modifier.fillMaxWidth()) {
                BasicText("Capture Photo")
            }

            // Display the captured image if it exists
            savedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))

                // Show the saved image
                CapturedImagePreview(imageUri = uri)
            }
        }
    } else {
        // Show message if permission is not granted
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            BasicText("Camera permission is required to use the camera")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                BasicText("Grant Permission")
            }
        }
    }
}

@Composable
fun CapturedImagePreview(imageUri: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Load the image from the file
    LaunchedEffect(imageUri) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        bitmap = BitmapFactory.decodeStream(inputStream)
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

// Function to create a file in external storage
private fun createFile(context: Context): File {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
        File(it, "CameraX").apply { mkdirs() }
    }

    val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis()) + ".jpg"

    return File(mediaDir, fileName)
}


