package uk.antiperson.autotorch.config;

import org.bukkit.entity.Player;
import uk.antiperson.autotorch.AutoTorch;

import java.io.File;
import java.io.IOException;

public class PlayerConfig extends Configuration {

    private final AutoTorch autoTorch;

    public PlayerConfig(AutoTorch autoTorch, Player player) {
        super(autoTorch, "player-data" + File.separator + player.getUniqueId() + ".yml");
        this.autoTorch = autoTorch;
    }

    @Override
    public void init() throws IOException {
        Configuration defaults = new PlayerDefaultConfig(autoTorch);
        defaults.init(true);
        init(false);
        if (getFileConfiguration().getKeys(true).size() == 0) {
            for (String path : defaults.getFileConfiguration().getKeys(true)) {
                getFileConfiguration().set(path, defaults.getFileConfiguration().get(path));
            }
        }
        getFileConfiguration().save(getFile());
        addToConfigRegistry(new ConfigItem.EnumConfigItem("take-torches-from", TorchLocation.class));
        addToConfigRegistry(new ConfigItem.EnumConfigItem("wall-torch-side", WallTorchSide.class));
        addToConfigRegistry(new ConfigItem.IntegerConfigItem("wall-torch-height", 0, 4));
    }

    public int getRadius() {
        return getFileConfiguration().getInt("radius");
    }

    public int getMinLightLevel() {
        return getFileConfiguration().getInt("light-level");
    }

    public int getYMin() {
        return getFileConfiguration().getInt("y-min");
    }

    public int getYMax() {
        return getFileConfiguration().getInt("y-max");
    }

    public PlayerConfig.TorchLocation getTorchLocation() {
        return PlayerConfig.TorchLocation.valueOf(getFileConfiguration().getString("take-torches-from"));
    }

    public PlayerConfig.WallTorchSide getWallSide() {
        return PlayerConfig.WallTorchSide.valueOf(getFileConfiguration().getString("wall-torch-side"));
    }

    public int getWallTorchHeight() {
        return getFileConfiguration().getInt("wall-torch-height");
    }

    public boolean isAttachToWalls() {
        return getFileConfiguration().getBoolean("attach-to-walls");
    }

    public boolean isEnabled() {
        return getFileConfiguration().getBoolean("placing-enabled");
    }

    public void setEnabled(boolean enabled) {
        getFileConfiguration().set("placing-enabled", enabled);
    }

    public enum TorchLocation {
        HAND,
        OFF_HAND,
        INVENTORY
    }

    public enum WallTorchSide {
        RIGHT,
        LEFT
    }
}
