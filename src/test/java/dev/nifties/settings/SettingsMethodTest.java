package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsMethodTest {
    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = SettingsManager.builder().service(settingsService).build();

    public static class MyService1 {

        boolean notMatchingField;

        @Setting
        public void setEnabled(boolean notMatchingField) {
            this.notMatchingField = notMatchingField;
        }
    }

    @Test
    public void annotatedMethodWithoutFieldInjected() {
        settingsService.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.inject(myService);
        assertTrue(myService.notMatchingField);

        myService = new MyService1();
        settingsService.remove(MyService1.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.notMatchingField);
    }

    @Test
    public void annotatedMethodWithoutFieldBound() {
        settingsService.put(MyService1.class.getName() + ".enabled", Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.bind(myService);
        assertTrue(myService.notMatchingField);

        settingsService.remove(MyService1.class.getName() + ".enabled");
        assertFalse(myService.notMatchingField);

        settingsManager.unbind(myService);
        assertFalse(myService.notMatchingField);

        settingsService.put(MyService1.class.getName() + ".enabled", Boolean.TRUE);
        assertFalse(myService.notMatchingField);
    }

    @Test
    public void annotatedMethodWithoutFieldConversionFailureInjected() {
        settingsService.put(MyService1.class.getName() + ".enabled", 1);

        MyService1 myService = new MyService1();
        assertThrows(IllegalArgumentException.class, () -> settingsManager.inject(myService));
    }

    @Test
    public void annotatedMethodWithoutFieldConversionFailureBound() {
        settingsService.put(MyService1.class.getName() + ".enabled", 1);

        MyService1 myService = new MyService1();
        assertThrows(IllegalArgumentException.class, () -> settingsManager.bind(myService));

        settingsService.put(MyService1.class.getName() + ".enabled", true);
        settingsManager.bind(myService);
        assertTrue(myService.notMatchingField);

        assertThrows(IllegalArgumentException.class, () -> settingsService.put(MyService1.class.getName() + ".enabled", 1));
        assertTrue(myService.notMatchingField);
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
        settingsService.put(MyService2.class.getName() + ".enabled", Boolean.TRUE);

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> settingsManager.inject(new MyService2()));
        assertEquals("""
          @Settings not allowed on method: \
          public boolean dev.nifties.settings.SettingsMethodTest$MyService2.isEnabled(), \
          only setters and fields can be marked with this annotation""", e.getMessage());
    }

    @Test
    public void annotatedGetterWithPrivateFieldBound() {
        settingsService.put(MyService2.class.getName() + ".enabled", Boolean.TRUE);

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
    public void annotatedSetterWithPrivateFieldBound() {
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
    public void annotatedSetterWithPrivateFieldAndSetterBound() {
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

    public static class MyService5 extends MyService1 {
    }

    @Test
    public void inheritedAnnotatedMethodWithoutFieldInjected() {
        settingsService.put(MyService5.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService5 myService = new MyService5();
        settingsManager.inject(myService);
        assertTrue(myService.notMatchingField);

        myService = new MyService5();
        settingsService.remove(MyService5.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.notMatchingField);
    }

    @Test
    public void inheritedAnnotatedMethodWithoutFieldBound() {
        settingsService.put(MyService5.class.getName() + ".enabled", Boolean.TRUE);

        MyService5 myService = new MyService5();
        settingsManager.bind(myService);
        assertTrue(myService.notMatchingField);

        settingsService.remove(MyService5.class.getName() + ".enabled");
        assertFalse(myService.notMatchingField);

        settingsManager.unbind(myService);
        assertFalse(myService.notMatchingField);

        settingsService.put(MyService5.class.getName() + ".enabled", Boolean.TRUE);
        assertFalse(myService.notMatchingField);
    }

    /**
     * For methods this works slightly differently then with fields - methods do get overridden. So even though analyzer
     * detects two mappings, when invoking them the same MyService6.setEnabled method gets executed twice.
     * FIXME Same setter is invoked twice
     */
    public static class MyService6 extends MyService1 {
        private boolean overriddenField;
        @Setting
        public void setEnabled(boolean overriddenField) {
            super.setEnabled(overriddenField); // parent value will get set only if we call super explicitly.
            this.overriddenField = overriddenField;
        }
    }

    @Test
    public void overriddenInheritedAnnotatedMethodWithoutFieldInjected() {
        settingsService.put(MyService6.class.getName() + ".enabled", Boolean.TRUE);

        MyService6 myService = new MyService6();
        settingsManager.inject(myService);
        assertTrue(myService.overriddenField);
        assertTrue(myService.notMatchingField);

        myService = new MyService6();
        settingsService.remove(MyService6.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.overriddenField);
        assertFalse(myService.notMatchingField);
    }

    @Test
    public void overriddenInheritedAnnotatedMethodWithoutFieldBound() {
        settingsService.put(MyService6.class.getName() + ".enabled", Boolean.TRUE);

        MyService6 myService = new MyService6();
        settingsManager.bind(myService);
        assertTrue(myService.overriddenField);
        assertTrue(myService.notMatchingField);

        settingsService.remove(MyService6.class.getName() + ".enabled");
        assertFalse(myService.overriddenField);
        assertFalse(myService.notMatchingField);

        settingsManager.unbind(myService);
        assertFalse(myService.overriddenField);
        assertFalse(myService.notMatchingField);

        settingsService.put(MyService6.class.getName() + ".enabled", Boolean.TRUE);
        assertFalse(myService.overriddenField);
        assertFalse(myService.notMatchingField);
    }
}
