package org.mechdancer.frontrobot

import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.OmniDirectionOdometry
import org.mechdancer.common.Pose2D
import org.mechdancer.common.paint
import org.mechdancer.common.remote
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.encoder
import org.mechdancer.ftclib.core.structure.monomeric.sensor.Encoder
import org.mechdancer.ftclib.util.AutoCallable
import org.mechdancer.ftclib.util.Resettable
import org.mechdancer.geometry.angle.toDegree

class Locator(private val enable: Boolean = false)
    : AbstractStructure("chassis", {
    // Left
    encoder("LF") {
        cpr = ENCODER_CPR
        this.enable = enable
    }
    // Right
    encoder("LB") {
        cpr = ENCODER_CPR
        this.enable = enable
    }
    // Center
    encoder("RF") {
        cpr = ENCODER_CPR
        this.enable = enable
    }
}), AutoCallable, Resettable {

    @Inject(name = "LF")
    private lateinit var e0: Encoder
    @Inject(name = "LB")
    private lateinit var e1: Encoder
    @Inject(name = "RF")
    private lateinit var e2: Encoder

    private var e0n = .0
    private var e1n = .0
    private var e2n = .0

    val currentLeft
        get() = e0.position
    val currentRight
        get() = e1.position
    val currentCenter
        get() = e2.position

    // TODO: Encoder pose
    private val odometry: OmniDirectionOdometry = OmniDirectionOdometry(
        Pose2D(vector2DOf(0.048, 0.068), 135.toDegree()),
        Pose2D(vector2DOf(0.0435, -0.07), 45.toDegree()),
        Pose2D(vector2DOf(-0.031, 0.005), 0.toDegree())
    )

    val pose
        get() = odometry.pose

    var count = 0

    override fun run() {
        if (!enable) return

        if (count++ % 1000 != 0) return

        val `e0n-1` = e0n
        val `e1n-1` = e1n
        val `e2n-1` = e2n
        e0n = e0.position * TRACK
        e1n = e1.position * TRACK
        e2n = e2.position * TRACK

        val d0 = e0n - `e0n-1`
        val d1 = e1n - `e1n-1`
        val d2 = e2n - `e2n-1`

        remote.paint("d0", d0)
        remote.paint("d1", d1)
        remote.paint("d2", d2)

        odometry.update(d0, d1, d2)
    }

    override fun reset() {
        if (!enable) return
        odometry.clean()
        e0.reset(.0)
        e1.reset(.0)
        e2.reset(.0)
        e0n = .0
        e1n = .0
        e2n = .0
    }

    private fun Encoder.shit() = position * TRACK

    companion object {
        const val ENCODER_CPR = -4000.0
        const val TRACK = 0.039312
    }
}