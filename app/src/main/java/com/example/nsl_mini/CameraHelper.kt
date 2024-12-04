package com.example.nsl_mini

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import android.util.Log

class CameraHelper(
    private val context: Context,
    private val textureView: TextureView,
    private val onBitmapReady: (Bitmap) -> Unit
) {
    private var cameraId: String = "0"
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var isFrontCamera: Boolean = false
    private var isCameraOpen: Boolean = false

    init {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                textureView.bitmap?.let { bitmap ->
                    onBitmapReady(bitmap)
                }
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                closeCamera()
                return true
            }
        }
    }

    @Synchronized
    private fun openCamera() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = if (isFrontCamera) getFrontCameraId(cameraManager) else getBackCameraId(cameraManager)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.d("CameraHelper", "Opening camera with ID: $cameraId")
                cameraManager.openCamera(cameraId, stateCallback, null)
                isCameraOpen = true
            } else {
                Log.e("CameraHelper", "Camera permission not granted")
            }
        } catch (e: CameraAccessException) {
            Log.e("CameraHelper", "CameraAccessException: ${e.message}")
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun closeCamera() {
        try {
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            isCameraOpen = false
            Log.d("CameraHelper", "Camera closed")
        } catch (e: Exception) {
            Log.e("CameraHelper", "Exception in closeCamera: ${e.message}")
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
            cameraDevice = null
            isCameraOpen = false
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
            isCameraOpen = false
        }
    }

    @Synchronized
    private fun startPreview() {
        if (!isCameraOpen) {
            Log.e("CameraHelper", "Camera is not open")
            return
        }

        try {
            val surfaceTexture = textureView.surfaceTexture
            surfaceTexture?.setDefaultBufferSize(textureView.width, textureView.height)
            val surface = Surface(surfaceTexture)

            val captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)

            cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    if (cameraDevice == null) {
                        Log.e("CameraHelper", "CameraDevice is null in onConfigured")
                        return
                    }
                    captureSession = session
                    captureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    try {
                        captureSession?.setRepeatingRequest(captureRequestBuilder?.build()!!, null, null)
                    } catch (e: CameraAccessException) {
                        Log.e("CameraHelper", "CameraAccessException in startPreview: ${e.message}")
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("CameraHelper", "CaptureSession configuration failed")
                }
            }, null)
        } catch (e: CameraAccessException) {
            Log.e("CameraHelper", "CameraAccessException in startPreview: ${e.message}")
            e.printStackTrace()
        }
    }

    @Synchronized
    fun switchCamera(onSwitchComplete: () -> Unit) {
        try {
            isFrontCamera = !isFrontCamera
            closeCamera()
            openCamera()
            onSwitchComplete()
        } catch (e: Exception) {
            Log.e("CameraHelper", "Exception in switchCamera: ${e.message}")
            e.printStackTrace()
            onSwitchComplete()  // Ensure the callback is called even on error
        }
    }

    private fun getFrontCameraId(cameraManager: CameraManager): String {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                return cameraId
            }
        }
        return "0" // default to back camera if front camera is not found
    }

    private fun getBackCameraId(cameraManager: CameraManager): String {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                return cameraId
            }
        }
        return "0" // default to back camera if back camera is not found
    }

    @Synchronized
    fun stopCamera() {
        closeCamera()
    }

    @Synchronized
    fun startCamera() {
        openCamera()
    }
}
