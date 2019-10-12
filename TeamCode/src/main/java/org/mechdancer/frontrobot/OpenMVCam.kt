package org.mechdancer.frontrobot

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType
import com.qualcomm.robotcore.util.TypeConversion
import org.mechdancer.algebra.core.Vector
import org.mechdancer.algebra.core.columnView
import org.mechdancer.algebra.doubleEquals
import org.mechdancer.ftclib.util.SmartLogger
import org.mechdancer.ftclib.util.warn
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

class Vector3D(val x: Double, val y: Double, val z: Double) : Vector {

    override val dim: Int = 3

    override val length: Double = sqrt(x * x + y * y + z * z)

    override fun equals(other: Any?): Boolean =
        if (other is Vector3D)
            doubleEquals(x, other.x) && doubleEquals(y, other.y) && doubleEquals(z, other.z)
        else false


    override fun get(i: Int): Double = when (i) {
        0    -> x
        1    -> y
        2    -> z
        else -> throw IllegalArgumentException()
    }

    override fun hashCode(): Int = toList().hashCode()

    override fun toList(): List<Double> = listOf(x, y, z)

    override fun toString(): String = columnView()

}

fun vector3DOf(x: Number, y: Number, z: Number) = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())

fun vector3DOfZero() = vector3DOf(0, 0, 0)