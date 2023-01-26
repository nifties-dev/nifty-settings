package dev.nifties.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Test;

import dev.nifties.settings.annotation.Setting;

public class NiftySettingsUsecaseTests {

    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = new SettingsManager(
            new SettingsAnalyzer(), settingsService);

    public static class MyService1 {
        @Setting
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void process(Collection<?> entries) {
            if (!enabled) {
                return;
            }
            // ...process...
        }
    }

    @Test
    public void useCase1() {
        settingsService.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.inject(myService);
        assertTrue(myService.isEnabled());

        myService = new MyService1();
        settingsService.remove(MyService1.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.isEnabled());
    }

    public class MyService2 implements Closeable {
        private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        public ThreadPoolExecutor getThreadPoolExecutor() {
            return threadPoolExecutor;
        }

        @Setting
        public void setThreadPoolSize(int size) {
            threadPoolExecutor.setMaximumPoolSize(size);
            threadPoolExecutor.setCorePoolSize(size);
        }

        @Override
        public void close() {
            threadPoolExecutor.shutdown();
        }
    }

    @Test
    public void useCase2() {
        int defaultPoolSize = Runtime.getRuntime().availableProcessors();
        int customPoolSize = defaultPoolSize * 2;
        settingsService.put(MyService2.class.getName() + ".threadPoolSize",
                customPoolSize);

        try (MyService2 myService = new MyService2()) {
            settingsManager.inject(myService);
            assertEquals(customPoolSize,
                    myService.getThreadPoolExecutor().getCorePoolSize());
            assertEquals(customPoolSize,
                    myService.getThreadPoolExecutor().getMaximumPoolSize());
        }

        try (MyService2 myService = new MyService2()) {
            settingsService
                    .remove(MyService2.class.getName() + ".threadPoolSize");
            settingsManager.inject(myService);
            assertEquals(defaultPoolSize,
                    myService.getThreadPoolExecutor().getCorePoolSize());
            assertEquals(defaultPoolSize,
                    myService.getThreadPoolExecutor().getMaximumPoolSize());
        }
    }
}
