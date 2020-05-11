package academy.hekiyou.tenkore;

import academy.hekiyou.door.model.Register;
import academy.hekiyou.tenkore.plugin.LoaderManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface Tenkore {
    
    default void loadPlugins(Path root){
        try (Stream<Path> children = Files.walk(root, 1)) {
            children.filter(Files::isRegularFile)
                    .forEach(LoaderManager::loadPlugin);
        } catch(IOException exc) {
            throw new IllegalStateException("failed to walk plugin folder " + root, exc);
        }
    }
    
    Register getCommandRegister();
    
}
