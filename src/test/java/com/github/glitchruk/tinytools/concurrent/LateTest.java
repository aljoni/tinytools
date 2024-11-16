package com.github.glitchruk.tinytools.concurrent;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LateTest {

    private static final int NUM_THREADS = 16;
    private static final int NUM_ITERATIONS = 100;
    private static final String VALUE = "TestValue";

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentSet() throws InterruptedException {
        final Late<String> late = new Late<>();
        final AtomicInteger exceptions = new AtomicInteger(0);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    try {
                        late.set("Value-" + Thread.currentThread().threadId());
                    } catch (IllegalStateException e) {
                        exceptions.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertEquals(exceptions.get(), NUM_THREADS - 1, "Exactly one thread should successfully set the value, all others should throw exceptions");
        assertTrue(late.isInitialized(), "Late value should be initialized");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentGetBeforeInitialization() throws InterruptedException {
        final Late<String> late = new Late<>();
        final AtomicInteger exceptions = new AtomicInteger(0);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    try {
                        late.get();
                    } catch (IllegalStateException e) {
                        exceptions.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertEquals(NUM_THREADS, exceptions.get(),
                "All threads should throw exceptions when calling get before initialization");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testConcurrentGetAfterInitialization() throws InterruptedException {
        final Late<String> late = new Late<>();
        late.set(VALUE);
        final AtomicInteger successCount = new AtomicInteger(0);
        boolean terminated;

        try (final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    try {
                        String value = late.get();
                        if (VALUE.equals(value)) {
                            successCount.incrementAndGet();
                        }
                    } catch (IllegalStateException e) {
                        fail("No thread should throw an exception when calling get after initialization");
                    }
                });
            }

            executor.shutdown();
            terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        assertTrue(terminated, "Executor should terminate");
        assertEquals(NUM_THREADS, successCount.get(),
                "All threads should successfully retrieve the initialized value");
    }

    @RepeatedTest(NUM_ITERATIONS)
    public void testIsInitialized() {
        final Late<String> late = new Late<>();
        assertFalse(late.isInitialized(), "Late value should not be initialized initially");

        late.set("TestValue");
        assertTrue(late.isInitialized(), "Late value should be initialized after set");
    }

    @Test
    public void testSetNull() {
        final Late<String> late = new Late<>();
        assertThrows(NullPointerException.class, () -> late.set(null), "Setting null should throw NullPointerException");
    }
}
