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
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import android.graphics.Typeface
import android.media.Image
import android.media.Image.Plane
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.SystemClock
import android.os.Trace
import android.util.Size
import android.util.TypedValue
import android.view.Display
import android.widget.Toast
import java.io.IOException
import java.util.LinkedList
import java.util.Vector
import demo.tensorflow_demo.OverlayView.DrawCallback
import demo.tensorflow_demo.env.BorderedText
import demo.tensorflow_demo.env.ImageUtils
import demo.tensorflow_demo.env.Logger
import demo.tensorflow_demo.tracking.MultiBoxTracker
import demo.tensorflow_demo.R

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
class DetectorActivity : CameraActivity(), OnImageAvailableListener {

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.  Optionally use legacy Multibox (trained using an older version of the API)
    // or YOLO.
    private enum class DetectorMode {
        TF_OD_API, MULTIBOX, YOLO
    }

    private var sensorOrientation: Int? = null

    private var detector: Classifier? = null

//    protected var previewWidth = 0
//    protected var previewHeight = 0
//    protected var yuvBytes: Array<ByteArray>? = null
    private var rgbBytes: IntArray? = null
//    protected var rgbFrameBitmap: Bitmap? = null
//    protected var croppedBitmap: Bitmap? = null
//
//    protected var computing = false

    private var timestamp: Long = 0

    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null

//    protected var cropCopyBitmap: Bitmap? = null

    private var tracker: MultiBoxTracker? = null

    private var luminance: ByteArray? = null

    private var borderedText: BorderedText? = null

//    protected var lastProcessingTimeMs: Long = 0

    public override fun onPreviewSizeChosen(size: Size, rotation: Int) {
        val textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, resources.displayMetrics)
        borderedText = BorderedText(textSizePx)
        borderedText!!.setTypeface(Typeface.MONOSPACE)

        tracker = MultiBoxTracker(this)

        var cropSize = TF_OD_API_INPUT_SIZE
        if (MODE == DetectorMode.YOLO) {
            detector = TensorFlowYoloDetector.create(
                    assets,
                    YOLO_MODEL_FILE,
                    YOLO_INPUT_SIZE,
                    YOLO_INPUT_NAME,
                    YOLO_OUTPUT_NAMES,
                    YOLO_BLOCK_SIZE)
            cropSize = YOLO_INPUT_SIZE
        } else if (MODE == DetectorMode.MULTIBOX) {
            detector = TensorFlowMultiBoxDetector.create(
                    assets,
                    MB_MODEL_FILE,
                    MB_LOCATION_FILE,
                    MB_IMAGE_MEAN,
                    MB_IMAGE_STD,
                    MB_INPUT_NAME,
                    MB_OUTPUT_LOCATIONS_NAME,
                    MB_OUTPUT_SCORES_NAME)
            cropSize = MB_INPUT_SIZE
        } else {
            try {
                detector = TensorFlowObjectDetectionAPIModel.create(
                        assets, TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE)
                cropSize = TF_OD_API_INPUT_SIZE
            } catch (e: IOException) {
                LOGGER.e("Exception initializing classifier!", e)
                val toast = Toast.makeText(
                        applicationContext, "Classifier could not be initialized", Toast.LENGTH_SHORT)
                toast.show()
                finish()
            }

        }

        previewWidth = size.width
        previewHeight = size.height

        val display = windowManager.defaultDisplay
        val screenOrientation = display.rotation

        LOGGER.i("Sensor orientation: %d, Screen orientation: %d", rotation, screenOrientation)

        sensorOrientation = rotation + screenOrientation

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight)
        rgbBytes = IntArray(previewWidth * previewHeight)
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888)
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888)

        frameToCropTransform = ImageUtils.getTransformationMatrix(
                previewWidth, previewHeight,
                cropSize, cropSize,
                sensorOrientation!!, MAINTAIN_ASPECT)

        cropToFrameTransform = Matrix()
        frameToCropTransform!!.invert(cropToFrameTransform)
        yuvBytes = arrayOfNulls(3)

        trackingOverlay = findViewById(R.id.tracking_overlay) as OverlayView
        trackingOverlay!!.addCallback(object : DrawCallback {
            override fun drawCallback(canvas: Canvas) {
                tracker!!.draw(canvas)
                if (isDebug) {
                    tracker!!.drawDebug(canvas)
                }
            }
        })

        addCallback(object : DrawCallback {
            override fun drawCallback(canvas: Canvas) {
                if (!isDebug) {
                    return
                }
                val copy = cropCopyBitmap ?: return

                val backgroundColor = Color.argb(100, 0, 0, 0)
                canvas.drawColor(backgroundColor)

                val matrix = Matrix()
                val scaleFactor = 2f
                matrix.postScale(scaleFactor, scaleFactor)
                matrix.postTranslate(
                        canvas.width - copy.width * scaleFactor,
                        canvas.height - copy.height * scaleFactor)
                canvas.drawBitmap(copy, matrix, Paint())

                val lines = Vector<String>()
                if (detector != null) {
                    val statString = detector!!.statString
                    val statLines = statString.split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    for (line in statLines) {
                        lines.add(line)
                    }
                }
                lines.add("")

                lines.add("Frame: " + previewWidth + "x" + previewHeight)
                lines.add("Crop: " + copy.width + "x" + copy.height)
                lines.add("View: " + canvas.width + "x" + canvas.height)
                lines.add("Rotation: " + sensorOrientation!!)
                lines.add("Inference time: " + lastProcessingTimeMs + "ms")

                borderedText!!.drawLines(canvas, 10f, (canvas.height - 10).toFloat(), lines)
            }
        })
    }

    internal var trackingOverlay: OverlayView? = null

    override fun onImageAvailable(reader: ImageReader) {
        var image: Image? = null

        ++timestamp
        val currTimestamp = timestamp

        try {
            image = reader.acquireLatestImage()

            if (image == null) {
                return
            }

            Trace.beginSection("imageAvailable")

            val planes = image.planes
            fillBytes(planes, yuvBytes!!)

            tracker!!.onFrame(
                    previewWidth,
                    previewHeight,
                    planes[0].rowStride,
                    sensorOrientation!!,
                    yuvBytes[0]!!,
                    timestamp)
            trackingOverlay!!.postInvalidate()

            // No mutex needed as this method is not reentrant.
            if (computing) {
                image.close()
                return
            }
            computing = true

            val yRowStride = planes[0].rowStride
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

        rgbFrameBitmap!!.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight)
        val canvas = Canvas(croppedBitmap!!)
        canvas.drawBitmap(rgbFrameBitmap!!, frameToCropTransform!!, null)

        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap!!)
        }

        if (luminance == null) {
            luminance = ByteArray(yuvBytes[0]!!.size)
        }
        System.arraycopy(yuvBytes[0], 0, luminance!!, 0, luminance!!.size)

        runInBackground(
                Runnable {
                    val startTime = SystemClock.uptimeMillis()
                    val results = detector!!.recognizeImage(croppedBitmap!!)
                    lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime

                    cropCopyBitmap = Bitmap.createBitmap(croppedBitmap)
                    val canvas = Canvas(cropCopyBitmap!!)
                    val paint = Paint()
                    paint.color = Color.RED
                    paint.style = Style.STROKE
                    paint.strokeWidth = 2.0f

                    var minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API
                    when (MODE) {
                        DetectorActivity.DetectorMode.TF_OD_API -> minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API
                        DetectorActivity.DetectorMode.MULTIBOX -> minimumConfidence = MINIMUM_CONFIDENCE_MULTIBOX
                        DetectorActivity.DetectorMode.YOLO -> minimumConfidence = MINIMUM_CONFIDENCE_YOLO
                    }

                    val mappedRecognitions = LinkedList<Classifier.Recognition>()

                    for (result in results) {
                        val location = result.location
                        if (location != null && result.confidence!! >= minimumConfidence) {
                            canvas.drawRect(location, paint)

                            cropToFrameTransform!!.mapRect(location)
                            result.location = location
                            mappedRecognitions.add(result)
                        }
                    }

                    tracker!!.trackResults(mappedRecognitions, luminance!!, currTimestamp)
                    trackingOverlay!!.postInvalidate()

                    requestRender()
                    computing = false
                })

        Trace.endSection()
    }

    override fun processImageRGBbytes(rgbBytes: IntArray) {}

    override val layoutId: Int
        get() = R.layout.camera_connection_fragment_tracking

    override val desiredPreviewFrameSize: Size
        get() = DESIRED_PREVIEW_SIZE

    override fun onSetDebug(debug: Boolean) {
        detector!!.enableStatLogging(debug)
    }

    companion object {
        private val LOGGER = Logger()

        // Configuration values for the prepackaged multibox model.
        private val MB_INPUT_SIZE = 224
        private val MB_IMAGE_MEAN = 128
        private val MB_IMAGE_STD = 128f
        private val MB_INPUT_NAME = "ResizeBilinear"
        private val MB_OUTPUT_LOCATIONS_NAME = "output_locations/Reshape"
        private val MB_OUTPUT_SCORES_NAME = "output_scores/Reshape"
        private val MB_MODEL_FILE = "file:///android_asset/multibox_model.pb"
        private val MB_LOCATION_FILE = "file:///android_asset/multibox_location_priors.txt"

        private val TF_OD_API_INPUT_SIZE = 300
        private val TF_OD_API_MODEL_FILE = "file:///android_asset/ssd_mobilenet_v1_android_export.pb"
        private val TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt"

        // Configuration values for tiny-yolo-voc. Note that the graph is not included with TensorFlow and
        // must be manually placed in the assets/ directory by the user.
        // Graphs and models downloaded from http://pjreddie.com/darknet/yolo/ may be converted e.g. via
        // DarkFlow (https://github.com/thtrieu/darkflow). Sample command:
        // ./flow --model cfg/tiny-yolo-voc.cfg --load bin/tiny-yolo-voc.weights --savepb --verbalise
        private val YOLO_MODEL_FILE = "file:///android_asset/graph-tiny-yolo-voc.pb"
        private val YOLO_INPUT_SIZE = 416
        private val YOLO_INPUT_NAME = "input"
        private val YOLO_OUTPUT_NAMES = "output"
        private val YOLO_BLOCK_SIZE = 32
        private val MODE = DetectorMode.TF_OD_API

        // Minimum detection confidence to track a detection.
        private val MINIMUM_CONFIDENCE_TF_OD_API = 0.6f
        private val MINIMUM_CONFIDENCE_MULTIBOX = 0.1f
        private val MINIMUM_CONFIDENCE_YOLO = 0.25f

        private val MAINTAIN_ASPECT = MODE == DetectorMode.YOLO

        private val DESIRED_PREVIEW_SIZE = Size(640, 480)

        private val SAVE_PREVIEW_BITMAP = false
        private val TEXT_SIZE_DIP = 10f
    }
}
