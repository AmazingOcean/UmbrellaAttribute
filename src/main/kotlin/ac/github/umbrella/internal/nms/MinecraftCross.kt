package ac.github.umbrella.internal.nms

import org.bukkit.entity.Player

interface MinecraftCross {


    fun sendTitle(player: Player,title : String,subTitle : String,a : Long,b : Long,c : Long)

    fun sendActionBar(player: Player,title: String)

}