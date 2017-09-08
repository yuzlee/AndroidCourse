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

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface

import android.media.ImageReader.OnImageAvailableListener
import android.os.SystemClock
import android.util.Size
import android.util.TypedValue
import android.view.Display
import java.util.Vector
import demo.tensorflow_demo.OverlayView.DrawCallback
import demo.tensorflow_demo.env.BorderedText
import demo.tensorflow_demo.env.ImageUtils
import demo.tensorflow_demo.env.Logger

// Explicit import needed for internal Google builds.
import demo.tensorflow_demo.R

class ClassifierActivity : CameraActivity(), OnImageAvailableListener {
    private var sensorOrientation: Int? = null
    private var classifier: Classifier? = null
    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null


    private var borderedText: BorderedText? = null


    override val layoutId: Int
        get() = R.layout.camera_connection_fragment

    override val desiredPreviewFrameSize: Size
        get() = DESIRED_PREVIEW_SIZE

    public override fun onPreviewSizeChosen(size: Size, rotation: Int) {
        val textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, resources.displayMetrics)
        borderedText = BorderedText(textSizePx)
        borderedText!!.setTypeface(Typeface.MONOSPACE)

        classifier = TensorFlowImageClassifier.create(
                assets,
                MODEL_FILE,
                LABEL_FILE,
                INPUT_SIZE,
                IMAGE_MEAN,
                IMAGE_STD,
                INPUT_NAME,
                OUTPUT_NAME)

        previewWidth = size.width
        previewHeight = size.height

        val display = windowManager.defaultDisplay
        val screenOrientation = display.rotation

        LOGGER.i("Sensor orientation: %d, Screen orientation: %d", rotation, screenOrientation)

        sensorOrientation = rotation + screenOrientation

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight)
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888)
        croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888)

        frameToCropTransform = ImageUtils.getTransformationMatrix(
                previewWidth, previewHeight,
                INPUT_SIZE, INPUT_SIZE,
                sensorOrientation!!, MAINTAIN_ASPECT)

        cropToFrameTransform = Matrix()
        frameToCropTransform!!.invert(cropToFrameTransform)

        yuvBytes = arrayOfNulls(3)

        addCallback(object: DrawCallback {
            override fun drawCallback(canvas: Canvas) {
                renderDebug(canvas)
            }
        })
    }

    override fun processImageRGBbytes(rgbBytes: IntArray) {
        rgbFrameBitmap!!.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight)
        val canvas = Canvas(croppedBitmap!!)
        canvas.drawBitmap(rgbFrameBitmap!!, frameToCropTransform!!, null)

        // For examining the actual TF input.
        if (CameraActivity.SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap!!)
        }
        runInBackground(
                Runnable {
                    val startTime = SystemClock.uptimeMillis()
                    val results = classifier!!.recognizeImage(croppedBitmap!!)
                    lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
                    LOGGER.i("Detect: %s", results)
                    cropCopyBitmap = Bitmap.createBitmap(croppedBitmap)
                    if (resultsView == null) {
                        resultsView = findViewById(R.id.results) as ResultsView
                    }
                    resultsView!!.setResults(results)
                    requestRender()
                    computing = false
                    if (postInferenceCallback != null) {
                        postInferenceCallback!!.run()
                    }
                })
    }

    override fun onSetDebug(debug: Boolean) {
        classifier!!.enableStatLogging(debug)
    }

    private fun renderDebug(canvas: Canvas) {
        if (!isDebug) {
            return
        }
        val copy = cropCopyBitmap
        if (copy != null) {
            val matrix = Matrix()
            val scaleFactor = 2f
            matrix.postScale(scaleFactor, scaleFactor)
            matrix.postTranslate(
                    canvas.width - copy.width * scaleFactor,
                    canvas.height - copy.height * scaleFactor)
            canvas.drawBitmap(copy, matrix, Paint())

            val lines = Vector<String>()
            if (classifier != null) {
                val statString = classifier!!.statString
                val statLines = statString.split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                for (line in statLines) {
                    lines.add(line)
                }
            }

            lines.add("Frame: " + previewWidth + "x" + previewHeight)
            lines.add("Crop: " + copy.width + "x" + copy.height)
            lines.add("View: " + canvas.width + "x" + canvas.height)
            lines.add("Rotation: " + sensorOrientation!!)
            lines.add("Inference time: " + lastProcessingTimeMs + "ms")

            borderedText!!.drawLines(canvas, 10f, (canvas.height - 10).toFloat(), lines)
        }
    }

    companion object {
        private val LOGGER = Logger()

        // These are the settings for the original v1 Inception model. If you want to
        // use a model that's been produced from the TensorFlow for Poets codelab,
        // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
        // INPUT_NAME = "Mul", and OUTPUT_NAME = "final_result".
        // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
        // the ones you produced.
        //
        // To use v3 Inception model, strip the DecodeJpeg Op from your retrained
        // model first:
        //
        // python strip_unused.py \
        // --input_graph=<retrained-pb-file> \
        // --output_graph=<your-stripped-pb-file> \
        // --input_node_names="Mul" \
        // --output_node_names="final_result" \
        // --input_binary=true
        private val INPUT_SIZE = 224
        private val IMAGE_MEAN = 117
        private val IMAGE_STD = 1f
        private val INPUT_NAME = "input"
        private val OUTPUT_NAME = "output"


        private val MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb"
        private val LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt"


        private val MAINTAIN_ASPECT = true

        private val DESIRED_PREVIEW_SIZE = Size(640, 480)

        private val TEXT_SIZE_DIP = 10f
    }
}
