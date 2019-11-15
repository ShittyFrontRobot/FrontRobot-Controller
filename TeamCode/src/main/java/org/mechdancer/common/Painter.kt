package org.mechdancer.common

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.firstinspires.ftc.robotcore.internal.network.DeviceNameManager
import org.firstinspires.ftc.robotcore.internal.network.DeviceNameManagerFactory
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.presets.remoteHub
import org.mechdancer.remote.protocol.writeEnd
import org.mechdancer.remote.resources.Command
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

private object PaintCommand : Command {
    override val id = 6.toByte()
}

val remote = remoteHub("robot-${DeviceNameManagerFactory.getInstance().deviceName}").also {
    it.openAllNetworks()
    GlobalScope.launch {
        while (isActive)
            it()
    }
}

/**
 * 画任意内容
 */
fun RemoteHub.paint(
    topic: String,
    block: ByteArrayOutputStream.() -> Unit
) {
    ByteArrayOutputStream()
        .also { stream ->
            stream.writeEnd(topic)
            stream.block()
        }
        .toByteArray()
        .let { broadcast(PaintCommand, it) }
}

/**
 * 画一维信号
 */
fun RemoteHub.paint(
    topic: String,
    value: Double
) = paint(topic) {
    DataOutputStream(this).apply {
        writeByte(1)
        writeDouble(value)
    }
}

/**
 * 画二维信号
 */
fun RemoteHub.paint(
    topic: String,
    x: Double,
    y: Double
) = paint(topic) {
    DataOutputStream(this).apply {
        writeByte(2)
        writeDouble(x)
        writeDouble(y)
    }
}

/**
 * 画位姿信号
 */
fun RemoteHub.paint(
    topic: String,
    x: Double,
    y: Double,
    theta: Double
) = paint(topic) {
    DataOutputStream(this).apply {
        writeByte(3)
        writeDouble(x)
        writeDouble(y)
        writeDouble(theta)
    }
}

/**
 * 画位姿信号
 */
fun RemoteHub.paintPose(
    topic: String,
    pose: Pose2D
) = paint(topic) {
    DataOutputStream(this).apply {
        writeByte(3)
        writeDouble(pose.p.x)
        writeDouble(pose.p.y)
        writeDouble(pose.d.asRadian())
    }
}
