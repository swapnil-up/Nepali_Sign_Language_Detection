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

class CameraHelper(
    private val context: Context,
    private val textureView: TextureView,
    private val onBitmapReady: (Bitmap) -> Unit
) {
    private var cameraId: String = "0"
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var isFrontCamera: Boolean = false

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

    private fun openCamera() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = if (isFrontCamera) getFrontCameraId(cameraManager) else getBackCameraId(cameraManager)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, null)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        captureSession?.close()
        captureSession = null
        cameraDevice?.close()
        cameraDevice = null
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    private fun startPreview() {
        try {
            val surfaceTexture = textureView.surfaceTexture
            surfaceTexture?.setDefaultBufferSize(textureView.width, textureView.height)
            val surface = Surface(surfaceTexture)

            val captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)

            cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    captureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    captureSession?.setRepeatingRequest(captureRequestBuilder?.build()!!, null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun switchCamera() {
        isFrontCamera = !isFrontCamera
        closeCamera()
        openCamera()
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

    fun stopCamera() {
        closeCamera()
    }

    fun startCamera() {
        openCamera()
    }
}
