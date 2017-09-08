/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package demo.tensorflow_demo

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Trace
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Comparator
import java.util.PriorityQueue
import java.util.Vector
import org.tensorflow.Operation
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

/** A classifier specialized to label images using TensorFlow.  */
class TensorFlowImageClassifier private constructor() : Classifier {

    // Config values.
    private var inputName: String? = null
    private var outputName: String? = null
    private var inputSize: Int = 0
    private var imageMean: Int = 0
    private var imageStd: Float = 0.toFloat()

    // Pre-allocated buffers.
    private val labels = Vector<String>()
    private var intValues: IntArray? = null
    private var floatValues: FloatArray? = null
    private var outputs: FloatArray? = null
    private var outputNames: Array<String>? = null

    private var logStats = false

    private var inferenceInterface: TensorFlowInferenceInterface? = null

    override fun recognizeImage(bitmap: Bitmap): List<Classifier.Recognition> {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage")

        Trace.beginSection("preprocessBitmap")
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (i in intValues!!.indices) {
            val value = intValues!![i]
            floatValues!![i * 3 + 0] = ((value shr 16 and 0xFF) - imageMean) / imageStd
            floatValues!![i * 3 + 1] = ((value shr 8 and 0xFF) - imageMean) / imageStd
            floatValues!![i * 3 + 2] = ((value and 0xFF) - imageMean) / imageStd
        }
        Trace.endSection()

        // Copy the input data into TensorFlow.
        Trace.beginSection("feed")
        inferenceInterface!!.feed(inputName, floatValues, 1, inputSize.toLong(), inputSize.toLong(), 3)
        Trace.endSection()

        // Run the inference call.
        Trace.beginSection("run")
        inferenceInterface!!.run(outputNames!!, logStats)
        Trace.endSection()

        // Copy the output Tensor back into the output array.
        Trace.beginSection("fetch")
        inferenceInterface!!.fetch(outputName, outputs)
        Trace.endSection()

        // Find the best classifications.
        val pq = PriorityQueue(
                3,
                Comparator<Classifier.Recognition> { lhs, rhs ->
                    // Intentionally reversed to put high confidence at the head of the queue.
                    java.lang.Float.compare(rhs.confidence!!, lhs.confidence!!)
                })
        for (i in outputs!!.indices) {
            if (outputs!![i] > THRESHOLD) {
                pq.add(
                        Classifier.Recognition(
                                "" + i, if (labels.size > i) labels[i] else "unknown", outputs!![i], null))
            }
        }
        val recognitions = ArrayList<Classifier.Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0..recognitionsSize - 1) {
            recognitions.add(pq.poll())
        }
        Trace.endSection() // "recognizeImage"
        return recognitions
    }

    override fun enableStatLogging(logStats: Boolean) {
        this.logStats = logStats
    }

    override val statString: String
        get() = inferenceInterface!!.statString

    override fun close() {
        inferenceInterface!!.close()
    }

    companion object {
        private val TAG = "TensorFlowClassifier"

        // Only return this many results with at least this confidence.
        private val MAX_RESULTS = 3
        private val THRESHOLD = 0.1f

        /**
         * Initializes a native TensorFlow session for classifying images.
         *
         * @param assetManager The asset manager to be used to load assets.
         * @param modelFilename The filepath of the model GraphDef protocol buffer.
         * @param labelFilename The filepath of label file for classes.
         * @param inputSize The input size. A square image of inputSize x inputSize is assumed.
         * @param imageMean The assumed mean of the image values.
         * @param imageStd The assumed std of the image values.
         * @param inputName The label of the image input node.
         * @param outputName The label of the output node.
         * @throws IOException
         */
        fun create(
                assetManager: AssetManager,
                modelFilename: String,
                labelFilename: String,
                inputSize: Int,
                imageMean: Int,
                imageStd: Float,
                inputName: String,
                outputName: String): Classifier {
            val c = TensorFlowImageClassifier()
            c.inputName = inputName
            c.outputName = outputName

            // Read the label names into memory.
            // TODO(andrewharp): make this handle non-assets.
            val actualFilename = labelFilename.split("file:///android_asset/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
            Log.i(TAG, "Reading labels from: " + actualFilename)
            val br: BufferedReader
            try {
                br = BufferedReader(InputStreamReader(assetManager.open(actualFilename)))
                var line: String? = null
                while (br.readLine().apply { line = this } != null) {
                    c.labels.add(line!!)
                }
                br.close()
            } catch (e: IOException) {
                throw RuntimeException("Problem reading label file!", e)
            }

            c.inferenceInterface = TensorFlowInferenceInterface(assetManager, modelFilename)

            // The shape of the output is [N, NUM_CLASSES], where N is the batch size.
            val operation = c.inferenceInterface!!.graphOperation(outputName)
            val numClasses = operation.output(0).shape().size(1).toInt()
            Log.i(TAG, "Read " + c.labels.size + " labels, output layer size is " + numClasses)

            // Ideally, inputSize could have been retrieved from the shape of the input operation.  Alas,
            // the placeholder node for input in the graphdef typically used does not specify a shape, so it
            // must be passed in as a parameter.
            c.inputSize = inputSize
            c.imageMean = imageMean
            c.imageStd = imageStd

            // Pre-allocate buffers.
            c.outputNames = arrayOf(outputName)
            c.intValues = IntArray(inputSize * inputSize)
            c.floatValues = FloatArray(inputSize * inputSize * 3)
            c.outputs = FloatArray(numClasses)

            return c
        }
    }
}
