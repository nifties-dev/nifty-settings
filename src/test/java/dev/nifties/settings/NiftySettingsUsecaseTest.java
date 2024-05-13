package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

public class NiftySettingsUsecaseTest {

    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = SettingsManager.builder()
            .service(settingsService)
            .build();

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

    public static class ThreadPoolExecutorConfigurator {
        private final ThreadPoolExecutor threadPoolExecutor;

        public ThreadPoolExecutorConfigurator(ThreadPoolExecutor threadPoolExecutor) {
            this.threadPoolExecutor = threadPoolExecutor;
        }

        @Setting
        public void setPoolSize(int size) {
            if (size >= threadPoolExecutor.getMaximumPoolSize()) {
                threadPoolExecutor.setMaximumPoolSize(size);
                threadPoolExecutor.setCorePoolSize(size);
            } else {
                threadPoolExecutor.setCorePoolSize(size);
                threadPoolExecutor.setMaximumPoolSize(size);
            }
        }
    }

    @Test
    public void useCase2() {
        int defaultPoolSize = Runtime.getRuntime().availableProcessors();
        int customPoolSize = defaultPoolSize * 2;
        settingsService.put(ThreadPoolExecutorConfigurator.class.getName() + ".poolSize", customPoolSize);

        ThreadPoolExecutor processingExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(defaultPoolSize);
        ThreadPoolExecutorConfigurator processingExecutorConfigurator = new ThreadPoolExecutorConfigurator(processingExecutor);

        try {
            settingsManager.bind(processingExecutorConfigurator);
            assertEquals(customPoolSize, processingExecutor.getCorePoolSize());
            assertEquals(customPoolSize, processingExecutor.getMaximumPoolSize());

            settingsService.put(ThreadPoolExecutorConfigurator.class.getName() + ".poolSize", defaultPoolSize);
            assertEquals(defaultPoolSize, processingExecutor.getCorePoolSize());
            assertEquals(defaultPoolSize, processingExecutor.getMaximumPoolSize());
        } finally {
            processingExecutor.shutdown();
        }
    }
}
