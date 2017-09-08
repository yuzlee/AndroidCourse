package demo.tensorflow_demo

/*
 * Copyright 2014 The Android Open Source Project
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

import android.app.Fragment
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup

import java.io.IOException

import android.hardware.Camera
import android.hardware.Camera.CameraInfo

import demo.tensorflow_demo.env.Logger

// Explicit import needed for internal Google builds.
import demo.tensorflow_demo.R

class LegacyCameraConnectionFragment constructor(): Fragment() {

    private var imageListener: Camera.PreviewCallback? = null
    /**
     * The layout identifier to inflate for this Fragment.
     */
    private var layout: Int? = null

    private var camera: Camera? = null

    /**
     * [android.view.TextureView.SurfaceTextureListener] handles several lifecycle events on a
     * [TextureView].
     */
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
                texture: SurfaceTexture, width: Int, height: Int) {

            val index = cameraId
            camera = Camera.open(index)

            try {
                val parameters = camera!!.parameters
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

                camera!!.setDisplayOrientation(90)
                camera!!.parameters = parameters
                camera!!.setPreviewTexture(texture)
            } catch (exception: IOException) {
                camera!!.release()
            }

            camera!!.setPreviewCallbackWithBuffer(imageListener)
            val s = camera!!.parameters.previewSize
            val bufferSize = s.height * s.width * 3 / 2
            camera!!.addCallbackBuffer(ByteArray(bufferSize))

            textureView!!.setAspectRatio(s.height, s.width)

            camera!!.startPreview()
        }

        override fun onSurfaceTextureSizeChanged(
                texture: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

    /**
     * An [AutoFitTextureView] for camera preview.
     */
    private var textureView: AutoFitTextureView? = null

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var backgroundThread: HandlerThread? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout!!, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textureView = view.findViewById(R.id.texture) as AutoFitTextureView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).

        if (textureView!!.isAvailable) {
            camera!!.startPreview()
        } else {
            textureView!!.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onPause() {
        stopCamera()
        stopBackgroundThread()
        super.onPause()
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread!!.start()
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        backgroundThread!!.quitSafely()
        try {
            backgroundThread!!.join()
            backgroundThread = null
        } catch (e: InterruptedException) {
            LOGGER.e(e, "Exception!")
        }

    }

    protected fun stopCamera() {
        if (camera != null) {
            camera!!.stopPreview()
            camera!!.setPreviewCallback(null)
            camera!!.release()
            camera = null
        }
    }

    private // No camera found
    val cameraId: Int
        get() {
            val ci = CameraInfo()
            for (i in 0..Camera.getNumberOfCameras() - 1) {
                Camera.getCameraInfo(i, ci)
                if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
                    return i
            }
            return -1
        }

    companion object {
        private val LOGGER = Logger()

        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        fun newInstance(imageListener: Camera.PreviewCallback, layout: Int): LegacyCameraConnectionFragment {
            val frag = LegacyCameraConnectionFragment()
            frag.imageListener = imageListener
            frag.layout = layout
            return frag
        }
    }
}
