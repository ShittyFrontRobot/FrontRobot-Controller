package org.mechdancer.common

import org.mechdancer.remote.modules.multicast.multicastListener
import org.mechdancer.remote.presets.RemoteHub
import org.mechdancer.remote.resources.Command
import java.io.DataInputStream

class RemoteDouble(private val id: Int, remote: RemoteHub) {
    companion object : Command {
        override val id: Byte = 32
    }

    var core = .0

    var onNewData = { _: Double -> }

    init {
        multicastListener { _, cmd, payload ->
            if (cmd != RemoteDouble.id) return@multicastListener
            DataInputStream(payload.inputStream()).apply {
                remote.components
                if (readInt() != id) return@multicastListener
                core = readDouble()
                onNewData(core)
            }
        }.also { remote.addDependency(it) }
    }

}