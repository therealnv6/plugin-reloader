package io.github.devrawr.reloader

import io.github.devrawr.watcher.Watcher
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class ReloaderPlugin : JavaPlugin()
{
    override fun onEnable()
    {
        val root = this.dataFolder.parentFile

        Watcher.watchDirectory(root) {
            if (it.name.endsWith(".jar")) // if the file is a jar, it **could** most likely be a plugin
            {
                val pluginLoader = this.pluginLoader
                val description = pluginLoader.getPluginDescription(it)

                if (description != null) // right now, we know it has a plugin.yml.
                {
                    if (description.name == this.name)
                    {
                        // this plugin should not be able to be reloaded,
                        // if the plugin gets unloaded it can't execute any other code.
                        // so, return here.
                        return@watchDirectory
                    }

                    val plugin = Bukkit
                        .getPluginManager()
                        .getPlugin(
                            description.name
                        )

                    if (plugin == null) // if the plugin is null, it probably is a new plugin which never was in the directory.
                    {
                        pluginLoader.loadPlugin(it)
                    } else
                    {
                        // if we're here, it means the plugin had been previously loaded.
                        // this means the plugin could still be loaded, so we'll be
                        // unloading it here first.
                        if (plugin.isEnabled)
                        {
                            pluginLoader.disablePlugin(plugin)
                        }

                        // right now, the plugin should be unloaded, so we're enabling the plugin
                        // here now.
                        pluginLoader.enablePlugin(plugin)
                    }
                }
            }
        }
    }
}