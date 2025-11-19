package com.ylabz.basepro.feature.camera.ui.components

// import androidx.compose.ui.viewinterop.AndroidView // No longer needed
// import androidx.lifecycle.LifecycleOwner // Replaced with LocalLifecycleOwner
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ylabz.basepro.feature.camera.ui.CamEvent
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
// import androidx.compose.ui.tooling.preview.Preview as ComposePreview

// import java.util.concurrent.Executors // No longer needed

/**
 * CameraXViewfinder Composable: Instead of using AndroidView to embed a PreviewView,
 * you can directly use the CameraXViewfinder Composable in your Jetpack Compose UI.
 *
 * This Composable handles the complexities of displaying the camera feed, including rotation,
 * scaling, and managing the Surface lifecycle.
 *
 * A new artifact, camera-compose is released for the CameraX Viewfinder Compose Adapter which displays
 * a Preview stream from a CameraX SurfaceRequest from camera-core. (I8666e)
 *
 * Added a new composable, CameraXViewfinder, which acts as an idiomatic composable that adapts CameraX
 * SurfaceRequests for the composable Viewfinder. (I4770f)
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimpleCameraCaptureWithImagePreview(
    paddingValues: PaddingValues,
    onEvent: (CamEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

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
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        // Initializing CameraX Preview and ImageCapture
        LaunchedEffect(cameraProviderFuture, lifecycleOwner) {
            val cameraProvider = cameraProviderFuture.get()

            // Build the Preview use case
            val preview = Preview.Builder().build()

            // **This is the key change:**
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

        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                // Use the new CameraXViewfinder Composable
                // It will recompose when surfaceRequest changes.
                surfaceRequest?.let { request ->
                    CameraXViewfinder(
                        surfaceRequest = request,
                        modifier = Modifier
                            // Removed redundant padding
                            .fillMaxSize()
                    )
                }
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
                                onEvent(
                                    CamEvent.AddItem(
                                        name = "Photo",
                                        description = "Captured Image",
                                        imgPath = savedImageUri.toString() // Store the path
                                    )
                                )
                                //navTo("list_screen")
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

    // No longer need the DisposableEffect to shut down the executor
}

// Function to create a file in external storage (with your robust fallback)
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
        // **FIX:** Use a '.use' block to automatically close the InputStream
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

/*@ComposePreview(showBackground = true)
@Composable
fun SimpleComposablePreview() {
    SimpleCameraCaptureWithImagePreview(
        paddingValues = PaddingValues(0.dp),
        onEvent = {},
        navTo = {}
    )
}*/