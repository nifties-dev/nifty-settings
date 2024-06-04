package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsFieldTest {

    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = SettingsManager.builder()
            .service(settingsService)
            .build();

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

        settingsManager.unbind(myService);
        assertFalse(myService.enabled);

        settingsService.put(MyService1.class.getName() + ".enabled", Boolean.TRUE);
        assertFalse(myService.enabled);
    }

    @Test
    public void annotatedFieldWithoutMethodsConversionFailureInjected() {
        settingsService.put(MyService1.class.getName() + ".enabled", 1);

        MyService1 myService = new MyService1();
        assertThrows(IllegalArgumentException.class, () -> settingsManager.inject(myService));
    }

    @Test
    public void annotatedFieldWithoutMethodsConversionFailureBound() {
        settingsService.put(MyService1.class.getName() + ".enabled", 1);

        MyService1 myService = new MyService1();
        assertThrows(IllegalArgumentException.class, () -> settingsManager.bind(myService));

        settingsService.put(MyService1.class.getName() + ".enabled", true);
        settingsManager.bind(myService);
        assertTrue(myService.enabled);

        assertThrows(IllegalArgumentException.class, () -> settingsService.put(MyService1.class.getName() + ".enabled", 1));
        assertTrue(myService.enabled);
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

    public static class MyService5 extends MyService1 {
    }

    @Test
    public void inheritedAnnotatedFieldWithoutMethodsInjected() {
        settingsService.put(MyService5.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService5 myService = new MyService5();
        settingsManager.inject(myService);
        assertTrue(myService.enabled);

        myService = new MyService5();
        settingsService.remove(MyService5.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.enabled);
    }

    @Test
    public void inheritedAnnotatedFieldWithoutMethodsBound() {
        settingsService.put(MyService5.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService5 myService = new MyService5();
        settingsManager.bind(myService);
        assertTrue(myService.enabled);

        settingsService.remove(MyService5.class.getName() + ".enabled");
        assertFalse(myService.enabled);

        settingsManager.unbind(myService);
        assertFalse(myService.enabled);

        settingsService.put(MyService5.class.getName() + ".enabled",
                Boolean.TRUE);
        assertFalse(myService.enabled);
    }

    /**
     * This is a weird situation, enabled field exists both here and in superclass. Mappings are created for both fields
     * and they are updated sequentially, even though from outside we can only access top level object's field.
     */
    public static class MyService6 extends MyService1 {
        @Setting
        public boolean enabled;
    }

    @Test
    public void overriddenInheritedAnnotatedFieldWithoutMethodsInjected() {
        settingsService.put(MyService6.class.getName() + ".enabled", Boolean.TRUE);

        MyService6 myService = new MyService6();
        settingsManager.inject(myService);
        assertTrue(myService.enabled);

        myService = new MyService6();
        settingsService.remove(MyService6.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.enabled);
    }

    @Test
    public void overriddenInheritedAnnotatedFieldWithoutMethodsBound() {
        settingsService.put(MyService6.class.getName() + ".enabled", Boolean.TRUE);

        MyService6 myService = new MyService6();
        settingsManager.bind(myService);
        assertTrue(myService.enabled);

        settingsService.remove(MyService6.class.getName() + ".enabled");
        assertFalse(myService.enabled);

        settingsManager.unbind(myService);
        assertFalse(myService.enabled);

        settingsService.put(MyService6.class.getName() + ".enabled", Boolean.TRUE);
        assertFalse(myService.enabled);
    }

    public static class MyService7 {

        @Setting
        private MyService1 nested;
    }

    @Test
    public void annotatedNestedFieldWithoutMethodsInjected() {
        settingsService.put(MyService7.class.getName() + ".nested.enabled", Boolean.TRUE);

        MyService7 myService = new MyService7();
        // FIXME nested @Settings should probably be detected and fail-fasted
        settingsManager.inject(myService);
        assertNull(myService.nested);

        // while in future complex objects could be supported, currently their not, and we expect conversion failure
        settingsService.put(MyService7.class.getName() + ".nested", "enabled=true");
        assertThrows(IllegalArgumentException.class, () -> settingsManager.inject(myService));
        assertNull(myService.nested);

        settingsService.remove(MyService7.class.getName() + ".nested");
        settingsManager.inject(myService);
        assertNull(myService.nested);
    }

    @Test
    public void annotatedNestedFieldWithoutMethodsBound() {
        settingsService.put(MyService7.class.getName() + ".nested.enabled", Boolean.TRUE);

        MyService7 myService = new MyService7();
        // FIXME nested @Settings should probably be detected and fail-fasted
        settingsManager.bind(myService);
        assertNull(myService.nested);

        // while in future complex objects could be supported, currently their not, and we expect conversion failure
        assertThrows(IllegalArgumentException.class,
                () -> settingsService.put(MyService7.class.getName() + ".nested", "enabled=true"));

        settingsService.remove(MyService7.class.getName() + ".nested.enabled");
        assertNull(myService.nested);

        settingsManager.unbind(myService);
        assertNull(myService.nested);

        settingsService.put(MyService7.class.getName() + ".nested.enabled", Boolean.TRUE);
        assertNull(myService.nested);
    }
}
