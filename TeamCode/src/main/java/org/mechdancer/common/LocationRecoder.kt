package org.mechdancer.common

import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.frontrobot.Locator
import org.mechdancer.ftclib.algorithm.MyTimer
import java.io.File
import java.text.DateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object LocationRecoder {

    private val points = ConcurrentHashMap<Long, Pair<Triple<Double, Double, Double>, Pose2D>>()

    private val timer = MyTimer(200)

    private val formatter = DateFormat.getDateInstance()

    fun clear() {
        points.clear()
    }

    fun run(locator: Locator) {
        if (!timer.isFinished) return
        points[System.currentTimeMillis()] =
            Triple(
                locator.currentLeft,
                locator.currentRight,
                locator.currentCenter) to locator.pose
        timer.start()
    }

    fun dump() {
        File(AppUtil.FIRST_FOLDER, "${formatter.format(Date())}-Recording.txt")
            .run {
                createNewFile()
                outputStream()
                    .writer()
                    .use {
                        points.forEach { (timestamp, data) ->
                            val (encoders, pose) = data
                            val (left, right, center) = encoders
                            it.appendln("$timestamp\t$left\t$right\t$center\t${pose.p.x}\t${pose.p.y}\t${pose.d.asRadian()}")
                        }
                    }

            }
    }

}