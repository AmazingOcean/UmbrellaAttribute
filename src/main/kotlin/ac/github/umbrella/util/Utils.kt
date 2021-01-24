package ac.github.umbrella.util

import java.io.File

class Utils {

    companion object {

        fun listFiles(file : File) : ArrayList<File> {
            val arrayListOf = arrayListOf<File>()
            if (file.isDirectory) {
                file.listFiles()?.let { it -> it.forEach { subFile -> arrayListOf.addAll(listFiles(subFile)) } }
            } else {
                arrayListOf.add(file)
            }
            return arrayListOf
        }
    }

}