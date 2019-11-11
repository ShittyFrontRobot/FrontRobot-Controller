package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
@Disabled
class SBOpMode : OpMode() {

    private lateinit var sb: DcMotor
    override fun init() {
        sb = hardwareMap.dcMotor["chassis.RF"]
    }

    override fun loop() {
        telemetry.addData("SB", sb.currentPosition)
    }

}