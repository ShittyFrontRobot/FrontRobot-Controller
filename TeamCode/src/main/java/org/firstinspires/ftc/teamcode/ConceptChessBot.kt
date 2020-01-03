package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.mechdancer.ftclib.core.opmode.RemoteControlOpMode
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.gamepad.Gamepad

class ChessBot : Robot("chess_bot", Mecanum()) {

    @Inject
    lateinit var chassis: Mecanum

}

@Disabled
class ChessBotOpMode : RemoteControlOpMode<ChessBot>() {
    override fun initTask() {

    }

    override fun loop(master: Gamepad, helper: Gamepad) {
        robot.chassis.descartes {
            x = master.leftStick.y
            y = master.leftStick.x
            w = -master.rightStick.x
        }
    }

    override fun stopTask() {
    }

}