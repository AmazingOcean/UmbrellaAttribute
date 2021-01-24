package ac.github.umbrella.internal.`object`

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.command.Command
import ac.github.umbrella.command.UmbrellaSubCommand
import ac.github.umbrella.internal.hook.HolographicDisplaysHook
import cc.kunss.bountyhuntercore.common.command.SubCommandAdapter
import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class HologramStack(
    val def: Int,
    val auto: Int,
    val sourceLocation: Location,
    val animationSpeed: Int
) : Stack<Hologram>() {

    companion object {
        val hologramCache = mutableMapOf<Hologram, Long>()
    }

    var amount = 0

    init {
        create(def)

        Command.instance.registerSub(object : UmbrellaSubCommand("gc","<info/gc>","[查看/清理]内存",30.0){

            override fun execute(sender: CommandSender, args: Array<out String>): Boolean {

                sender.sendMessage("§f[§bHole Memory§f]")
                sender.sendMessage("  §3- Hole Amount: §f$amount")


                return true
            }

        })

        object : BukkitRunnable() {
            override fun run() {
                val distance = 0.1 / animationSpeed
                val closes = arrayListOf<Hologram>()
                hologramCache.forEach { entry ->
                    if (entry.value + (animationSpeed * 1000) < System.currentTimeMillis()) {
                        closes.add(entry.key)
                    } else {
                        entry.key.teleport(entry.key.location.add(0.0, distance, 0.0))
                    }
                }
                if (closes.isNotEmpty()) closes.forEach { it.gc() }
            }
        }.runTaskTimerAsynchronously(UmbrellaAttribute.instance, 20, 2)

    }

    fun gc() {
        if (size > def) {
            val i = size - def
            for (index in 0 until i) {
                this.pop()
            }
        }
    }

    fun create(amount: Int) {
        for (index in 0 until amount) {
            push(HologramsAPI.createHologram(UmbrellaAttribute.instance, sourceLocation))
            this@HologramStack.amount++
        }
    }

    override fun pop(): Hologram {
        return if (empty()) {
            create(auto)
            pop()
        } else {
            super.pop()
        }
    }

    fun recovery(hologram: Hologram) {
        this.push(hologram)
        hologram.teleport(sourceLocation)
    }
}

fun Hologram.register() {
    HologramStack.hologramCache[this] = System.currentTimeMillis()
}

fun Hologram.gc() {
    this.clearLines()
    HologramStack.hologramCache.remove(this)
    HolographicDisplaysHook.hologramStack.recovery(this@gc)
}