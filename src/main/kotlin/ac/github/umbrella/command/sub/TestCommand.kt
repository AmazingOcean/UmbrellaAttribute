package ac.github.umbrella.command.sub

import ac.github.umbrella.api.EntityOperation
import ac.github.umbrella.command.UmbrellaSubCommand
import cc.kunss.bountyhuntercore.common.command.SubCommandAdapter
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand : UmbrellaSubCommand("test",null,"测试",30.0){


    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {

        sender as Player
        EntityOperation.updateLivingEntity(sender)

        return true
    }


}