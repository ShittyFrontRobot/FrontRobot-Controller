package org.mechdancer

import org.mechdancer.algebra.function.vector.minus
import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.Pose2D
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad
import org.mechdancer.geometry.angle.rotate


@Naming("淦")
class FuckyouOpMode : RemoteControlOpMode<FrontRobot>() {


    private var error = Pose2D.zero()

    companion object {
        const val DISTANCE = 20.0
        val PID_X = PID.zero()
        val PID_Y = PID.zero()
        val PID_W = PID.zero()
    }

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

        robot.chassis.descartes {
            x = PID_X(error.p.y)
            y = PID_Y(error.p.x)
            w = PID_W(error.d.asRadian())
        }
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