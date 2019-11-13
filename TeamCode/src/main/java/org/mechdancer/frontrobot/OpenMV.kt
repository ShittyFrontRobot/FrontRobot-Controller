package org.mechdancer.frontrobot

import android.app.PendingIntent
import android.content.Intent
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.algebra.function.vector.div
import org.mechdancer.algebra.implement.vector.to3D
import org.mechdancer.algebra.implement.vector.vector3DOf
import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.common.Pose3D
import org.mechdancer.common.idealTagToRobot
import org.mechdancer.common.toPose3D
import org.mechdancer.common.usbManager
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.OpModeLifecycle
import org.mechdancer.ftclib.util.SmartLogger
import org.mechdancer.ftclib.util.warn
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OpenMV(private val enable: Boolean = false)
    : MonomericStructure("openMV"),
      OpModeLifecycle.Initialize<Robot>,
      OpModeLifecycle.Start,
      OpModeLifecycle.Stop,
      SmartLogger,
      SerialInputOutputManager.Listener {


    companion object {
        private val camera = Pose3D(vector3DOfZero(), Angle3D(0.toDegree(), 180.toDegree(), (76 + 19 / 60).toDegree(), AxesOrder.ZYX))
        private const val MAGIC_PER_METER = 20.0
    }

    private lateinit var ioManager: SerialInputOutputManager
    private lateinit var port: UsbSerialPort

    private lateinit var executor: ExecutorService


    var rawTag = Pose3D.zero()
    var idealTagOnRobot = Pose3D.zero()

    var newDataCallback = { _: Pose3D -> }

    override fun init(opMode: OpModeWithRobot<Robot>) {
        init()
    }

    fun init() {
        if (!enable) return
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
        if (!enable) return
        executor = Executors.newSingleThreadExecutor().apply { submit(ioManager) }
    }

    override fun run() {
    }

    override fun stop() {
        if (!enable) return
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
                    // 1m -> 20.0
                    (vector3DOf(it[0], it[1], it[2]) / MAGIC_PER_METER).to3D(),
                    Angle3D(it[3].toDegree(), it[4].toDegree(), it[5].toDegree(), AxesOrder.XYZ)
                )
            }.let {
                warn("new data: $it")
                rawTag = it
                idealTagOnRobot = idealTagToRobot(it, camera).toPose3D(AxesOrder.XYZ)
                newDataCallback(idealTagOnRobot)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun toString(): String = javaClass.simpleName
}