package org.mechdancer.common

import org.mechdancer.algebra.implement.vector.vector3DOf
import org.mechdancer.geometry.angle.Angle
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import org.mechdancer.geometry.transformation.Transformation


data class Pose3D(
    val tx: Double,
    val ty: Double,
    val tz: Double,
    val rx: Angle,
    val ry: Angle,
    val rz: Angle
) {
    companion object {
        fun zero() = Pose3D(.0, .0, .0, 0.toRad(), 0.toRad(), 0.toRad())
    }
}

val idealTagToTag = Transformation.fromInhomogeneous(Angle3D.Euler(90.0.toDegree(), 0.toRad(), 0.toRad(), AxesOrder.XYZ).matrix, vector3DOf(.0, .0, .0))

// Axes order of *AprilTag*: Z, Y, X
// Intrinsic
fun tagToCamera(aprilTag: Pose3D) =
    with(aprilTag) {
        Transformation.fromInhomogeneous(Angle3D.Euler(rx, ry, rz, AxesOrder.ZYX).matrix, vector3DOf(tx, ty, tz))
    }

//Axes order of Camera: X, Y, Z
// Extrinsic
fun cameraToRobot(camera: Pose3D) =
    with(camera) {
        Transformation.fromInhomogeneous(Angle3D.RollPitchYaw(rx, ry, rz, AxesOrder.XYZ).matrix, vector3DOf(tx, ty, tz))
    }

fun robotToTag(aprilTag: Pose3D, camera: Pose3D) =
    (cameraToRobot(camera) * tagToCamera(aprilTag)).inverse()

fun robotToIdealTag(aprilTag: Pose3D, camera: Pose3D) =
    (cameraToRobot(camera) * tagToCamera(aprilTag) * idealTagToTag).inverse()