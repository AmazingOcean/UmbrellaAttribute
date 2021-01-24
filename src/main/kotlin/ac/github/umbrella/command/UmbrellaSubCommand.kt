package ac.github.umbrella.command

import cc.kunss.bountyhuntercore.common.command.SubCommandAdapter
import org.bukkit.command.CommandSender

abstract class UmbrellaSubCommand(command: String, argsString: String?, description: String, priority: Double) :
    SubCommandAdapter(command, argsString, description, priority) {

    abstract override fun execute(sender: CommandSender, args: Array<out String>): Boolean

}