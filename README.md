# TinyTools

![Version](https://img.shields.io/badge/Version-0.1.0-blue)
![License: GPLv3](https://img.shields.io/badge/License-GPLv3-blue.svg)

A collection of small, useful tools for Java development. TinyTools aims to provide lightweight utilities that simplify
common programming tasks, focusing on thread safety, simplicity, and ease of use.

## License

This project is licensed under the GPLv3 license. See the [LICENSE](LICENSE) file for more information.

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

### Lazy\<T\>

The `Lazy` class allows for deferred initialization of an object, where the value is only set once and accessed multiple
times. This is useful in cases where an expensive computation or initialization needs to be deferred until the first
access, while ensuring thread safety and efficient access for subsequent reads.

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