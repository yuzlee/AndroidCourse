/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.tensorflow_demo

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.Image.Plane
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Trace
import android.util.Size
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import java.nio.ByteBuffer

import demo.tensorflow_demo.env.ImageUtils
import demo.tensorflow_demo.env.Logger

// Explicit import needed for internal Google builds.
import demo.tensorflow_demo.R

abstract class CameraActivity : Activity(), OnImageAvailableListener, Camera.PreviewCallback {

    var isDebug = false
        private set

    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private val useCamera2API: Boolean = true
    protected var rgbFrameBitmap: Bitmap? = null
    private var rgbBytes: IntArray? = null
    protected var previewWidth = 0
    protected var previewHeight = 0
    protected var croppedBitmap: Bitmap? = null
    protected var lastProcessingTimeMs: Long = 0
    protected var cropCopyBitmap: Bitmap? = null
    protected var resultsView: ResultsView? = null
    protected var computing = false
    protected var postInferenceCallback: Runnable? = null
    protected var yuvBytes = arrayOfNulls<ByteArray>(3)
    protected var yRowStride: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        LOGGER.d("onCreate $this")
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_camera)

        if (hasPermission()) {
            setFragment()
        } else {
            requestPermission()
        }
    }

    /**
     * Callback for android.hardware.Camera API
     */
    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        if (computing) {
            return
        }
        computing = true
        yuvBytes[0] = bytes
        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                val previewSize = camera.parameters.previewSize
                previewHeight = previewSize.height
                previewWidth = previewSize.width
                rgbBytes = IntArray(previewWidth * previewHeight)
                onPreviewSizeChosen(Size(previewSize.width, previewSize.height), 90)
            }
            ImageUtils.convertYUV420SPToARGB8888(bytes, rgbBytes!!, previewWidth, previewHeight, false)
        } catch (e: Exception) {
            LOGGER.e(e, "Exception!")
            return
        }

        postInferenceCallback = Runnable { camera.addCallbackBuffer(bytes) }
        processImageRGBbytes(rgbBytes!!)
    }

    /**
     * Callback for Camera2 API
     */
    override fun onImageAvailable(reader: ImageReader) {
        var image: Image? = null
        //We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        rgbBytes = IntArray(previewWidth * previewHeight)
        try {
            image = reader.acquireLatestImage()

            if (image == null) {
                return
            }

            if (computing) {
                image.close()
                return
            }
            computing = true
            Trace.beginSection("imageAvailable")
            val planes = image.planes

            fillBytes(planes, yuvBytes)

            yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes!!)
            image.close()

        } catch (e: Exception) {
            if (image != null) {
                image.close()
            }
            LOGGER.e(e, "Exception!")
            Trace.endSection()
            return
        }
        processImageRGBbytes(rgbBytes!!)
        Trace.endSection()
    }

    @Synchronized public override fun onStart() {
        LOGGER.d("onStart " + this)
        super.onStart()
    }

    @Synchronized public override fun onResume() {
        LOGGER.d("onResume " + this)
        super.onResume()

        handlerThread = HandlerThread("inference")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    @Synchronized public override fun onPause() {
        LOGGER.d("onPause " + this)

        if (!isFinishing) {
            LOGGER.d("Requesting finish")
            finish()
        }

        handlerThread!!.quitSafely()
        try {
            handlerThread!!.join()
            handlerThread = null
            handler = null
        } catch (e: InterruptedException) {
            LOGGER.e(e, "Exception!")
        }

        super.onPause()
    }

    @Synchronized public override fun onStop() {
        LOGGER.d("onStop " + this)
        super.onStop()
    }

    @Synchronized public override fun onDestroy() {
        LOGGER.d("onDestroy " + this)
        super.onDestroy()
    }

    @Synchronized protected fun runInBackground(r: Runnable) {
        if (handler != null) {
            handler!!.post(r)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setFragment()
                } else {
                    requestPermission()
                }
            }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) || shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(this@CameraActivity,
                        "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show()
            }
            requestPermissions(arrayOf(PERMISSION_CAMERA, PERMISSION_STORAGE), PERMISSIONS_REQUEST)
        }
    }

    private fun chooseCamera(): String? {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue
                return cameraId
            }
        } catch (e: CameraAccessException) {
            LOGGER.e(e, "Not allowed to access camera")
        }

        return null
    }

    protected fun setFragment() {
        val cameraId = chooseCamera()

        val fragment: Fragment
        if (useCamera2API) {
            val camera2Fragment = CameraConnectionFragment.newInstance(
                    object: CameraConnectionFragment.ConnectionCallback {
                        override fun onPreviewSizeChosen(size: Size, rotation: Int) {
                            previewHeight = size.height
                            previewWidth = size.width
                            this@CameraActivity.onPreviewSizeChosen(size, rotation)
                        }
                    },
                    this,
                    layoutId,
                    desiredPreviewFrameSize)

            camera2Fragment.setCamera(cameraId!!)
            fragment = camera2Fragment
        } else {
            fragment = LegacyCameraConnectionFragment.newInstance(this, layoutId)
        }

        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

    protected fun fillBytes(planes: Array<Plane>, yuvBytes: Array<ByteArray?>) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity())
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i])
        }
    }

    fun requestRender() {
        val overlay = findViewById(R.id.debug_overlay) as OverlayView
        overlay.postInvalidate()
    }

    fun addCallback(callback: OverlayView.DrawCallback) {
        val overlay = findViewById(R.id.debug_overlay) as OverlayView
        overlay.addCallback(callback)
    }

    open fun onSetDebug(debug: Boolean) {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            isDebug = !isDebug
            requestRender()
            onSetDebug(isDebug)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    protected abstract fun processImageRGBbytes(rgbBytes: IntArray)
    protected abstract fun onPreviewSizeChosen(size: Size, rotation: Int)
    protected abstract val layoutId: Int
    protected abstract val desiredPreviewFrameSize: Size

    companion object {
        private val LOGGER = Logger()

        private val PERMISSIONS_REQUEST = 1

        private val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private val PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val SAVE_PREVIEW_BITMAP = false
    }
}
