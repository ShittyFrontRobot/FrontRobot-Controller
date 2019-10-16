package org.mechdancer.frontrobot

import android.content.Context
import android.hardware.usb.UsbManager
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.OpModeLifecycle

class RaspberryPi :
    MonomericStructure("raspberryPi"),
    OpModeLifecycle.Initialize<Robot> {

    companion object {
        private fun usbManager() =
            AppUtil.getDefContext().getSystemService(Context.USB_SERVICE) as UsbManager
    }

    override fun init(opMode: OpModeWithRobot<Robot>) {
        usbManager().deviceList.forEach {  }
    }

    override fun run() {

    }

    override fun toString(): String = name

}