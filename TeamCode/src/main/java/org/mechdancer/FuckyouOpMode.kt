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
import kotlin.math.abs


@Naming("淦")
class FuckyouOpMode : RemoteControlOpMode<FrontRobot>() {


    private var targetOnRobot = Pose2D.zero()

    companion object {
        // m
        const val DISTANCE = 0.65
        const val PID_XY_K = 0.8 / DISTANCE
        const val PID_W_K = 0.4 / (PI / 3)
    }

    private val onReset = {
        robot.reset()
    }
    private val pidX = RemotePID(0, remote).also { it.onReset = onReset;it.core = PID(PID_XY_K, .0, .0, .05, .0) }
    private val pidY = RemotePID(1, remote).also { it.onReset = onReset;it.core = PID(PID_XY_K, .0, .0, .05, .0) }
    private val pidW = RemotePID(2, remote).also { it.onReset = onReset;it.core = PID(PID_W_K, .0, .0, .05, .0) }


    override fun initTask() {
        robot.openMV.onTargetDetected = {
            robot.locator.reset()
            targetOnRobot = it plusDelta Pose2D(vector2DOf(DISTANCE, 0), 0.toDegree())
        }
        robot.openMV.onTimeout = {
        }
    }

    override fun loop(master: Gamepad, helper: Gamepad) {
        val (currentPose, currentD) = robot.locator.pose
        val (currentX, currentY) = currentPose
        val (targetPose, targetD) = targetOnRobot
        val (targetX, targetY) = targetPose

        robot.chassis.descartes {
            x = pidX.core(targetX - currentX)
            y = -pidY.core(targetY - currentY)
            w = when {
                abs(targetOnRobot.d.asRadian()) < 0.15 ->
                    PID_W_K * 0.15 * 1.5
                else                                   ->
                    pidW.core(
                        (targetD.asRadian() - currentD.asRadian())
                    )
            }
        }

        // Paint
        remote.paintPose("error", targetOnRobot)
        remote.paintPose("robot", Pose2D.zero())
        remote.paintPose("tag", robot.openMV.idealTagOnRobot)


        telemetry.addData("location", robot.locator.pose)
        telemetry.addData("error", targetOnRobot)
        telemetry.addData("idealTagOnRobot", robot.openMV.idealTagOnRobot)

//        telemetry.addData("Left", robot.locator.currentLeft)
//        telemetry.addData("Right", robot.locator.currentRight)
//        telemetry.addData("Center", robot.locator.currentCenter)


    }

    override fun stopTask() {
    }


}