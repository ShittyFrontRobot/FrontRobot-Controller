package org.mechdancer.common

import android.content.Context
import android.hardware.usb.UsbManager
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.algebra.implement.matrix.Cofactor
import org.mechdancer.algebra.implement.vector.to2D
import org.mechdancer.algebra.implement.vector.to3D
import org.mechdancer.algebra.implement.vector.vector2DOfZero
import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DynamicScope
import org.mechdancer.dependency.plusAssign
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.geometry.angle.toAngle
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.toVector
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import org.mechdancer.geometry.transformation.Transformation
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.text.DecimalFormat

// Math

fun Transformation.toPose(): Pose2D {
    require(dim == 2) { "pose is a 2d transformation" }
    val p = invoke(vector2DOfZero()).to2D()
    val d = invokeLinear(.0.toRad().toVector()).to2D().toAngle()
    return Pose2D(p, d)
}

fun Pose2D.toTransformation() =
    Transformation.fromPose(p, d)

operator fun Transformation.invoke(pose: Pose2D) =
    Pose2D(invoke(pose.p).to2D(), invokeLinear(pose.d.toVector()).to2D().toAngle())


fun Transformation.toPose3D(axesOrder: AxesOrder): Pose3D {
    require(dim == 3)
    val move = invoke(vector3DOfZero()).to3D()
    val linear = Cofactor(matrix, 3, 3)
    val angle = Angle3D.fromMatrix(linear, axesOrder)
    return Pose3D(move, angle)
}

fun Pose3D.toTransformation() =
    Transformation.fromInhomogeneous(d.matrix, p)

// Misc

fun usbManager() = AppUtil.getDefContext().getSystemService(Context.USB_SERVICE) as UsbManager

private val sbFormat = DecimalFormat("#.0000")


// Remote

fun Pose2D.display() =
    "(${sbFormat.format(p.x)},${sbFormat.format(p.y)})(${sbFormat.format(d.asRadian())})"


fun RemoteHub.addDependency(component: Component) {
    (RemoteHub::class.java.declaredFields.find { it.name.contains("scope") }!!.also {
        it.isAccessible = true
    }[this] as DynamicScope) += component
}

fun RemoteHub.sendPID(pid: PID, id: Int, reset: Boolean = true) =
    broadcast(object : Command {
        override val id: Byte = 9
    }, ByteArrayOutputStream().let {
        DataOutputStream(it).run {
            with(pid) {
                writeInt(id)
                writeDouble(k)
                writeDouble(ki)
                writeDouble(kd)
                writeDouble(integrateArea)
                writeDouble(deadArea)
                writeBoolean(reset)
            }
            it.toByteArray()
        }
    })

fun RemoteHub.sendDouble(double: Double, id: Int) =
    broadcast(object : Command {
        override val id: Byte = 32
    }, ByteArrayOutputStream().let {
        DataOutputStream(it).run {
            writeInt(id)
            writeDouble(double)
            it.toByteArray()
        }
    })
