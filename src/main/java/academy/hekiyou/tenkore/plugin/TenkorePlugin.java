package academy.hekiyou.tenkore.plugin;

import academy.hekiyou.tenkore.Tenkore;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * An abstract class for plugins that utilize Tenkore to extend.
 */
public abstract class TenkorePlugin {

	private Tenkore core;
	private String name;
	private Path storageDir;
	private Logger logger;
	
	/**
	 * Entry for any TenkorePlugins; allows initialization of resources
	 * and etc. for any plugin later on.
	 */
	public void enable(){};
	
	/**
	 * Exit for any TenkorePlugin; perform clean-up here.
	 */
	public void disable(){};
	
	@NotNull
	public Logger getLogger(){
		if(logger == null)
			logger = Logger.getLogger(getName());
		return logger;
	}
	
	@NotNull
	public String getName(){
		return name;
	}
	
	@NotNull
	public Path getStorageDir(){
		if(storageDir == null)
			storageDir = Paths.get("", "plugins", getName());
		return storageDir;
	}

	@NotNull
	public Tenkore getCore() {
		return core;
	}

	/**
	 * Initializes the plugin with the core plugin and gives it a name.
	 * It is expected that this function will be called while loading.
	 * @param core The core plugin
	 * @param name The name of this plugin
	 */
	public void __init__(@NotNull Tenkore core, @NotNull String name){
		this.core = core;
		this.name = name;
	}
	
}
