package org.mechdancer

import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.Pose2D
import org.mechdancer.common.RemotePID
import org.mechdancer.common.paintPose
import org.mechdancer.common.remote
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad
import org.mechdancer.geometry.angle.toDegree
import kotlin.math.PI


@Naming("紫薯布丁")
class FuckyouOpMode : RemoteControlOpMode<FrontRobot>() {


    private var targetOnRobot = Pose2D.zero()

    companion object {
        // m
        const val DISTANCE = 0.8
    }

    private val onReset = {
        //        robot.reset()
    }

    private val pidX = RemotePID(0, remote).also { it.onReset = onReset;it.core = PID(1.5 / 0.8, .0, .0, .15, .08) }
    private val pidY = RemotePID(1, remote).also { it.onReset = onReset;it.core = PID(1.5 / 0.8, .2, .0, .15, .08) }
    private val pidW = RemotePID(2, remote).also { it.onReset = onReset;it.core = PID(0.46, .0, .0, .05, .0) }


    override fun initTask() {
        robot.openMV.onTargetDetected = {
            robot.locator.reset()
            targetOnRobot = it plusDelta Pose2D(vector2DOf(DISTANCE, 0), 0.toDegree())
        }
    }

    override fun loop(master: Gamepad, helper: Gamepad) {
        val (currentPose, currentD) = robot.locator.pose
        val (currentX, currentY) = currentPose
        val (targetPose, targetD) = targetOnRobot
        val (targetX, targetY) = targetPose

        robot.chassis.descartes {
            x = pidX.core(targetX - currentX)
            y = pidY.core(targetY - currentY)
            w = pidW.core((targetD.asRadian() - currentD.asRadian()))
        }

        // Paint
        remote.paintPose("targetOnRobot", targetOnRobot)
        remote.paintPose("tagOnRobot", robot.openMV.idealTagOnRobot)
        remote.paintPose("robotOnWorld", robot.locator.pose)
        remote.paintPose("zeroOfWorld", Pose2D.zero())

        telemetry.addData("robotOnWorld", robot.locator.pose)
        telemetry.addData("targetOnRobot", targetOnRobot)
        telemetry.addData("error", "\nx:${targetX - currentX}\n y:${targetY - currentY}\n w:${(targetD.asRadian() - currentD.asRadian()).let { var result = it;while (result > PI) result -= 2 * PI;while (result < -PI) result += 2 * PI;result }}\n")
        telemetry.addData("tagOnRobot", robot.openMV.idealTagOnRobot)


        telemetry.addData("pidX", pidX.core)
        telemetry.addData("pidY", pidY.core)
        telemetry.addData("pidW", pidW.core)

//        telemetry.addData("Left", robot.locator.currentLeft)
//        telemetry.addData("Right", robot.locator.currentRight)
//        telemetry.addData("Center", robot.locator.currentCenter)


    }

    override fun stopTask() {
    }


}