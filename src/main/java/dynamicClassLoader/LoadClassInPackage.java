package dynamicClassLoader;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class LoadClassInPackage {

    private final Queue<FileAndPackageName> queue = Collections.asLifoQueue(
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
                        () -> resources.hasMoreElements()
                              ? resources.nextElement()
                              : null)
                .takeWhile(Objects::nonNull)
                .map(URL::getFile)
                .map(File::new)
                .map(file -> new FileAndPackageName(file, packageName))
                .flatMap(this::findClassesLazy);
    }

    private Optional<FileAndPackageName> generateClass() {
        FileAndPackageName fileAndPackageName = null;
        while (!queue.isEmpty() && (fileAndPackageName = queue.remove()).file()
                .isDirectory()) {
            final var files = fileAndPackageName.file().listFiles();
            assert files != null;
            for (final var file : files) {
                queue.add(new FileAndPackageName(
                        file,
                        fileAndPackageName.packageName() + '.' + file.getName()
                ));
            }
            fileAndPackageName = null;
        }

        return Optional.ofNullable(fileAndPackageName);
    }

    private Stream<Class<?>> findClassesLazy(final FileAndPackageName in) {
        queue.add(in);

        return Stream.generate(this::generateClass)
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .filter(FileAndPackageName::refersToClass)
                .map(FileAndPackageName::toClassName)
                .map(LoadClassInPackage::loadClass);
    }

    private record FileAndPackageName(File file, String packageName) {
        public FileAndPackageName(final File file, final String packageName) {
            this.file = requireNonNull(file);
            this.packageName = requireNonNull(packageName);
        }

        public String toPackageName() {
            return packageName() + '.' + file().getName();
        }

        public boolean refersToClass() {
            return file().getName().endsWith(".class");
        }

        public String toClassName() {
            // Remove the '.class' file suffix.
             return packageName().substring(0, packageName().length() - 6);
        }
    }
}
