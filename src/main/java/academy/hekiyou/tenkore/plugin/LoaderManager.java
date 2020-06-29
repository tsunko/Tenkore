package academy.hekiyou.tenkore.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A general manager for handling plugin loaders and their association by extensions.
 */
public class LoaderManager {
    
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());
    private static final Map<PathMatcher, Loader> loaders = new ConcurrentHashMap<>();
    private static final Map<String, PathMatcher> matchers = new ConcurrentHashMap<>();
    
    /**
     * Registers a loader as a part of Tenkore without overwriting other loaders
     * @param klass The class to load as a loader
     * @return <code>true</code> if the loader was successfully registered for at least 1 extension; <code>false</code> otherwise
     */
    public static boolean registerLoader(@NotNull Class<? extends Loader> klass){
        return registerLoader(klass, false);
    }
    
    /**
     * Registers a loader as a part of Tenkore
     * @param klass The class to load as a loader
     * @param overwrite If <code>true</code>, we overwrite conflicting loaders
     * @return <code>true</code> if the loader was successfully registered for at least 1 extension; <code>false</code> otherwise
     */
    public static boolean registerLoader(@NotNull Class<? extends Loader> klass, boolean overwrite){
        Loader loader;
        try {
            loader = klass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exc){
            exc.printStackTrace();
            return false;
        }
        
        boolean loaded = false;
        
        for(String ext : loader.compatibleExtensions()){
            if(matchers.containsKey(ext)){
                PathMatcher oldKey = matchers.get(ext);
                Loader old = loaders.get(oldKey);
                if(overwrite && old != null){
                    LOGGER.info("Overwriting old loader for " + ext + " (" + old.getClass().getCanonicalName() + ") with " + klass.getCanonicalName());
                    loaders.put(oldKey, loader);
                } else if(!overwrite && old != null) {
                    LOGGER.warning("Extension " + ext + " already loaded (associated with " + old.getClass().getCanonicalName() + ")");
                }
            } else {
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*." + ext);
                matchers.put(ext, matcher);
                loaders.put(matcher, loader);
                LOGGER.info("Registered loader " + loader.getClass().getSimpleName() + " for ." + ext);
                loaded = true;
            }
        }
        
        return loaded;
    }
    
    /**
     * @return An array containing all loaders registered
     */
    @NotNull
    public static Loader[] getLoaders(){
        return loaders.values().toArray(new Loader[0]);
    }
    
    /**
     * @param ext The extension for to try to look for
     * @return The Loader instance or <code>null</code> if no loader was found with the extension
     */
    @Nullable
    public static Loader getLoaderFor(String ext){
        PathMatcher key = matchers.get(ext);
        if(key == null) return null;
        return loaders.get(key);
    }
    
    /**
     * Tries to find and load a plugin based on extension
     * @param path The path to the plugin file
     * @return An instance of the plugin
     */
    @Nullable
    public static TenkorePlugin loadPlugin(Path path){
        for(Map.Entry<PathMatcher, Loader> entry : loaders.entrySet()){
            if(entry.getKey().matches(path)){
                return entry.getValue().loadPlugin(path);
            }
        }
        if(!path.toString().endsWith(".jar"))
            LOGGER.warning("Don't know how to load " + path);
        return null;
    }
    
    /**
     * Unregisters a loader and no longer allows plugins to load with the given loader.
     * @param klass The class of the loader to unload
     * @return <code>true</code> if the loader was successfully unregistered; <code>false</code> otherwise.
     * @apiNote The return value can return <code>false</code> if there was no extension associated to begin with
     */
    public static boolean unregisterLoader(@NotNull Class<? extends Loader> klass){
        boolean removed = false;
        Iterator<Map.Entry<String, PathMatcher>> iter = matchers.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, PathMatcher> entry = iter.next();
            String extension = entry.getKey();
            PathMatcher matcher = entry.getValue();
            Loader loader = loaders.get(matcher);
            
            if(loader == null)
                continue;
            
            if(loader.getClass().getName().equals(klass.getName())){
                iter.remove();
                matchers.remove(extension);
                removed = true;
            }
        }
        return removed;
    }
    
}
