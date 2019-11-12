package org.mechdancer.frontrobot

import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.OmniDirectionOdometry
import org.mechdancer.common.Pose2D
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

    private var lastE0 = .0
    private var lastE1 = .0
    private var lastE2 = .0

    val currentLeft
        get() = e0.position
    val currentRight
        get() = e1.position
    val currentCenter
        get() = e2.position

    // TODO: Encoder pose
    private val odometry: OmniDirectionOdometry = OmniDirectionOdometry(
        Pose2D(vector2DOf(0.03426, 0.08065), 135.toDegree()),
        Pose2D(vector2DOf(0.03425, -0.08065), 45.toDegree()),
        Pose2D(vector2DOf(-0.046, 0.019), 0.toDegree())
    )

    val pose
        get() = odometry.pose

    override fun run() {
        if (!enable) return
        odometry.update(
            e0.shit() - lastE0,
            e1.shit() - lastE1,
            e2.shit() - lastE2
        )
        lastE0 = e0.shit()
        lastE1 = e1.shit()
        lastE2 = e2.shit()
    }

    override fun reset() {
        if (!enable) return
        odometry.clean()
        e0.reset(.0)
        e1.reset(.0)
        e2.reset(.0)
    }

    private fun Encoder.shit() = position * TRACK

    companion object {
        const val ENCODER_CPR = -4000.0
        const val TRACK = 0.039312
    }
}