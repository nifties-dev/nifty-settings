import dev.nifties.settings.SettingsChannel;
import dev.nifties.settings.SettingsSource;

module nifty.settings.client {
    requires static lombok;
    exports dev.nifties.settings;
    exports dev.nifties.settings.annotation;
    uses SettingsSource;
}