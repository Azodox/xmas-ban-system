package fr.azodox.bansystem.config;

import fr.azodox.bansystem.BanSystem;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class TomlConfig {

    protected final String fileName;

    protected TomlConfig(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the toml config file based on the given name in the class constructor.
     * AUTOMATICALLY ADD TOML EXTENSION AT THE END OF FILE'S NAME.
     * @return toml config file
     */
    public TomlParseResult getConfig() {
        try{
            BanSystem banSystem = BanSystem.getInstance();

            Path config = Paths.get(banSystem.getDataDirectory() + File.separator + fileName + ".toml");
            TomlParseResult result = Toml.parse(config);
            result.errors().forEach(error -> System.err.println(error.toString()));
            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public String parseStringFromArray(String pathToArray){
        final TomlArray array = getConfig().getArray(pathToArray);
        if(array == null){
            throw new NullPointerException("Array is null, probably not the right path.");
        }

        if(array.containsArrays() || array.containsTables()){
            throw new IllegalArgumentException("Arrays cannot contains another array or any table.");
        }

        final StringBuilder sB = new StringBuilder();
        final List<Object> list = array.toList();
        for (Object o : list) {
            sB.append(o).append("\n");
        }

        sB.delete(sB.length() - 1, sB.length());
        return sB.toString();
    }
}
