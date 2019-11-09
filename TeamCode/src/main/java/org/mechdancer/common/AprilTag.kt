package org.mechdancer.common

import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import org.mechdancer.geometry.transformation.Transformation


val idealTagToTag =
    Transformation.fromInhomogeneous(Angle3D(90.0.toDegree(), 0.toRad(), 0.toRad(), AxesOrder.XYZ).matrix, vector3DOfZero())

// Axes order of *AprilTag*: Z, Y, X
// Intrinsic
fun tagToCamera(aprilTag: Pose3D) = aprilTag.toTransformation()

//Axes order of Camera: X, Y, Z
// Extrinsic
fun cameraToRobot(camera: Pose3D) = camera.toTransformation()

fun robotToTag(aprilTag: Pose3D, camera: Pose3D) =
    (cameraToRobot(camera) * tagToCamera(aprilTag)).inverse()

fun robotToIdealTag(aprilTag: Pose3D, camera: Pose3D) =
    (cameraToRobot(camera) * tagToCamera(aprilTag) * idealTagToTag).inverse()