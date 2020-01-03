package org.mechdancer

import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad

@Naming("疋")
class ShitOpMode : RemoteControlOpMode<FrontRobot>() {
    override fun initTask() {

    }

    override fun loop(master: Gamepad, helper: Gamepad) {

        robot.chassis.descartes {
            x = master.leftStick.y
            y = -master.leftStick.x
            w = -master.rightStick.x
        }

        telemetry.addData("encoders", robot.locator.showEncoderValues())
    }

    override fun stopTask() {
    }

}