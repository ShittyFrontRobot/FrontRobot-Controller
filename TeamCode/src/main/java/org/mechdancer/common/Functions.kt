package org.mechdancer.common

import org.mechdancer.algebra.implement.vector.to2D
import org.mechdancer.algebra.implement.vector.vector2DOfZero
import org.mechdancer.geometry.angle.toAngle
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.toVector
import org.mechdancer.geometry.transformation.Transformation
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

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

private val hexArray = "0123456789ABCDEF".toCharArray()

fun ByteArray.toAsciiString() = String(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(this)).array())

fun ByteArray.toHexString(): String {
    val sb = StringBuilder()
    for (i in indices) {
        val v: Int = this[i].toInt() and 255
        sb.append(hexArray[v shr 4])
        sb.append(hexArray[v and 15])
    }
    return sb.toString()
}

fun ByteArray.toDoubles() = ByteBuffer.wrap(this).let {
    (0..(size / 8)).map { i ->
        it.getDouble(8 * i)
    }
}