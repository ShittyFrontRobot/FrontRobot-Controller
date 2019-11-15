package org.mechdancer.common

import org.mechdancer.ftclib.algorithm.PID
import org.mechdancer.remote.modules.multicast.multicastListener
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import java.io.DataInputStream

class RemotePID(private val id: Int, remote: RemoteHub) {
    companion object : Command {
        override val id: Byte = 9
    }

    var core = PID.zero()

    var onReset = {}

    init {
        multicastListener(RemotePID) { _, _, payload ->
            DataInputStream(payload.inputStream()).apply {
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

