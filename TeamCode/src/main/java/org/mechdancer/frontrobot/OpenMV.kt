package org.mechdancer.frontrobot

import android.app.PendingIntent
import android.content.Intent
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.algebra.implement.vector.vector3DOf
import org.mechdancer.common.Pose3D
import org.mechdancer.common.usbManager
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.OpModeLifecycle
import org.mechdancer.ftclib.util.SmartLogger
import org.mechdancer.ftclib.util.warn
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OpenMV
    : MonomericStructure("openMV"),
      OpModeLifecycle.Initialize<Robot>,
      OpModeLifecycle.Start,
      OpModeLifecycle.Stop,
      SmartLogger,
      SerialInputOutputManager.Listener {


    private lateinit var ioManager: SerialInputOutputManager
    private lateinit var port: UsbSerialPort

    private lateinit var executor: ExecutorService

    var aprilTag = Pose3D.zero()


    override fun init(opMode: OpModeWithRobot<Robot>) {
        init()
    }

    fun init() {
        val drivers =
            UsbSerialProber
                .getDefaultProber()
                .findAllDrivers(usbManager())

        warn(drivers.joinToString { it.javaClass.simpleName })
        val driver = drivers.first()
        val connection = usbManager().openDevice(driver.device)
        if (connection == null)
            usbManager().requestPermission(driver.device, PendingIntent.getBroadcast(AppUtil.getDefContext(), 0, Intent("ch34x"), 0))
        port = driver.ports.first()
        port.open(connection)
        port.setParameters(19200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_EVEN)
        ioManager = SerialInputOutputManager(port, this)
    }

    override fun start() {
        executor = Executors.newSingleThreadExecutor().apply { submit(ioManager) }
    }

    override fun run() {
    }

    override fun stop() {
        ioManager.stop()
        port.close()
        executor.shutdown()
    }

    override fun onRunError(e: Exception) {
        throw e
    }

    override fun onNewData(data: ByteArray) {
        try {
            String(data).trim().split(",").map { it.toDouble() }.let {
                // Intrinsic Z-Y-X -> Extrinsic X-Y-Z
                Pose3D(
                    vector3DOf(it[0], it[1], it[2]),
                    Angle3D(it[3].toRad(), it[4].toRad(), it[5].toRad(), AxesOrder.XYZ)
                )
            }.let { aprilTag = it }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun toString(): String = javaClass.simpleName
}