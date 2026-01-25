package com.ylabz.basepro.feature.camera.ui.components

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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ylabz.basepro.feature.camera.ui.CamEvent
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimpleCameraCaptureWithImagePreview(
    paddingValues: PaddingValues,
    onEvent: (CamEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Permission State (Accompanist 0.37.2)
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // 2. Camera Use Cases (Hoisted)
    // We remember these so they survive recompositions and can be accessed by the rotation listener
    val previewUseCase = remember { Preview.Builder().build() }
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }

    // 3. State for the CameraXViewfinder
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var savedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 4. Orientation Logic (Keeps photos upright)
    val orientationEventListener = remember {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                val rotation = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                // Update the capture use case dynamically
                imageCaptureUseCase.targetRotation = rotation
            }
        }
    }

    // Effect: Request Permission if not granted
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        // Effect: Bind Camera Lifecycle
        LaunchedEffect(lifecycleOwner) {
            // Await the camera provider (Suspends, doesn't block)
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)
            // Set up the preview use case
            previewUseCase.surfaceProvider = null

            // Connect the Preview UseCase to the Viewfinder surface
            previewUseCase.setSurfaceProvider(ContextCompat.getMainExecutor(context)) { request ->
                surfaceRequest = request
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    previewUseCase,
                    imageCaptureUseCase
                )
            } catch (e: Exception) {
                Log.e("CameraCapture", "Binding failed", e)
            }
        }

        // Effect: Manage Orientation Listener
        DisposableEffect(Unit) {
            orientationEventListener.enable()
            onDispose { orientationEventListener.disable() }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 5. The Camera Preview
            Box(modifier = Modifier.weight(1f)) {
                surfaceRequest?.let { request ->
                    // The new Compose-native viewfinder from androidx.camera:camera-compose
                    CameraXViewfinder(
                        surfaceRequest = request,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Capture Button
            Button(
                onClick = {
                    val photoFile = createFile(context)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCaptureUseCase.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                savedImageUri = Uri.fromFile(photoFile)
                                Log.d("CameraCapture", "Saved: $savedImageUri")
                                onEvent(
                                    CamEvent.AddItem(
                                        name = "Photo",
                                        description = "Captured Image",
                                        imgPath = savedImageUri.toString()
                                    )
                                )
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraCapture", "Error: ${exception.message}", exception)
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicText("Capture Photo")
            }

            // Preview Thumbnail
            savedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                CapturedImagePreview(imageUri = uri)
            }
        }
    } else {
        // Permission Denied State
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            BasicText("Camera permission required.")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                BasicText("Grant Permission")
            }
        }
    }
}

// --- Helpers ---

private fun createFile(context: Context): File {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    val outputDir = File(mediaDir, "CameraX").apply { mkdirs() }
    val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis()) + ".jpg"
    return File(outputDir, fileName)
}

@Composable
fun CapturedImagePreview(imageUri: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(imageUri) {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    bitmap?.let {
        Image(
            painter = BitmapPainter(it.asImageBitmap()),
            contentDescription = "Captured Image",
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