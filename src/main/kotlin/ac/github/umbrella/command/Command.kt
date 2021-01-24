package ac.github.umbrella.command

import ac.github.umbrella.UmbrellaAttribute
import ac.github.umbrella.command.sub.GiveCommand
import ac.github.umbrella.command.sub.TestCommand
import ac.github.umbrella.internal.language.Language
import cc.kunss.bountyhuntercore.common.command.CommandAdapter

class Command : CommandAdapter(UmbrellaAttribute.instance, Language.instance,"ua"){

    companion object {
        lateinit var instance : Command
    }

    init {

        registerSub(TestCommand())
        registerSub(GiveCommand())

    }
}