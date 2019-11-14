package org.mechdancer

import org.mechdancer.common.Pose2D
import org.mechdancer.common.display
import org.mechdancer.common.paintPose
import org.mechdancer.common.remote
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.algorithm.Lens
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad

@Naming("极度去世")
class SmartOpMode : RemoteControlOpMode<FrontRobot>() {

    private lateinit var limiter: Lens

    override fun initTask() {
        limiter = Lens(-1.0, 1.0, -0.3, 0.3)
    }

    override fun startTask() {
        robot.reset()
    }


    override fun loop(master: Gamepad, helper: Gamepad) {

        robot.locator.run()
        robot.chassis.descartes {
            x = (master.leftStick.y)
            y = (master.leftStick.x)
            w = -(master.rightStick.x)
        }
//
//        robot.chassis.descartes {
//            when {
//                master.x.bePressed() -> x = .5
//                master.y.bePressed() -> y = .5
//                master.a.bePressed() -> w = .4
//                else                 -> {
//                    x = .0
//                    y = .0
//                    w = .0
//                }
//            }
//        }


        if (master.leftTrigger.bePressed() && master.rightTrigger.bePressed())
            robot.reset()

        telemetry.addData("Location", robot.locator.pose.display())
        telemetry.addData("deg", robot.locator.deg)
        telemetry.addData("Encoder values", robot.locator.showEncoderValues())

        remote.paintPose("robotOnWorld", robot.locator.pose)
        remote.paintPose("zeroOfWorld", Pose2D.zero())
    }

    override fun stopTask() {
    }
}