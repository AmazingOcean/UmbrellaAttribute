package ac.github.umbrella.command.sub

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.command.UmbrellaSubCommand
import ac.github.umbrella.internal.item.ItemFactory
import ac.github.umbrella.internal.item.message
import cc.kunss.bountyhuntercore.common.container.Param
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GiveCommand : UmbrellaSubCommand("give", "[player] [item] [amount] [option(json)]", "给予物品", 20.0) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val player = if (args[1] == "me") sender as Player else Bukkit.getPlayer(args[1])
        if (player == null) {
            "PLUGIN.COMMAND.PLAYER-NOT-ONLINE"
                .message(Param.newInstance().add("player", args[1]))
                .sendSender(sender)
            return false
        }
        val key = args[2]
        val itemConfigSection = ItemFactory.instance().getItemConfigSection(key)
        if (itemConfigSection == null) {
            "PLUGIN.COMMAND.ITEM-CONFIG-INVALID"
                .message(Param.newInstance().add("key", key))
                .sendSender(sender)
            return false
        }
        var amount = 1
        //give player item amount option
        if (args.size == 4) {
            if (!args[3].matches("[0-9]+".toRegex())) {
                "PLUGIN.COMMAND.AMOUNT-NOT-REGEX-MATCH"
                    .message()
                    .sendSender(sender)
                return false
            }
            amount = args[3].toInt()
        }
        var optionMap = mutableMapOf<String, String>()
        if (args.size >= 5) {
            var json = args.toList().subList(4, args.size - 1).joinToString()
            if (UmbrellaAttribute.hooks.contains("PlaceholderAPI")) {
                json = PlaceholderAPI.setPlaceholders(player, json)
            }
            optionMap =
                UmbrellaAttribute.instance.gson.fromJson(json, MutableMap::class.java) as MutableMap<String, String>
        }

        var successInt = 0
        for (index in 0 until amount) {
            val item = ItemFactory.instance().getItem(player, key, optionMap)
            if (item != null) {
                player.inventory.addItem(item)
                successInt++
            }
        }
        if (successInt == 0) {
            "PLUGIN.COMMAND.GIVE-ITEM-ERROR".message().sendSender(sender)
        } else {
            val param = Param.newInstance().add("item", key).add("amount", amount).add("player", player.name)
            if (sender is Player && sender.name != player.name) {
                "PLUGIN.COMMAND.GIVE-ITEM-BY-SENDER".message(param).sendSender(sender)
            }
            "PLUGIN.COMMAND.GIVE-ITEM-BY-PLAYER".message(param).sendSender(sender)

            return true
        }
        return false
    }

}