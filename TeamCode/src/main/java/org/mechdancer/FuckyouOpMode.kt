package org.mechdancer

import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.algebra.implement.vector.vector2DOfZero
import org.mechdancer.common.*
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toDegree


@Naming("淦")
class FuckyouOpMode : RemoteControlOpMode<FrontRobot>() {


    private var error = Pose2D.zero()

    companion object {
        const val DISTANCE = 20.0
        private val rotate90 = Pose2D(vector2DOfZero(), (-90).toDegree()).toTransformation()
    }

    private val onReset = {
        robot.reset()
    }
    private val pidX = RemotePID(0, remote).also { it.onReset = onReset }
    private val pidY = RemotePID(1, remote).also { it.onReset = onReset }
    private val pidW = RemotePID(2, remote).also { it.onReset = onReset }

    private fun Pose3D.magic() =
        Pose2D(vector2DOf(p.x, p.y), d.third.rotate((-90).toDegree()))
            .let { rotate90.invoke(it) }

    override fun initTask() {
        robot.openMV.newDataCallback = {
            robot.locator.reset()
            error = it.magic().plusDelta(Pose2D(vector2DOf(.65, 0), 0.toDegree()))
        }
    }

    override fun loop(master: Gamepad, helper: Gamepad) {


        robot.chassis.descartes {
            x = pidX.core(error.p.y)
            y = pidY.core(error.p.x)
            w = pidW.core(error.d.asRadian())
        }

        remote.paintPose("error", error)
        remote.paintPose("robot", Pose2D.zero())
//        remote.paintPose("tag", robot.openMV.idealTagOnRobot.let {
//            Pose2D(vector2DOf(it.p.x, it.p.y), it.d.third.rotate((-90).toDegree()))
//        }.let { rotate90.invoke(it) })
        remote.paintPose("tag", robot.openMV.idealTagOnRobot.magic())
        telemetry.addData("location", robot.locator.pose)
        telemetry.addData("error", error)
        telemetry.addData("idealTagOnRobot", robot.openMV.idealTagOnRobot)

        telemetry.addData("Left", robot.locator.currentLeft)
        telemetry.addData("Right", robot.locator.currentRight)
        telemetry.addData("Center", robot.locator.currentCenter)


    }

    override fun stopTask() {
    }


}