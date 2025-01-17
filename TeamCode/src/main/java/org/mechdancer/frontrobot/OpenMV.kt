package org.mechdancer.frontrobot

import android.app.PendingIntent
import android.content.Intent
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.algebra.function.vector.div
import org.mechdancer.algebra.implement.vector.*
import org.mechdancer.common.*
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.MonomericStructure
import org.mechdancer.ftclib.core.structure.composite.Robot
import org.mechdancer.ftclib.util.OpModeLifecycle
import org.mechdancer.ftclib.util.SmartLogger
import org.mechdancer.ftclib.util.warn
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OpenMV(
    private val enable: Boolean = false
) : MonomericStructure("openMV"),
    OpModeLifecycle.Initialize<Robot>,
    OpModeLifecycle.Start,
    OpModeLifecycle.Stop,
    SmartLogger,
    SerialInputOutputManager.Listener {


    companion object {
        private val camera = Pose3D(vector3DOfZero(), Angle3D(0.toDegree(), 180.toDegree(), (76 + 19 / 60).toDegree(), AxesOrder.ZYX))
        private const val MAGIC_PER_METER = 15.0
        //        private const val MAGIC_PER_METER = 13.1 The Big One
        private val rotate90 = Pose2D(vector2DOfZero(), (-90).toDegree()).toTransformation()

    }

    private lateinit var ioManager: SerialInputOutputManager
    private lateinit var port: UsbSerialPort

    private lateinit var executor: ExecutorService

    var rawTag = Pose3D.zero()
        private set
    var idealTagOnRobot = Pose2D.zero()
        private set

    var onTargetDetected = { _: Pose2D -> }


    // TODO Magic!
    private fun Pose3D.magic() =
        Pose2D(vector2DOf(p.x, p.y), d.third.rotate((-90).toDegree()))
            .let { rotate90.invoke(it) }

    override fun init(opMode: OpModeWithRobot<Robot>) {
        init()
    }

    private fun init() {
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
        if (!enable || !this::ioManager.isInitialized) return
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
                    Angle3D(it[3].toRad(), it[4].toRad(), it[5].toRad(), AxesOrder.XYZ)
                )
            }.let {
                warn("new data: $it")
                rawTag = it
                idealTagOnRobot = idealTagToRobot(it, camera).also { m ->
                    warn("finalMatrix\n$m")
                }.toPose3D(AxesOrder.XYZ).magic()
                onTargetDetected(idealTagOnRobot)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    override fun toString(): String = javaClass.simpleName

}