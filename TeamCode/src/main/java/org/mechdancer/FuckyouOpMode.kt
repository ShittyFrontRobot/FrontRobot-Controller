package org.mechdancer

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.mechdancer.frontrobot.OpenMV


@TeleOp
class FuckyouOpMode() : OpMode() {
    private lateinit var openMV: OpenMV

    override fun init() {
        openMV = OpenMV()
        openMV.init()
    }

    override fun start() {
        openMV.start()
    }

    override fun loop() {
        openMV.run()
        telemetry.addData("1", openMV.aprilTag.toString())
    }

    override fun stop() {
        openMV.stop()
    }
}