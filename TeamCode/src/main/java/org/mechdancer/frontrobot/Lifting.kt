package org.mechdancer.frontrobot

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mechdancer.ftclib.core.structure.composite.AbstractStructure
import org.mechdancer.ftclib.core.structure.injector.Inject
import org.mechdancer.ftclib.core.structure.monomeric.MotorWithEncoder
import org.mechdancer.ftclib.core.structure.monomeric.effector.Motor
import org.mechdancer.ftclib.core.structure.monomeric.motor
import org.mechdancer.ftclib.core.structure.monomeric.motorWithEncoder
import org.mechdancer.ftclib.util.Resettable
import kotlin.properties.Delegates

// 抬升装置结构定义
class Lifting : AbstractStructure("lifting", {

    // 两个内置编码器电机拉线
    motorWithEncoder("leftLift") {
        cpr = MotorWithEncoder.Neverest40
        enable = true
    }

    motorWithEncoder("rightLift") {
        cpr = MotorWithEncoder.Neverest40
        enable = true
    }

    // 两个电机控制护盾展开
    motor("leftExp") {
        enable = true
    }

    motor("rightExp") {
        enable = true
    }

}),Resettable {
    // 设备
    @Inject
    lateinit var leftLift: MotorWithEncoder
    @Inject
    lateinit var rightLift: MotorWithEncoder
    @Inject
    lateinit var leftExp: Motor
    @Inject
    lateinit var rightExp: Motor

    // 负责展开电机是否被占用
    @Volatile
    var expandBusy = false

    // 升降状态
    var liftState: LiftState by Delegates.observable(LiftState.Up) { _, old, new ->
        fun setMotors(target: Double) {
            leftLift.targetPosition = target
            rightLift.targetPosition = target
        }
        when {
            // 下降 -> 升起
            old == LiftState.Down && new == LiftState.Up                                      -> setMotors(2030.0)
            // 升起 -> 下降 需先收缩再下降
            old == LiftState.Up && new == LiftState.Down && expandState == ExpandState.Shrink -> setMotors(.0)
        }
    }

    //展开状态
    var expandState: ExpandState by Delegates.observable(ExpandState.Shrink) { _, old, new ->
        fun setMotors(isExpand: Boolean) {
            // 电机正忙
            if (expandBusy) return
            val powerA = if (isExpand) 0.4 else .0
            val powerB = if (isExpand) .0 else .4
            // 使用协程异步延时开环控制电机
            GlobalScope.launch {
                expandBusy = true
                leftExp.power = powerA
                rightExp.power = powerA
                delay(2000)
                leftExp.power = powerB
                rightExp.power = powerB
                expandBusy = false
            }
        }

        when {
            // 收缩 -> 展开 需先升起再展开
            old == ExpandState.Shrink && new == ExpandState.Expand && liftState == LiftState.Up -> setMotors(true)
            // 展开 -> 收缩 需先升起再收缩
            old == ExpandState.Expand && new == ExpandState.Shrink && liftState == LiftState.Up -> setMotors(false)
        }
    }

    // 重置状态
    override fun reset() {
        leftLift.mode=MotorWithEncoder.Mode.POSITION_CLOSE_LOOP
        rightLift.mode=MotorWithEncoder.Mode.POSITION_CLOSE_LOOP
        liftState=LiftState.Up
        expandState=ExpandState.Shrink
    }

    enum class LiftState { Up, Down }
    enum class ExpandState { Expand, Shrink }

}