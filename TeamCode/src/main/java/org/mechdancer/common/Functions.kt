package org.mechdancer.common

import android.content.Context
import android.hardware.usb.UsbManager
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.mechdancer.algebra.implement.matrix.Cofactor
import org.mechdancer.algebra.implement.vector.to2D
import org.mechdancer.algebra.implement.vector.to3D
import org.mechdancer.algebra.implement.vector.vector2DOfZero
import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.geometry.angle.toAngle
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.toVector
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import org.mechdancer.geometry.transformation.Transformation

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


fun Transformation.toPose3D(): Pose3D {
    require(dim == 3)
    val move = invokeLinear(vector3DOfZero()).to3D()
    val linear = Cofactor(matrix, 3, 3)
    val angle = Angle3D.fromMatrix<Angle3D.RollPitchYaw>(linear, AxesOrder.XYZ)
    return Pose3D(move.x, move.y, move.z, angle.first, angle.second, angle.third)
}

fun usbManager() = AppUtil.getDefContext().getSystemService(Context.USB_SERVICE) as UsbManager