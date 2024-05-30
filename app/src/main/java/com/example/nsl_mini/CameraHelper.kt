package com.example.nsl_mini

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat

class CameraHelper(
    private val context: Context,
    private val textureView: TextureView,
    private val frameListener: (Bitmap) -> Unit
) {
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundThread: HandlerThread

    fun startCamera() {
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun openCamera() {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Find the front-facing camera ID
            val cameraId = manager.cameraIdList.firstOrNull { id ->
                val characteristics = manager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
            } ?: manager.cameraIdList[0] // Fallback to the first camera if no front camera is found

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, handle this case appropriately in your app
                return
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e("CameraHelper", "Error accessing camera", e)
        } catch (e: SecurityException) {
            Log.e("CameraHelper", "Camera permission not granted", e)
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice.close()
            Log.e("CameraHelper", "Camera device error: $error")
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            texture?.setDefaultBufferSize(640, 480)
            val surface = Surface(texture)

            val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("CameraHelper", "Configuration change")
                }
            }, null)
        } catch (e: CameraAccessException) {
            Log.e("CameraHelper", "Error creating camera preview session", e)
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
            val bitmap = textureView.bitmap
            if (bitmap != null) {
                frameListener(bitmap)
            }
        }
    }

    fun stopCamera() {
        captureSession.close()
        cameraDevice.close()
        stopBackgroundThread()
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            Log.e("CameraHelper", "Error stopping background thread", e)
        }
    }
}
