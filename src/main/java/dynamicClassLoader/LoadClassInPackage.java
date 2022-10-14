package dynamicClassLoader;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class LoadClassInPackage {

    private final Queue<FileAndName> queue = Collections.asLifoQueue(
            new LinkedList<>());

    private static Class<?> loadClass(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException ignored) {
            throw new AssertionError(
                    "Could not load class '" + className + "'.");
        }
    }

    public Stream<Class<?>> loadClasses(final String packageName) {
        final var classLoader = Thread.currentThread().getContextClassLoader();
        final var path = packageName.replace('.', '/');

        final Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }

        return Stream.generate(
                        () -> resources.hasMoreElements() ?
                              resources.nextElement()
                                                          : null)
                .takeWhile(Objects::nonNull)
                .map(URL::getFile)
                .map(File::new)
                .map(file -> new FileAndName(file, packageName))
                .flatMap(this::findClassesLazy);
    }

    private Optional<FileAndName> generateClassFileAndName() {
        FileAndName fileAndName = null;
        while (!queue.isEmpty() && (fileAndName = queue.remove()).file()
                .isDirectory()) {
            final var files = fileAndName.file().listFiles();
            assert files != null;
            for (final var file : files) {
                queue.add(new FileAndName(file, fileAndName.name() + '.'
                        + file.getName()));
            }
            fileAndName = null;
        }

        return Optional.ofNullable(fileAndName);
    }

    private Stream<Class<?>> findClassesLazy(final FileAndName in) {
        queue.add(in);

        return Stream.generate(this::generateClassFileAndName)
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .filter(FileAndName::refersToClass)
                .map(FileAndName::toClassName)
                .map(LoadClassInPackage::loadClass);
    }

    private record FileAndName(File file, String name) {
        public FileAndName(final File file, final String name) {
            this.file = requireNonNull(file);
            this.name = requireNonNull(name);
        }

        public boolean refersToClass() {
            return file().getName().endsWith(".class");
        }

        public String toClassName() {
            // Remove the '.class' file suffix.
            return name().substring(0, name().length() - 6);
        }
    }
}
