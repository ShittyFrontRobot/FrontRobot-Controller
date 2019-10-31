package org.mechdancer.frontrobot

import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.common.Pose3D
import org.mechdancer.common.Uart
import org.mechdancer.common.toDoubles
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.AutoCallable
import org.mechdancer.ftclib.util.OpModeLifecycle
import org.mechdancer.geometry.angle.toRad

class OpenMV
    : MonomericStructure("openmv"),
      OpModeLifecycle.Initialize<Robot>,
      OpModeLifecycle.Start,
      OpModeLifecycle.Stop,
      AutoCallable {

    private lateinit var uart: Uart

    lateinit var aprilTag: Pose3D

    override fun init(opMode: OpModeWithRobot<Robot>) {
        uart = Uart(AppUtil.getDefContext())
        uart.requestPermission()
    }

    override fun start() {
        uart.init(Uart.Config(baudRate = Uart.BaudRate._19200))
    }

    override fun run() {
        uart.read(64)?.toDoubles()?.let {
            Pose3D(it[0], it[1], it[2], it[3].toRad(), it[4].toRad(), it[5].toRad())
        }?.let { aprilTag = it }
    }

    override fun stop() {
        uart.close()
    }

    override fun toString(): String = javaClass.simpleName
}