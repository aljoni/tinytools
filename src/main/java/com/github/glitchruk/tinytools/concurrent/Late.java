package com.github.glitchruk.tinytools.concurrent;

/*
 * This file is part of TinyTools.
 *
 * TinyTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TinyTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TinyTools. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * A thread-safe utility class for late initialization.
 * <p>
 * The {@code Late} class provides a mechanism for deferred initialization
 * of an object. The value is set only once and can then be accessed multiple times.
 * This is particularly useful in scenarios where an expensive computation or
 * initialization should only be performed once, while maintaining thread safety
 * and ensuring efficient subsequent reads.
 * </p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Single initialization with {@code set(T value)}.</li>
 *     <li>Thread-safe access to the value using {@code get()}.</li>
 *     <li>Initialization state checking with {@code isInitialized()}.</li>
 * </ul>
 *
 * <p>
 * This class is designed for cases where a value is assigned exactly once and is expected
 * to be accessed multiple times. All methods are {@code synchronized}, ensuring
 * safe concurrent usage.
 * </p>
 *
 * <p><strong>Thread Safety:</strong> If the value type {@code T} is mutable, users must take care
 * to avoid modifying the value after it has been initialized, as such modifications are
 * not thread-safe. For simpler and safer usage, immutable types are recommended.
 * </p>
 *
 * <p><strong>Note:</strong> In the example below, the {@code Integer} type is used as
 * a wrapper for the primitive {@code int}. This is required because generics in Java
 * work only with reference types, not primitives. For most use cases, this distinction
 * is irrelevant, but it is worth noting when working with primitive values.
 * </p>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * public final class Person {
 *     private final Late<Integer> age = new Late<>();
 *
 *     public Person() {
 *         // Constructor does not set age; it will be initialized later
 *     }
 *
 *     public void initializeAge(final int initialAge) {
 *         // Initialize the age only once
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
 *         person.initializeAge(42); // Deferred initialization
 *         System.out.println("Age: " + person.getAge()); // Outputs: Age: 42
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of the value to be initialized late
 */
public class Late<T> {
    private T value;
    private boolean initialized;

    /**
     * Creates a new {@code Late} instance with no initial value.
     */
    public Late() {
        this.value = null;
        this.initialized = false;
    }

    /**
     * Initializes the value if it has not already been set.
     *
     * @param value the value to set
     * @throws IllegalStateException if the value has already been initialized
     */
    public synchronized void set(final T value) {
        if (initialized) {
            throw new IllegalStateException("Late value already initialized");
        }
        this.value = value;
        this.initialized = true;
    }

    /**
     * Retrieves the initialized value.
     *
     * @return the initialized value
     * @throws IllegalStateException if the value has not been initialized
     */
    public synchronized T get() {
        if (!initialized) {
            throw new IllegalStateException("Late value not initialized");
        }
        return value;
    }

    /**
     * Checks if the value of the memoized object has been set.
     *
     * @return {@code true} if the value has been set, {@code false} otherwise
     */
    public synchronized boolean isInitialized() {
        return initialized;
    }
}