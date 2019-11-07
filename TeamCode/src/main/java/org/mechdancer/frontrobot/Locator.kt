package org.mechdancer.frontrobot

import org.mechdancer.common.OmniDirectionOdometry
import org.mechdancer.common.Pose2D
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.encoder
import org.mechdancer.ftclib.core.structure.monomeric.sensor.Encoder
import org.mechdancer.ftclib.util.AutoCallable
import org.mechdancer.ftclib.util.Resettable

class Locator : AbstractStructure("locator", {
    encoder("e0") {
        enable = true
    }
    encoder("e1") {
        enable = true
    }
    encoder("e2") {
        enable = true
    }
}), AutoCallable, Resettable {

    @Inject
    private lateinit var e0: Encoder
    @Inject
    private lateinit var e1: Encoder
    @Inject
    private lateinit var e2: Encoder

    private var lastE0 = .0
    private var lastE1 = .0
    private var lastE2 = .0

    private val odometry: OmniDirectionOdometry = OmniDirectionOdometry(
        Pose2D(),
        Pose2D(),
        Pose2D()
    )

    val pose
        get() = odometry.pose

    override fun run() {
        return
        odometry.update(
            e0.position - lastE0,
            e1.position - lastE1,
            e2.position - lastE2
        )
        lastE0 = e0.position
        lastE1 = e1.position
        lastE2 = e2.position
    }

    override fun reset() {
        return
        odometry.clean()
    }
}