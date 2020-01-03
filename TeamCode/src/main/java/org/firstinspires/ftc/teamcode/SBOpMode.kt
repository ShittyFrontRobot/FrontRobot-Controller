package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
class SBOpMode : OpMode() {

    private lateinit var sb1: DcMotor
    private lateinit var sb2: DcMotor
    private lateinit var sb3: DcMotor
    private lateinit var sb4: DcMotor

    private val sbs by lazy {
        arrayOf(sb1, sb2, sb3, sb4)
    }

    override fun init() {
        sb1 = hardwareMap.dcMotor["chassis.LF"]
        sb2 = hardwareMap.dcMotor["chassis.LB"]
        sb3 = hardwareMap.dcMotor["chassis.RF"]
        sb4 = hardwareMap.dcMotor["chassis.RB"]


    }


    override fun loop() {

        sb1.power = .3
        sb2.power = .3
        sb3.power = .3
        sb4.power = .3

        telemetry.addData("SB1", sb1.currentPosition)
        telemetry.addData("SB2", sb2.currentPosition)
        telemetry.addData("SB3", sb3.currentPosition)
        telemetry.addData("SB4", sb4.currentPosition)
    }

}