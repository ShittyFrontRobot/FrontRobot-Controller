package org.mechdancer.frontrobot

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.mechdancer.common.Uart
import org.mechdancer.common.toAsciiString
import org.mechdancer.ftclib.util.SmartLogger
import org.mechdancer.ftclib.util.warn

@TeleOp
class FKOpMode : OpMode(), SmartLogger {

    private lateinit var uart: Uart

    @Volatile
    private var value = ""

    @Volatile
    private var finish = true

    override fun init() {
        uart = Uart(hardwareMap.appContext)
        uart.requestPermission()
        uart.getAndOpenDevice()
        uart.initUartDevice()
        uart.setConfig(Uart.Config(baudRate = Uart.BaudRate._19200, parity = 2))

    }

    override fun start() {
        GlobalScope.launch {
            while (isActive) {
                uart.read(32)?.let {
                    val str = it.toAsciiString()

                    if (finish)
                        value = str
                    else value += str

                    finish = str.last() == '\r'

                    warn(it.size)
                }
            }
        }
    }

    override fun loop() {


        warn(value)
        telemetry.addData("Data", value)
    }

    override fun stop() {
        uart.close()
    }

}