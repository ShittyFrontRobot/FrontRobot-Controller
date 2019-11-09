package org.mechdancer

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.common.Pose3D
import org.mechdancer.common.robotToTag
import org.mechdancer.common.tagToCamera
import org.mechdancer.common.toPose3D
import org.mechdancer.frontrobot.OpenMV
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder


@TeleOp
class FuckyouOpMode() : OpMode() {
    private lateinit var openMV: OpenMV

    private val camera = Pose3D(vector3DOfZero(), Angle3D(90.0.toDegree(), (-90.0).toDegree(), .0.toDegree(), AxesOrder.XYZ))

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
        telemetry.addData("trans", tagToCamera(openMV.aprilTag).toPose3D(AxesOrder.XYZ))
        telemetry.addData("fuck you", robotToTag(openMV.aprilTag, camera).toPose3D(AxesOrder.XYZ))
    }

    override fun stop() {
        openMV.stop()
    }
}