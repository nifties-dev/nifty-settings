# Nifty Settings
## Introduction
Nifty Settings is a pure Java library for delivering configuration to your application conveniently and continuously.
The simplest scenario is best illustrated with an example.

Say, you have some class:

```
public class MyService {
    @Setting
    volatile boolean enabled;

    public void process(Data data) {
        if (!enabled) {
            return;
        }
        // ...process...
    }
}
```
Now, by marking its _enabled_ field with the @Setting annotation you say, that you want this field
to be configurable. After that you can obtain SettingsManager and ask it to inject settings into your object.
```
MyService myService1 = new MyService();
SettingsManager settingsManager = SettingsManager.builder()
  // set up configuration setting sources
  .build();
settingsManager.bind(myService1);
```
By calling _bind_ method a permanent binding is created between SettingsManager and your object, so whenever value 
changes in your configuration settings source, it gets delivered to your object instance instantly, without requiring 
an application restart.

There could be a variety of configuration settings sources such as Git repository or a database as well as
environment variables or static resource files within a project. Sources can be wired up, so some meaningful defaults
lie in one place, volatile overrides in another.

For more details on the reasoning behind this project please refer to a 
[wiki](https://github.com/nifties-dev/nifty-settings/wiki/Introduction).

## Compatibility
Java 8