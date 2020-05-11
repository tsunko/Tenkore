package academy.hekiyou.tenkore.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents a plugin loader
 */
public interface Loader {
	
	/**
	 * Loads a plugin.
	 * @param path The path of the plugin to load
	 * @return The instance of the plugin
	 */
	@NotNull
	TenkorePlugin loadPlugin(@NotNull Path path);
	
	/**
	 * Unloads a plugin.
	 * @param pluginName The name of the plugin to unload
	 * @return The instance of the unloaded plugin
	 */
	@Nullable
	TenkorePlugin unloadPlugin(@NotNull String pluginName);
	
	/**
	 * @return A list containing all of the names of the currently loaded plugins
	 */
	@NotNull
	List<String> getLoadedPlugins();
	
	/**
	 * @return An array of file extensions this plugin loader is capable of.
	 */
	@NotNull
	String[] compatibleExtensions();
	
}
