package com.github.glitchruk.tinytools.concurrent;

/**
 * A thread-safe utility class for lazy initialization.
 * <p>
 * The {@code Lazy} class allows for deferred initialization of an object,
 * where the value is only set once and accessed multiple times. This is useful
 * in cases where an expensive computation or initialization needs to be deferred
 * until the first access, while ensuring thread safety and efficient access for
 * subsequent reads.
 * </p>
 *
 * <p>
 * The {@code set(T value)} method initializes the value and can only be called once.
 * Any subsequent calls to {@code set} will throw an {@link IllegalStateException}.
 * The {@code get()} method provides access to the initialized value, and will throw
 * an {@link IllegalStateException} if {@code set} has not been called before
 * the first {@code get()} invocation. This class is designed to be used in cases
 * where a value is assigned exactly once, and is expected to be read multiple times.
 * </p>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * public final class Person {
 *     private final Lazy<Integer> age = new Lazy<>();
 *
 *     public Person() {
 *         // Constructor does not set age; it will be set later
 *     }
 *
 *     public void initializeAge(final int initialAge) {
 *         // Age is set once here, outside the constructor
 *         age.set(initialAge);
 *     }
 *
 *     public int getAge() {
 *         return age.get();
 *     }
 * }
 *
 * public class Main {
 *     public static void main(String[] args) {
 *         Person person = new Person();
 *         person.initializeAge(25); // Deferred initialization
 *         System.out.println("Age: " + person.getAge()); // Outputs: Age: 25
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of the value to be lazily initialized
 */
public class Lazy<T> {
    private T value;
    private boolean initialized;

    public Lazy() {
        this.value = null;
        this.initialized = false;
    }

    public synchronized T get() {
        if (!initialized) {
            throw new IllegalStateException("Lazy value not initialized");
        }
        return value;
    }

    public synchronized void set(final T value) {
        if (initialized) {
            throw new IllegalStateException("Lazy value already initialized");
        }
        this.value = value;
        this.initialized = true;
    }
}
