package org.mechdancer.frontrobot

import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor

class FrontRobot : Robot(
    "front_robot",
    false,
    Mecanum(
        enable = true,
        lfMotorDirection = Motor.Direction.FORWARD,
        lbMotorDirection = Motor.Direction.FORWARD,
        rfMotorDirection = Motor.Direction.FORWARD,
        rbMotorDirection = Motor.Direction.FORWARD
    ),
    MecanumLocator(),
    OpenMV(true)
) {

    @Inject
    lateinit var chassis: Mecanum

    @Inject(name = "chassis")
    lateinit var locator: MecanumLocator

    @Inject
    lateinit var openMV: OpenMV

}