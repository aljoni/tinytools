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

import java.util.Objects;

/**
 * A thread-safe utility class for memoization.
 * <p>
 * The {@code Memo} class allows for deferred initialization and reuse of a value,
 * ensuring thread-safe access and the ability to reset and reinitialize the value
 * when needed.
 * </p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Single initialization with {@code set(T value)}.</li>
 *     <li>Conditional initialization using {@code setIfAbsent(T value)}.</li>
 *     <li>Safe access with {@code get()} and fallback support via {@code getOrElse(T defaultValue)}.</li>
 *     <li>Reinitialization support with {@code reset()} or {@code resetAndSet(T value)}.</li>
 *     <li>Initialization state checking with {@code isInitialized()}.</li>
 * </ul>
 *
 * <p>
 * This class is particularly useful for scenarios where a value must be initialized
 * only once, safely accessed across threads, and occasionally reset and reused.
 * </p>
 *
 * <p><strong>Thread Safety:</strong> All methods in this class are synchronized,
 * ensuring safe concurrent access. If the value type {@code T} is mutable, users must
 * take care to avoid modifying it after initialization, as such modifications are
 * not thread-safe. For simpler usage, immutable types are recommended.
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
 *     private final Memo<Integer> age = new Memo<>();
 *
 *     public Person() {
 *         // Constructor does not set age; it will be initialized later
 *     }
 *
 *     public void setAge(final int initialAge) {
 *         // Set the age initially
 *         age.set(initialAge);
 *     }
 *
 *     public void updateAge(final int newAge) {
 *         // Reset and set a new value
 *         age.resetAndSet(newAge);
 *     }
 *
 *     public boolean hasAge() {
 *         // Check if the age has been initialized
 *         return age.isInitialized();
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
 *
 *         if (!person.hasAge()) {
 *             System.out.println("Age not set yet!");
 *             person.setAge(42);
 *         }
 *
 *         System.out.println("Age: " + person.getAge()); // Outputs: Age: 42
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of the value to be memoized
 */
public class Memo<T> {
    private T value;
    private boolean initialized;

    /**
     * Constructs a new {@code Memo} instance with the specified initial value.
     *
     * @param value the initial value to be memoized
     */
    public Memo(final T value) {
        this.value = value;
        this.initialized = true;
    }

    /**
     * Constructs a new {@code Memo} instance with no initial value.
     */
    public Memo() {
        this.value = null;
        this.initialized = false;
    }

    /**
     * Sets the value of the memoized object. This method can only be called once,
     * and subsequent attempts to set the value will throw an {@link IllegalStateException}.
     *
     * @param value the value to be memoized, cannot be null
     * @throws IllegalStateException if the value has already been set
     */
    public synchronized void set(final T value) {
        Objects.requireNonNull(value, "Memo value cannot be null");
        if (initialized) {
            throw new IllegalStateException("Memo value already initialized");
        }
        this.value = value;
        this.initialized = true;
    }

    /**
     * Sets the value of the memoized object if it has not been set yet.
     * If the value has already been set, the existing value is returned.
     *
     * @param value the value to be memoized, cannot be null
     * @return the existing value if it has already been set, otherwise the new value
     */
    public synchronized T setIfAbsent(final T value) {
        Objects.requireNonNull(value, "Memo value cannot be null");
        if (initialized) {
            return value;
        }
        this.value = value;
        this.initialized = true;
        return value;
    }

    /**
     * Resets the value of the memoized object and sets a new value.
     * This method can be used to reinitialize the value when needed.
     *
     * @param value the new value to be memoized, cannot be null
     */
    public synchronized void resetAndSet(final T value) {
        Objects.requireNonNull(value, "Memo value cannot be null");
        this.value = value;
        this.initialized = true;
    }

    /**
     * Retrieves the value of the memoized object.
     * If the value has not been set yet, an {@link IllegalStateException} is thrown.
     *
     * @return the value of the memoized object
     * @throws IllegalStateException if the value has not been set yet
     */
    public synchronized T get() {
        if (!initialized) {
            throw new IllegalStateException("Memo value not initialized");
        }
        return value;
    }

    /**
     * Retrieves the value of the memoized object, or returns the specified default value
     * if the value has not been set yet.
     *
     * @param defaultValue the default value to be returned if the value has not been set yet
     * @return the value of the memoized object, or the default value if it has not been set yet
     */
    public synchronized T getOrElse(final T defaultValue) {
        if (!initialized) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Resets the value of the memoized object to {@code null}.
     * This method can be used to reuse the {@code Memo} instance for another initialization cycle.
     */
    public synchronized void reset() {
        this.value = null;
        this.initialized = false;
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
