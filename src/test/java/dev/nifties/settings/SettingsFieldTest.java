package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsFieldTest {

    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = new SettingsManager(
            new SettingsAnalyzer(), new SettingsBinder(), settingsService);

    public static class MyService1 {
        @Setting
        public boolean enabled;
    }

    @Test
    public void annotatedFieldWithoutMethodsInjected() {
        settingsService.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.inject(myService);
        assertTrue(myService.enabled);

        myService = new MyService1();
        settingsService.remove(MyService1.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.enabled);
    }

    @Test
    public void annotatedFieldWithoutMethodsBound() {
        settingsService.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.bind(myService);
        assertTrue(myService.enabled);

        settingsService.remove(MyService1.class.getName() + ".enabled");
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
    public void annotatedPrivateFieldWithGetterInjected() {
        settingsService.put(MyService2.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService2 myService = new MyService2();
        settingsManager.inject(myService);
        assertTrue(myService.isEnabled());

        myService = new MyService2();
        settingsService.remove(MyService2.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.isEnabled());

        settingsService.put(MyService2.class.getName() + ".enabled",
                Boolean.TRUE);
        settingsManager.inject(myService);
        assertTrue(myService.isEnabled());
    }

    @Test
    public void annotatedPrivateFieldWithGetterBound() {
        settingsService.put(MyService2.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService2 myService = new MyService2();
        settingsManager.bind(myService);
        assertTrue(myService.isEnabled());

        settingsService.remove(MyService2.class.getName() + ".enabled");
        assertFalse(myService.isEnabled());

        settingsService.put(MyService2.class.getName() + ".enabled",
                Boolean.TRUE);
        assertTrue(myService.isEnabled());

        settingsManager.unbind(myService);
        assertFalse(myService.isEnabled());

        settingsService.put(MyService2.class.getName() + ".enabled", true);
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
    public void annotatedPrivateFieldWithSetterInjected() {
        settingsService.put(MyService3.class.getName() + ".queueSize", 10);

        MyService3 myService = new MyService3();
        settingsManager.inject(myService);
        assertEquals(10, myService.queueSize);

        settingsService.put(MyService3.class.getName() + ".queueSize", 0);
        settingsManager.inject(myService);
        assertEquals(1, myService.queueSize);

        myService = new MyService3();
        settingsService.remove(MyService3.class.getName() + ".queueSize");
        settingsManager.inject(myService);
        assertEquals(0, myService.queueSize);
    }

    @Test
    public void annotatedPrivateFieldWithSetterBound() {
        settingsService.put(MyService3.class.getName() + ".queueSize", 10);

        MyService3 myService = new MyService3();
        settingsManager.bind(myService);
        assertEquals(10, myService.queueSize);

        settingsService.put(MyService3.class.getName() + ".queueSize", 0);
        assertEquals(1, myService.queueSize);

        settingsService.remove(MyService3.class.getName() + ".queueSize");
        assertEquals(1, myService.queueSize);

        settingsManager.unbind(myService);
        assertEquals(1, myService.queueSize);

        settingsService.put(MyService3.class.getName() + ".queueSize", 20);
        assertEquals(1, myService.queueSize);
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
    public void annotatedPrivateFieldWithGetterAndSetterInjected() {
        settingsService.put(MyService4.class.getName() + ".queueSize", 10);

        MyService4 myService = new MyService4();
        settingsManager.inject(myService);
        assertEquals(10, myService.getQueueSize());

        settingsService.put(MyService4.class.getName() + ".queueSize", 0);
        settingsManager.inject(myService);
        assertEquals(1, myService.getQueueSize());

        myService = new MyService4();
        settingsService.remove(MyService4.class.getName() + ".queueSize");
        settingsManager.inject(myService);
        assertEquals(0, myService.getQueueSize());
    }

    @Test
    public void annotatedPrivateFieldWithGetterAndSetterBound() {
        settingsService.put(MyService4.class.getName() + ".queueSize", 10);

        MyService4 myService = new MyService4();
        settingsManager.bind(myService);
        assertEquals(10, myService.getQueueSize());

        settingsService.put(MyService4.class.getName() + ".queueSize", 0);
        assertEquals(1, myService.getQueueSize());

        settingsService.remove(MyService4.class.getName() + ".queueSize");
        assertEquals(1, myService.getQueueSize()); // 1 not 0, because original value is restored using setter

        settingsManager.unbind(myService);
        assertEquals(1, myService.getQueueSize());

        settingsService.put(MyService4.class.getName() + ".queueSize", 20);
        assertEquals(1, myService.getQueueSize());
    }
}
