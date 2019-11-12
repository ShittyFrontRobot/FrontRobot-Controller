package org.mechdancer

import org.mechdancer.common.display
import org.mechdancer.common.paintPose
import org.mechdancer.common.remote
import org.mechdancer.frontrobot.FrontRobot
import org.mechdancer.ftclib.algorithm.Lens
import org.mechdancer.ftclib.algorithm.NEXT
import org.mechdancer.ftclib.algorithm.StateMachine
import org.mechdancer.ftclib.classfilter.Naming
import org.mechdancer.ftclib.core.opmode.async.RemoteControlOpModeAsync
import org.mechdancer.ftclib.gamepad.Gamepad

@Naming("极度去世")
class SmartOpMode : RemoteControlOpModeAsync<FrontRobot>() {
    override val afterStopMachine: StateMachine = { NEXT }
    override val initLoopMachine: StateMachine = { NEXT }

    private lateinit var limiter: Lens

    init {
        initTask.add {
            limiter = Lens(-1.0, 1.0, -0.3, 0.3)
            NEXT
        }
        displayTask.add {
            telemetry.addData("Location", robot.locator.pose.display())
            telemetry.addData("Left", robot.locator.currentLeft)
            telemetry.addData("Right", robot.locator.currentRight)
            telemetry.addData("Center", robot.locator.currentCenter)
            remote.paintPose("robot", robot.locator.pose)
        }
    }

    override fun loop(master: Gamepad, helper: Gamepad) {
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


    }
}