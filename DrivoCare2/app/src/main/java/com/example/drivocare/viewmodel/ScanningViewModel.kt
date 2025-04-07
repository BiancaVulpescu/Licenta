package com.example.drivocare.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.drivocare.ml.WarningLightModelMare
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


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

    fun capturePhoto(context: Context, onResult: (String) -> Unit) {
        val imageCapture = _imageCapture.value ?: return
        val photoFile = File(context.externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraX", "Photo saved: ${photoFile.absolutePath}")

                    // Step 1: Decode image
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

                    // Step 2: Convert Bitmap to ByteBuffer
                    val byteBuffer = convertBitmapToByteBuffer(resized)

                    // Step 3: Load model and run inference
                    val model = WarningLightModelMare.newInstance(context)

                    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                    inputFeature0.loadBuffer(byteBuffer)

                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                    val confidences = outputFeature0.floatArray
                    Log.d("Prediction", "Confidences: ${confidences.joinToString()}")

                    val maxIdx = confidences.indices.maxByOrNull { confidences[it] } ?: -1

                    val labels = listOf("abs", "air_suspension", "airbag_indicator", "battery", "brake", "catalytic_converter", "check_engine",
                        "engine_temperature", "front_fog_light", "fuel_filter", "glow_plug", "headlight_range", "high_beam_light", "hood_open", "low_beam_light",
                        "low_fuel", "master_warning", "oil_pressure", "parking_brake", "powertrain", "rear_fog_light", "seat_belt", "tire_pressure", "traction_control", "traction_control_off", "transmission_temperature")
                    val result = labels.getOrNull(maxIdx) ?: "Unknown"

                    model.close()

                    onResult(result)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                    onResult("Error")
                }
            }
        )
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        var pixelIndex = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val pixelValue = intValues[pixelIndex++]

                // Normalize RGB values to [0, 1]
                byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((pixelValue and 0xFF) / 255.0f))
            }
        }

        return byteBuffer
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}