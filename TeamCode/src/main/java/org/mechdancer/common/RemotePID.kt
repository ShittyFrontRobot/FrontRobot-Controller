package org.mechdancer.common

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DynamicScope
import org.mechdancer.dependency.plusAssign
import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.remote.modules.multicast.multicastListener
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import java.io.DataInputStream

class RemotePID(private val id: Int, remote: RemoteHub) {
    companion object {
        private val pidCmd = object : Command {
            override val id: Byte = 9
        }

        private fun RemoteHub.addDependency(component: Component) {
            (RemoteHub::class.java.declaredFields.find { it.name.contains("scope") }!!.also {
                it.isAccessible = true
            }[this] as DynamicScope) += component
        }
    }

    var core = PID.zero()

    var onReset = {}

    init {
        multicastListener { _, cmd, payload ->
            if (cmd != pidCmd.id) return@multicastListener
            DataInputStream(payload.inputStream()).apply {
                remote.components
                if (readInt() != id) return@multicastListener
                with(core) {
                    k = readDouble()
                    ki = readDouble()
                    kd = readDouble()
                    integrateArea = readDouble()
                    deadArea = readDouble()
                    if (readBoolean()) {
                        core.reset()
                        onReset()
                    }
                }
            }
        }.also { remote.addDependency(it) }
    }
}

