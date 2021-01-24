package ac.github.umbrella.internal.language

import ac.github.umbrella.UmbrellaAttribute
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class LanguageFactory {

    lateinit var dataFolder : File
    var defaultFiles = arrayOf("zhcn.yml")

    fun initLanguage() : Language {
        dataFolder = File(UmbrellaAttribute.instance.dataFolder,"language")
        if (!dataFolder.exists()) {
            defaultFiles.forEach {
                UmbrellaAttribute.instance.saveResource("language/$it",false)
            }
        }
        val languageString = UmbrellaAttribute.instance.config.getString("LANGUAGE", "zhcn").replace(".yml","") + ".yml"
        var file = File(dataFolder, languageString)

        if (!file.exists()) {
            UmbrellaAttribute.instance.saveResource("language/${defaultFiles[0]}",false)
            file = File(dataFolder, defaultFiles[0])
        }
        return Language(YamlConfiguration.loadConfiguration(file))
    }

}