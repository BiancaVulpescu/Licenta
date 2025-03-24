package com.example.drivocare.viewmodel

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ScanningViewModel : ViewModel() {
    private val _imageCapture = MutableStateFlow<ImageCapture?>(null)
    val imageCapture: StateFlow<ImageCapture?> = _imageCapture

    val hasCameraPermission = MutableStateFlow(false)
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun updatePermissionState(isGranted: Boolean) {
        hasCameraPermission.value = isGranted
    }
    fun startCamera(context: Context, previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (!hasCameraPermission.value) {
            Log.e("CameraX", "Camera permission not granted.")
            return
        }
        val cameraProviderFuture= ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider=cameraProviderFuture.get()
            val preview= Preview.Builder().build().also{
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCaptureInstance = ImageCapture.Builder().build()
            _imageCapture.value = imageCaptureInstance
            val cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCaptureInstance
                )
            }catch (e:Exception){
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto(context: Context) {
        val imageCapture = _imageCapture.value
        if (imageCapture == null) {
            Log.e("CameraX", "ImageCapture is not initialized yet")
            return
        }
        val photoFile = File(context.externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object :
            ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("CameraX", "Photo saved: ${photoFile.absolutePath}")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
            }
        })
    }
    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}