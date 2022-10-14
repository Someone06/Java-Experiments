package dynamicClassLoader;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicClassLoaderTest {

    @Test
    public void itWorks() {
        final var loader = new LoadClassInPackage();
        final var classNames = loader.loadClasses("dynamicClassLoader.toLoad")
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        final var expected = Set.of("TestClass", "TestClassSubpackage");
        assertEquals(expected, classNames);
    }
}
