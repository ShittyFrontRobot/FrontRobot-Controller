package org.mechdancer

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.algebra.implement.vector.vector2DOfZero
import org.mechdancer.common.*
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toDegree
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.abs


@Naming("淦")
class FuckyouOpMode : RemoteControlOpMode<FrontRobot>() {


    private var error = Pose2D.zero()

    companion object {
        // m
        const val DISTANCE = 0.65
        const val PID_XY_K = 0.8 / DISTANCE
        const val PID_W_K = 0.4 / (PI / 3)
        private val rotate90 = Pose2D(vector2DOfZero(), (-90).toDegree()).toTransformation()

    }

    private val onReset = {
        robot.reset()
    }
    private val pidX = RemotePID(0, remote).also { it.onReset = onReset;it.core = PID(PID_XY_K, .0, .0, .05, .0) }
    private val pidY = RemotePID(1, remote).also { it.onReset = onReset;it.core = PID(PID_XY_K, .0, .0, .05, .0) }
    private val pidW = RemotePID(2, remote).also { it.onReset = onReset;it.core = PID(PID_W_K, .0, .0, .05, .0) }

    // TODO Magic!
    private fun Pose3D.magic() =
        Pose2D(vector2DOf(p.x, p.y), d.third.rotate((-90).toDegree()))
            .let { rotate90.invoke(it) }

    private val int = AtomicInteger(0)

    override fun initTask() {
        robot.openMV.newDataCallback = {
            GlobalScope.launch {
                val current = int.incrementAndGet()
                delay(1000)
                if (int.get() == current) error = Pose2D.zero()
            }
            robot.locator.reset()
            error = it.magic() plusDelta Pose2D(vector2DOf(DISTANCE, 0), 0.toDegree())
        }
    }

    override fun loop(master: Gamepad, helper: Gamepad) {


        robot.chassis.descartes {
            x = pidX.core(error.p.x)
            y = pidY.core(-error.p.y)
            w = when {
                abs(error.d.asRadian()) < 0.15 -> PID_W_K * 0.15 * 1.5
                else                           -> pidW.core(error.d.asRadian())
            }
        }

        // Paint
        remote.paintPose("error", error)
        remote.paintPose("robot", Pose2D.zero())
        remote.paintPose("tag", robot.openMV.idealTagOnRobot.magic())

        telemetry.addData("location", robot.locator.pose)
        telemetry.addData("error", error)
        telemetry.addData("idealTagOnRobot", robot.openMV.idealTagOnRobot)

//
//        telemetry.addData("Left", robot.locator.currentLeft)
//        telemetry.addData("Right", robot.locator.currentRight)
//        telemetry.addData("Center", robot.locator.currentCenter)


    }

    override fun stopTask() {
    }


}