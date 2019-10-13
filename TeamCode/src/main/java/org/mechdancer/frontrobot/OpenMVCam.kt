package org.mechdancer.frontrobot

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType
import com.qualcomm.robotcore.util.TypeConversion
import org.mechdancer.algebra.core.Matrix
import org.mechdancer.algebra.core.Vector
import org.mechdancer.algebra.core.columnView
import org.mechdancer.algebra.doubleEquals
import org.mechdancer.algebra.function.matrix.dim
import org.mechdancer.algebra.function.matrix.times
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.common.SixAxisData
import org.mechdancer.common.Vector3D
import org.mechdancer.common.vector3DOfZero
import org.mechdancer.ftclib.util.SmartLogger
import org.mechdancer.ftclib.util.warn
import org.mechdancer.geometry.transformation.Transformation
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@I2cDeviceType
@DeviceProperties(name = "OpenMVCam", description = "OpenMVCam", xmlTag = "OpenMVCam")
class OpenMVCam(deviceClient: I2cDeviceSynch) : I2cDeviceSynchDevice<I2cDeviceSynch>(deviceClient, false), SmartLogger {
    override fun doInitialize(): Boolean = true

    override fun getDeviceName(): String = "OpenMVCam"

    override fun getManufacturer(): HardwareDevice.Manufacturer = HardwareDevice.Manufacturer.Other

    init {
        deviceClient.readWindow = I2cDeviceSynch.ReadWindow(
                2,
                1,
                I2cDeviceSynch.ReadMode.ONLY_ONCE
        )
        deviceClient.i2cAddress = I2cAddr.create7bit(0x12)
        registerArmingStateCallback(false)
        engage()
    }


    fun getData():SixAxisData= TODO()

    fun readDouble() = TypeConversion.byteArrayToLong(Array(8) { deviceClient.read8(2) }.toByteArray().also { warn(it.joinToString()) }).toDouble()

    fun rawValue() = Array(6) { readDouble() }.let {
        Vector3D(it[0], it[1], it[2]) to Vector3D(it[3], it[4], it[5])
    }

    fun readByte() = deviceClient.readTimeStamped(2, 1)
}


@TeleOp
class CamTest : OpMode() {
    private var last = false

    private var p = vector3DOfZero()
    private var d = vector3DOfZero()

    private lateinit var cam: OpenMVCam

    private var a = TimestampedData().apply {
        data = byteArrayOf()
        nanoTime = -1
    }

    override fun init() {
        cam = hardwareMap["cam"] as OpenMVCam
    }

    override fun loop() {
//        cam.rawValue().let { (x, y) ->
//            p = x
//            d = y
//        }
        last = false
        if (!last && gamepad1.a) {
            a = cam.readByte()
        }

        telemetry.addData("X", a.data.joinToString())
        telemetry.addData("Y", a.nanoTime)
//        telemetry.addData("P", p)
//        telemetry.addData("D", d)
        last = gamepad1.a

    }

}

