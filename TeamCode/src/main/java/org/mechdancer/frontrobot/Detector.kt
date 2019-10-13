package org.mechdancer.frontrobot

import org.mechdancer.algebra.implement.matrix.special.DiagonalMatrix
import org.mechdancer.common.Vector3D
import org.mechdancer.common.eulerToMatrix
import org.mechdancer.common.toTrans
import org.mechdancer.ftclib.core.opmode.OpModeWithRobot
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.util.OpModeLifecycle
import org.mechdancer.geometry.transformation.Transformation

class Detector : AbstractStructure("Detector"),
        OpModeLifecycle.Initialize<FrontRobot> {

    var tagToCamTrans:Transformation= Transformation(DiagonalMatrix(List(1) {1.0}))

    //TODO 参数
    private val camToBaselinkTrans:Transformation= Transformation.fromInhomogeneous(
            eulerToMatrix(.0,.0,.0),
            Vector3D(.0,.0,.0)
    )


    private lateinit var openMVCam: OpenMVCam
    override fun init(opMode: OpModeWithRobot<FrontRobot>) {
        openMVCam=opMode.hardwareMap["cam"]as OpenMVCam
    }

    override fun run() {
        super.run()
        openMVCam.getData().toTrans()

    }

    fun getAprilTagToBaselinkTrans()=camToBaselinkTrans*tagToCamTrans

}