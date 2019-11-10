package org.mechdancer

import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.gamepad.Gamepad

class SmartOpMode : RemoteControlOpMode<FrontRobot>() {
    override fun initTask() {
    }

    override fun loop(master: Gamepad, helper: Gamepad) {
        robot.chassis.descartes {
            x = master.leftStick.y
            y = master.leftStick.x
            w = -master.rightStick.x
        }
        telemetry.addData("Location", robot.locator.pose)
    }

    override fun stopTask() {
    }
}