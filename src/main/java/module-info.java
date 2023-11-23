import dev.nifties.settings.SettingsChannel;
import dev.nifties.settings.SettingsSource;

module nifty.settings.client {
    requires lombok;
    exports dev.nifties.settings;
    uses SettingsSource;
    uses SettingsChannel;
}