package org.mechdancer.common

import org.mechdancer.algebra.implement.vector.Vector3D
import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder

data class Pose3D(
    val p: Vector3D,
    val d: Angle3D
) {
    companion object {
        fun zero() = Pose3D(vector3DOfZero(), Angle3D(0.toRad(), 0.toRad(), 0.toRad(), AxesOrder.ZYZ))
    }
}