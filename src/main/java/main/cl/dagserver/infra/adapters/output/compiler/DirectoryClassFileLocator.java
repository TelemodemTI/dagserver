package main.cl.dagserver.infra.adapters.output.compiler;

import net.bytebuddy.dynamic.ClassFileLocator;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryClassFileLocator implements ClassFileLocator {

    private final File directory;

    public DirectoryClassFileLocator(String directoryPath) {
        this.directory = new File(directoryPath);
    }

    @Override
    public Resolution locate(String className) throws IOException {
        String classFilePath = className.replace('.', File.separatorChar) + ".class";
        Path path = directory.toPath().resolve(classFilePath);
        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            return new Resolution.Explicit(bytes);
        }
        return new Resolution.Illegal(className);
    }

    @Override
    public void close() throws IOException {
        // No es necesario realizar ninguna operación de cierre para este ejemplo
    }
}
