package org.mechdancer

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.common.Pose3D
import org.mechdancer.common.robotToTag
import org.mechdancer.common.tagToCamera
import org.mechdancer.common.toPose3D
import org.mechdancer.frontrobot.OpenMV
import org.mechdancer.geometry.angle.toDegree


@TeleOp
class FuckyouOpMode() : OpMode() {
    private lateinit var openMV: OpenMV

    private val camera = Pose3D(.0, .0, .0, 90.0.toDegree(), (-90.0).toDegree(), .0.toDegree())
    override fun init() {
        openMV = OpenMV()
        openMV.init()
    }

    override fun start() {
        openMV.start()
    }

    override fun loop() {
        openMV.run()
        telemetry.addData("raw", openMV.aprilTag.toString())
        telemetry.addData("trans", tagToCamera(openMV.aprilTag).toPose3D())
        telemetry.addData("fuck you", robotToTag(openMV.aprilTag, camera).toPose3D())
    }

    override fun stop() {
        openMV.stop()
    }
}