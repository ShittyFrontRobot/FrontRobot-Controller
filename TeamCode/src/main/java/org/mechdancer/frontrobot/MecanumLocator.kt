package org.mechdancer.frontrobot

import org.mechdancer.algebra.function.matrix.inverse
import org.mechdancer.algebra.function.matrix.times
import org.mechdancer.algebra.function.matrix.transpose
import org.mechdancer.algebra.function.vector.*
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.ListVector
import org.mechdancer.algebra.implement.vector.listVectorOf
import org.mechdancer.algebra.implement.vector.listVectorOfZero
import org.mechdancer.algebra.implement.vector.vector2DOf
import org.mechdancer.common.Pose2D
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.MotorWithEncoder
import org.mechdancer.ftclib.core.structure.monomeric.encoder
import org.mechdancer.ftclib.core.structure.monomeric.sensor.Encoder
import org.mechdancer.ftclib.util.AutoCallable
import org.mechdancer.ftclib.util.Resettable
import org.mechdancer.geometry.angle.toRad

class MecanumLocator :
    AbstractStructure("chassis", {
        encoder("LF") {
            cpr = MotorWithEncoder.Neverest40
            enable = true
        }
        encoder("LB") {
            cpr = -MotorWithEncoder.Neverest40
            enable = true
        }
        encoder("RF") {
            cpr = -MotorWithEncoder.Neverest40
            enable = true
        }
        encoder("RB") {
            cpr = MotorWithEncoder.Neverest40
            enable = true
        }

    }), Resettable, AutoCallable {

    @Inject("LF")
    private lateinit var lf: Encoder

    @Inject("LB")
    private lateinit var lb: Encoder

    @Inject("RF")
    private lateinit var rf: Encoder

    @Inject("RB")
    private lateinit var rb: Encoder

    private var lastEncoderValues: ListVector = listVectorOfZero(4)

    var pose: Pose2D = Pose2D.zero()
        private set

    fun showEncoderValues() = """
        
        LF: ${lf.position},
        LB: ${lb.position},
        RF: ${rf.position},
        RB: ${rb.position}
    """.trimIndent()

    override fun reset() {
        lf.reset(.0)
        lb.reset(.0)
        rf.reset(.0)
        rb.reset(.0)
        lastEncoderValues = listVectorOfZero(4)
        pose = Pose2D.zero()
    }


    override fun run() {
        val currentEncoderValues = listVectorOf(lf.position, lb.position, rf.position, rb.position) * Locator.TRACK
        val (x, y, w) = solverMatrix * (currentEncoderValues - lastEncoderValues)
        lastEncoderValues = currentEncoderValues
        pose = pose plusDelta Pose2D(vector2DOf(x, y), w.toRad())
    }

    override fun toString() = "${javaClass.simpleName} | Pose: $pose"

    companion object {
        private val coefficient = matrix {
            row(+1, -1, -TREAD_XY)
            row(+1, +1, -TREAD_XY)
            row(+1, +1, +TREAD_XY)
            row(+1, -1, +TREAD_XY)
        }

        private const val TREAD_XY = 0.277

        private val transposed = coefficient.transpose()

        private val solverMatrix = (transposed * coefficient).inverse() * transposed
    }

}