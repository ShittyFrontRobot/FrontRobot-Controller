package org.mechdancer.frontrobot

import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.core.structure.composite.chassis.Mecanum
import org.mechdancer.ftclib.core.structure.injector.Inject

class FrontRobot : Robot(
    "front_robot",
    false,
    Mecanum(enable = true),
    Locator()) {

    @Inject
    lateinit var chassis: Mecanum

    @Inject
    lateinit var locator: Locator

}