package org.mechdancer

import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad

@Naming("极度去世")
class SmartOpMode : RemoteControlOpMode<FrontRobot>() {
    override fun initTask() {
    }

    override fun loop(master: Gamepad, helper: Gamepad) {
//        robot.chassis.descartes {
//            x = master.leftStick.y
//            y = master.leftStick.x
//            w = -master.rightStick.x
//        }

        robot.chassis.descartes {
            when {
                master.x.bePressed() -> x = .5
                master.y.bePressed() -> y = .5
                master.a.bePressed() -> w = .4
                else                 -> {
                    x = .0
                    y = .0
                    w = .0
                }
            }
        }


        if (master.leftTrigger.bePressed() && master.rightTrigger.bePressed())
            robot.reset()

        telemetry.addData("Location", robot.locator.pose)
        telemetry.addData("Left", robot.locator.currentLeft)
        telemetry.addData("Right", robot.locator.currentRight)
        telemetry.addData("Center", robot.locator.currentCenter)
    }

    override fun stopTask() {
    }
}