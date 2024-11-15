# TinyTools

![Version](https://img.shields.io/badge/Version-0.1.0-blue)
![License: LGPLv3](https://img.shields.io/badge/License-LGPLv3-blue.svg)

A collection of small, useful tools for Java development. TinyTools aims to provide lightweight utilities that simplify
common programming tasks, focusing on thread safety, simplicity, and ease of use.

## License

This project is licensed under the LGPLv3 license. See the [LICENSE](LICENSE) file for more information.

## Installation

To use TinyTools, use the following steps for either Maven or Gradle:

### 1. Clone the repository

```bash
git clone https://github.com/glitchruk/tinytools.git
```

### 2. Build the project

Navigate to the project directory:

```bash
cd tinytools
```

#### Using Maven

```bash
mvn clean package install
```

#### Using Gradle

```bash
gradle clean build
```

### 3. Add the dependency

#### Using Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.github.glitchruk</groupId>
    <artifactId>tinytools</artifactId>
    <version>0.1.0</version>
</dependency>
```

#### Using Gradle

1. Copy the built JAR file from the `tinytools/build/libs` directory to your project's `libs` directory.
2. Add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation files('libs/tinytools-0.1.0.jar')
}
```

## Components

A list of the components in this library.

| Class                                                                          | Description                                          |
|--------------------------------------------------------------------------------|------------------------------------------------------|
| [`Lazy<T>`](src/main/java/com/github/glitchruk/tinytools/concurrent/Lazy.java) | A thread-safe utility class for lazy initialization. |
| [`Memo<T>`](src/main/java/com/github/glitchruk/tinytools/concurrent/Memo.java) | A thread-safe utility class for memoization.         |

### Lazy\<T\>

The `Lazy` class allows for deferred initialization of an object, where the value is only set once and accessed multiple
times. This is useful in cases where an expensive computation or initialization needs to be deferred until the first
access, while ensuring thread safety and efficient access for subsequent reads.

#### Key Features

- Single initialization with `set(T value)`.
- Thread-safe access to the value using `get()`.
- Initialization state checking with `isInitialized()`.

#### Example Usage

```java
public final class Person {
    private final Lazy<Integer> age = new Lazy<>();

    public Person() {
        // Constructor does not set age; it will be set later
    }

    public void initializeAge(final int initialAge) {
        // Age is set once here, outside the constructor
        age.set(initialAge);
    }

    public int getAge() {
        return age.get();
    }
}

public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        person.initializeAge(25); // Deferred initialization
        System.out.println("Age: " + person.getAge()); // Outputs: Age: 25
    }
}
```

### Memo\<T\>

The `Memo` class allows for deferred initialization and reuse of a value, ensuring thread-safe
access and the ability to reset and reinitialize the value when needed.

#### Key Features

- Single initialization with `set(T value)`.
- Conditional initialization using `setIfAbsent(T value)`.
- Safe access with `get()` and fallback support via `getOrElse(T defaultValue)`.
- Reinitialization support with `reset()` or `resetAndSet(T value)`.
- Initialization state checking with `isInitialized()`.

#### Example Usage

```java
public final class Person {
    private final Memo<Integer> age = new Memo<>();

    public Person() {
        // Constructor does not set age; it will be initialized later
    }

    public void setAge(final int initialAge) {
        // Set the age initially
        age.set(initialAge);
    }

    public void updateAge(final int newAge) {
        // Reset and set a new value
        age.resetAndSet(newAge);
    }

    public boolean hasAge() {
        // Check if the age has been initialized
        return age.isInitialized();
    }

    public int getAge() {
        return age.get();
    }
}

public class Main {
    public static void main(String[] args) {
        Person person = new Person();

        if (!person.hasAge()) {
            System.out.println("Age not set yet!");
            person.setAge(42);
        }

        System.out.println("Age: " + person.getAge()); // Outputs: Age: 42
    }
}
```