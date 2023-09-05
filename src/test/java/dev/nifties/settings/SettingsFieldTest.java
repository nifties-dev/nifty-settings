package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsFieldTest {

    private SimpleSettingsHolder settingsHolder = new SimpleSettingsHolder();
    private SettingsService settingsService = new SettingsService(List.of(settingsHolder));
    private SettingsManager settingsManager = new SettingsManager(
            new SettingsAnalyzer(), new SettingsBinder(), settingsService);

    public static class MyService1 {
        @Setting
        public boolean enabled;
    }

    @Test
    public void annotatedFieldWithoutMethods() {
        settingsHolder.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.inject(myService);
        assertTrue(myService.enabled);

        myService = new MyService1();
        settingsHolder.remove(MyService1.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.enabled);
    }

    public static class MyService2 {
        @Setting
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }
    }

    @Test
    public void annotatedPrivateFieldWithGetter() {
        settingsHolder.put(MyService2.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService2 myService = new MyService2();
        settingsManager.inject(myService);
        assertTrue(myService.isEnabled());

        myService = new MyService2();
        settingsHolder.remove(MyService2.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.isEnabled());
    }

    public static class MyService3 {
        @Setting
        private int queueSize;

        public void setQueueSize(int queueSize) {
            this.queueSize = Math.max(queueSize, 1);
        }
    }

    @Test
    public void annotatedPrivateFieldWithSetter() {
        settingsHolder.put(MyService3.class.getName() + ".queueSize", 10);

        MyService3 myService = new MyService3();
        settingsManager.inject(myService);
        assertEquals(10, myService.queueSize);

        settingsHolder.put(MyService3.class.getName() + ".queueSize", 0);
        settingsManager.inject(myService);
        assertEquals(1, myService.queueSize);

        myService = new MyService3();
        settingsHolder.remove(MyService3.class.getName() + ".queueSize");
        settingsManager.inject(myService);
        assertEquals(0, myService.queueSize);
    }

    public static class MyService4 {
        @Setting
        private int queueSize;

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = Math.max(queueSize, 1);
        }
    }

    @Test
    public void annotatedPrivateFieldWithGetterAndSetter() {
        settingsHolder.put(MyService4.class.getName() + ".queueSize", 10);

        MyService4 myService = new MyService4();
        settingsManager.inject(myService);
        assertEquals(10, myService.getQueueSize());

        settingsHolder.put(MyService4.class.getName() + ".queueSize", 0);
        settingsManager.inject(myService);
        assertEquals(1, myService.getQueueSize());

        myService = new MyService4();
        settingsHolder.remove(MyService4.class.getName() + ".queueSize");
        settingsManager.inject(myService);
        assertEquals(0, myService.getQueueSize());
    }

    @Test
    public void annotatedPrivateFieldWithGetterAndSetter2() {
        settingsHolder.put(MyService4.class.getName() + ".queueSize", 10);

        MyService4 myService = new MyService4();
        settingsManager.bind(myService);
        assertEquals(10, myService.getQueueSize());

        settingsHolder.put(MyService4.class.getName() + ".queueSize", 0);
        assertEquals(1, myService.getQueueSize());

        settingsHolder.remove(MyService4.class.getName() + ".queueSize");
        assertEquals(1, myService.getQueueSize()); // 1 not 0, because original value is restored using setter

        settingsManager.unbind(myService);
//        assertEquals(0, myService.getQueueSize());
    }
}
