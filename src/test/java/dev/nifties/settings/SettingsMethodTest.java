package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsMethodTest {

    private SimpleSettingsHolder settingsHolder = new SimpleSettingsHolder();
    private SettingsService settingsService = new SettingsService(List.of(settingsHolder));
    private SettingsManager settingsManager = new SettingsManager(
            new SettingsAnalyzer(), new SettingsBinder(), settingsService);

    public static class MyService1 {

        boolean notMatchingField;

        @Setting
        public void setEnabled(boolean notMatchingField) {
            this.notMatchingField = notMatchingField;
        }
    }

    @Test
    public void annotatedMethodWithoutFieldInjected() {
        settingsHolder.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.inject(myService);
        assertTrue(myService.notMatchingField);

        myService = new MyService1();
        settingsHolder.remove(MyService1.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.notMatchingField);
    }

    @Test
    public void annotatedMethodWithoutFieldBound() {
        settingsHolder.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.bind(myService);
        assertTrue(myService.notMatchingField);

        settingsHolder.remove(MyService1.class.getName() + ".enabled");
        assertFalse(myService.notMatchingField);
    }

    public static class MyService2 {
        private boolean enabled;

        @Setting
        public boolean isEnabled() {
            return enabled;
        }
    }

    @Test
    public void annotatedGetterWithPrivateFieldInjected() {
        settingsHolder.put(MyService2.class.getName() + ".enabled", Boolean.TRUE);

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> settingsManager.inject(new MyService2()));
        assertEquals("""
          @Settings not allowed on method: \
          public boolean dev.nifties.settings.SettingsMethodTest$MyService2.isEnabled(), \
          only setters and fields can be marked with this annotation""", e.getMessage());
    }

    @Test
    public void annotatedGetterWithPrivateFieldBound() {
        settingsHolder.put(MyService2.class.getName() + ".enabled", Boolean.TRUE);

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> settingsManager.bind(new MyService2()));
        assertEquals("""
          @Settings not allowed on method: \
          public boolean dev.nifties.settings.SettingsMethodTest$MyService2.isEnabled(), \
          only setters and fields can be marked with this annotation""", e.getMessage());
    }

    public static class MyService3 {
        private int queueSize;

        @Setting
        public void setQueueSize(int queueSize) {
            this.queueSize = Math.max(queueSize, 1);
        }
    }

    @Test
    public void annotatedSetterWithPrivateFieldInjected() {
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

    @Test
    public void annotatedSetterWithPrivateFieldBound() {
        settingsHolder.put(MyService3.class.getName() + ".queueSize", 10);

        MyService3 myService = new MyService3();
        settingsManager.bind(myService);
        assertEquals(10, myService.queueSize);

        settingsHolder.put(MyService3.class.getName() + ".queueSize", 0);
        assertEquals(1, myService.queueSize);

        settingsHolder.remove(MyService3.class.getName() + ".queueSize");
        assertEquals(1, myService.queueSize);

        settingsManager.unbind(myService);
        assertEquals(1, myService.queueSize);

        settingsHolder.put(MyService3.class.getName() + ".queueSize", 20);
        assertEquals(1, myService.queueSize);
    }

    public static class MyService4 {
        private int queueSize;

        public int getQueueSize() {
            return queueSize;
        }

        @Setting
        public void setQueueSize(int queueSize) {
            this.queueSize = Math.max(queueSize, 1);
        }
    }

    @Test
    public void annotatedSetterWithPrivateFieldAndGetterInjected() {
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
    public void annotatedSetterWithPrivateFieldAndSetterBound() {
        settingsHolder.put(MyService4.class.getName() + ".queueSize", 10);

        MyService4 myService = new MyService4();
        settingsManager.bind(myService);
        assertEquals(10, myService.getQueueSize());

        settingsHolder.put(MyService4.class.getName() + ".queueSize", 0);
        assertEquals(1, myService.getQueueSize());

        settingsHolder.remove(MyService4.class.getName() + ".queueSize");
        assertEquals(1, myService.getQueueSize()); // 1 not 0, because original value is restored using setter

        settingsManager.unbind(myService);
        assertEquals(1, myService.getQueueSize());

        settingsHolder.put(MyService4.class.getName() + ".queueSize", 20);
        assertEquals(1, myService.getQueueSize());
    }
}
