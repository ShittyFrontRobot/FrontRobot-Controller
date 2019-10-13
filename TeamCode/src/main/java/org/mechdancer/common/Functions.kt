package org.mechdancer.common

import org.mechdancer.algebra.core.Vector
import org.mechdancer.algebra.core.columnView
import org.mechdancer.algebra.doubleEquals
import org.mechdancer.algebra.function.matrix.times
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.to2D
import org.mechdancer.algebra.implement.vector.vector2DOfZero
import org.mechdancer.geometry.angle.toAngle
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.angle.toVector
import org.mechdancer.geometry.transformation.Transformation
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Transformation.toPose(): Odometry {
    require(dim == 2) { "pose is a 2d transformation" }
    val p = invoke(vector2DOfZero()).to2D()
    val d = invokeLinear(.0.toRad().toVector()).to2D().toAngle()
    return Odometry(p, d)
}

fun Odometry.toTransformation() =
    Transformation.fromPose(p, d)

operator fun Transformation.invoke(pose: Odometry) =
    Odometry(invoke(pose.p).to2D(), invokeLinear(pose.d.toVector()).to2D().toAngle())

class Vector3D(val x: Double, val y: Double, val z: Double) : Vector {

    override val dim: Int = 3

    override val length: Double = sqrt(x * x + y * y + z * z)

    override fun equals(other: Any?): Boolean =
            if (other is Vector3D)
                doubleEquals(x, other.x) && doubleEquals(y, other.y) && doubleEquals(z, other.z)
            else false


    override fun get(i: Int): Double = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IllegalArgumentException()
    }

    override fun hashCode(): Int = toList().hashCode()

    override fun toList(): List<Double> = listOf(x, y, z)

    override fun toString(): String = columnView()

}

fun vector3DOf(x: Number, y: Number, z: Number) = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())

fun vector3DOfZero() = vector3DOf(0, 0, 0)

fun rMatrixX(a: Double) = matrix {
    row(cos(a), -sin(a), 0)
    row(sin(a), cos(a), 0)
    row(1, 0, 0)
}

fun rMatrixY(a: Double) = matrix {
    row(cos(a), 0, -sin(a))
    row(0, 1, 0)
    row(sin(a), 0, cos(a))

}

fun rMatrixZ(a: Double) = matrix {
    row(1, 0, 0)
    row(0, cos(a), -sin(a))
    row(0, sin(a), cos(a))

}

//Warning, order : y,z,x
fun eulerToMatrix(Rx:Double,Ry:Double,Rz:Double)= rMatrixZ(Rz)* rMatrixY(Ry) * rMatrixX(Rx)

fun aprilTagDataToTransformation(x:Double,y:Double,z:Double,Rx:Double,Ry:Double,Rz:Double)=Transformation.fromInhomogeneous(eulerToMatrix(Rx,Ry,Rz),Vector3D(x,y,z))

data class SixAxisData(var x:Double,var y: Double,var z: Double,var Rx: Double,var Ry: Double,var Rz: Double)

fun SixAxisData.toTrans()=Transformation.fromInhomogeneous(
        eulerToMatrix(Rx,Ry,Rz),
        Vector3D(x,y,z)
        )

