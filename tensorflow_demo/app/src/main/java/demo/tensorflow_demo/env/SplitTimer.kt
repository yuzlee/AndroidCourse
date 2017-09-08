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

package demo.tensorflow_demo.env

import android.os.SystemClock

/**
 * A simple utility timer for measuring CPU time and wall-clock splits.
 */
class SplitTimer(name: String) {
    private val logger: Logger

    private var lastWallTime: Long = 0
    private var lastCpuTime: Long = 0

    init {
        logger = Logger(name)
        newSplit()
    }

    fun newSplit() {
        lastWallTime = SystemClock.uptimeMillis()
        lastCpuTime = SystemClock.currentThreadTimeMillis()
    }

    fun endSplit(splitName: String) {
        val currWallTime = SystemClock.uptimeMillis()
        val currCpuTime = SystemClock.currentThreadTimeMillis()

        logger.i(
                "%s: cpu=%dms wall=%dms",
                splitName, currCpuTime - lastCpuTime, currWallTime - lastWallTime)

        lastWallTime = currWallTime
        lastCpuTime = currCpuTime
    }
}
