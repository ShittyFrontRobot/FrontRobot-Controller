package org.mechdancer

import org.mechdancer.algebra.function.vector.minus
import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.Pose2D
import org.mechdancer.common.RemotePID
import org.mechdancer.common.paintPose
import org.mechdancer.common.remote
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad
import org.mechdancer.geometry.angle.rotate


@Naming("淦")
class FuckyouOpMode : RemoteControlOpMode<FrontRobot>() {


    private var error = Pose2D.zero()

    companion object {
        const val DISTANCE = 20.0

    }

    private val onReset = {
        robot.reset()
    }
    private val pidX = RemotePID(0, remote).also { it.onReset = onReset }
    private val pidY = RemotePID(1, remote).also { it.onReset = onReset }
    private val pidW = RemotePID(2, remote).also { it.onReset = onReset }



    override fun initTask() {
        robot.openMV.newDataCallback = {
            robot.locator.reset()
            val tag2D = vector2DOf(it.p.x, it.p.y)
            val d = it.d.third
            val target = vector2DOf(0, DISTANCE).rotate(d)
            error = Pose2D(tag2D - target, d)
        }
    }

    override fun loop(master: Gamepad, helper: Gamepad) {

        /*
        robot.chassis.descartes {
            x = pidX.core(error.p.y)
            y = pidY.core(error.p.x)
            w = pidW.core(error.d.asRadian())
        }
        */
        remote.paintPose("error", error)
        remote.paintPose("robot", Pose2D.zero())
        remote.paintPose("tag", robot.openMV.idealTagOnRobot.let {
            Pose2D(vector2DOf(it.p.x, it.p.y), it.d.third)
        })
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