# Java Experiments

This project contains a collection of Java code snippets that each explore some part of the Java language.
Because this is just me having fun with Java in my spare time, the code is only sparsely commented and mostly untested.
If you're looking to grab any code from this repository make sure to at least test the code before using it (and report
any bugs to me, so I can fix them).

## Noteworthy content

- The Rust `Result` type re-implemented in Java, using a sealed interface.
- The `Intern` class can be used to intern objects of any class, which allows for interned objects to be compared in
  O(1). Some garbage collection is implemented to ensure all objects are reclaimed when they are no longer in use.
- A variation of the visitor patter that allows to visit any class (even if you don't own it) by using Reflections.
- A deserialization framework that allows deserializing strings to any type by providing a custom deserializer.
  New deserializers can be easily added as plugins, that are dynamically loaded from the classpath during runtime as
  needed.
- A framework for managing configuration values, where each key is associated with a default value, that can be
  overwritten by a config file or an environment variable. The framework currently only supports Java Property files,
  but can easily be extended to cover other kind of file formats. The framework also allows for configuration values to
  be of arbitrary type, not just strings.

# License

Licensed under the terms of the Apache 2.0 license. See [`LICENSE`](LICENSE) and [`NOTICE`](NOTICE).


