package com.github.glitchruk.tinytools.concurrent;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class MemoTest {

    private static final int NUM_THREADS = 16;
    private static final int NUM_ITERATIONS = 100;
    private static final String INITIAL_VALUE = "InitialValue";
    private static final String NEW_VALUE = "NewValue";

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentSet() throws InterruptedException {
        final Memo<String> memo = new Memo<>();
        final AtomicInteger exceptions = new AtomicInteger(0);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    try {
                        memo.set("Value-" + Thread.currentThread().threadId());
                    } catch (IllegalStateException e) {
                        exceptions.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertEquals(NUM_THREADS - 1, exceptions.get(), "Exactly one thread should successfully set the value, all others should throw exceptions");
        assertTrue(memo.isInitialized(), "Memo value should be initialized");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentSetIfAbsent() throws InterruptedException {
        final Memo<String> memo = new Memo<>();
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> memo.setIfAbsent("Value-" + Thread.currentThread().threadId()));
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertTrue(memo.isInitialized(), "Memo value should be initialized");
        assertNotNull(memo.get(), "Memo value should not be null");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentGetBeforeInitialization() throws InterruptedException {
        final Memo<String> memo = new Memo<>();
        final AtomicInteger exceptions = new AtomicInteger(0);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    try {
                        memo.get();
                    } catch (IllegalStateException e) {
                        exceptions.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertEquals(NUM_THREADS, exceptions.get(), "All threads should throw exceptions when calling get before initialization");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentGetAfterInitialization() throws InterruptedException {
        final Memo<String> memo = new Memo<>(INITIAL_VALUE);
        final AtomicInteger successCount = new AtomicInteger(0);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    String value = memo.get();
                    if (INITIAL_VALUE.equals(value)) {
                        successCount.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertEquals(NUM_THREADS, successCount.get(), "All threads should successfully retrieve the initialized value");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentResetAndSet() throws InterruptedException {
        final Memo<String> memo = new Memo<>();
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> memo.resetAndSet("Value-" + Thread.currentThread().threadId()));
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertTrue(memo.isInitialized(), "Memo value should be initialized");
        assertNotNull(memo.get(), "Memo value should not be null");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentReset() throws InterruptedException {
        final Memo<String> memo = new Memo<>(INITIAL_VALUE);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(memo::reset);
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertFalse(memo.isInitialized(), "Memo value should not be initialized after reset");
        assertThrows(IllegalStateException.class, memo::get, "Memo get should throw exception after reset");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testGetOrElse() {
        final Memo<String> memo = new Memo<>();
        assertEquals(NEW_VALUE, memo.getOrElse(NEW_VALUE), "GetOrElse should return default value when uninitialized");

        memo.set(INITIAL_VALUE);
        assertEquals(INITIAL_VALUE, memo.getOrElse(NEW_VALUE), "GetOrElse should return initialized value");
    }

    @Test
    public void testIsInitialized() {
        final Memo<String> memo = new Memo<>();
        assertFalse(memo.isInitialized(), "Memo value should not be initialized initially");

        memo.set(INITIAL_VALUE);
        assertTrue(memo.isInitialized(), "Memo value should be initialized after set");

        memo.reset();
        assertFalse(memo.isInitialized(), "Memo value should not be initialized after reset");
    }

    @Test
    public void testSetNull() {
        final Memo<String> memo = new Memo<>();
        assertThrows(NullPointerException.class, () -> memo.set(null), "Setting null should throw NullPointerException");
    }

    @Test
    public void testSetIfAbsentNull() {
        final Memo<String> memo = new Memo<>();
        assertThrows(NullPointerException.class, () -> memo.setIfAbsent(null), "Setting null should throw NullPointerException");
    }

    @Test
    public void testResetAndSetNull() {
        final Memo<String> memo = new Memo<>();
        assertThrows(NullPointerException.class, () -> memo.resetAndSet(null), "Resetting and setting null should throw NullPointerException");
    }
}
