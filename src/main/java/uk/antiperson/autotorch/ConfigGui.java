package uk.antiperson.autotorch;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.antiperson.autotorch.config.ConfigItem;
import uk.antiperson.autotorch.config.Configuration;
import uk.antiperson.autotorch.gui.BooleanGuiItem;
import uk.antiperson.autotorch.gui.EnumGuiItem;
import uk.antiperson.autotorch.gui.GuiItem;
import uk.antiperson.autotorch.gui.GuiItemStack;
import uk.antiperson.autotorch.gui.GuiPage;
import uk.antiperson.autotorch.gui.IntegerGuiItem;

import java.util.Arrays;
import java.util.Set;

public class ConfigGui {

    private final Configuration configuration;
    private final Configuration translation;

    public ConfigGui(Configuration configuration, Configuration translation) {
        this.configuration = configuration;
        this.translation = translation;
    }

    public GuiPage createPage(Player player) {
        Set<String> keys = configuration.getFileConfiguration().getKeys(true);
        GuiPage guiPage = new GuiPage(player, (int) Math.ceil(keys.size() / 9D));
        for (String key : keys) {
            GuiItem guiItem = generateItem(key);
            if (guiItem == null) {
                System.out.println("Couldn't generate item for  " + key);
                continue;
            }
            guiPage.putItem(guiPage.getInventory().firstEmpty(), guiItem);
        }
        return guiPage;
    }

    public GuiItem generateItem(String key) {
        Object object = configuration.getFileConfiguration().get(key);
        ConfigItem configItem = configuration.getFromConfigRegistry(key);
        if (object instanceof Boolean) {
            BooleanGuiItem booleanGuiItem = new BooleanGuiItem();
            GuiItemStack enabled = new GuiItemStack(new ItemStack(Material.GREEN_WOOL), translation.getFileConfiguration().getString(key), Arrays.asList(ChatColor.GREEN + "Enabled", "", ChatColor.WHITE + "Click to disable"));
            enabled.setOnClick(click -> configuration.getFileConfiguration().set(key, false));
            GuiItemStack disabled = new GuiItemStack(new ItemStack(Material.RED_WOOL), translation.getFileConfiguration().getString(key), Arrays.asList(ChatColor.RED + "Disabled", "", ChatColor.WHITE + "Click to enable"));
            disabled.setOnClick(click -> configuration.getFileConfiguration().set(key, true));
            booleanGuiItem.setTrueItem(enabled);
            booleanGuiItem.setFalseItem(disabled);
            booleanGuiItem.setDefaultState(configuration.getFileConfiguration().getBoolean(key));
            return booleanGuiItem;
        }
        if (object instanceof Integer) {
            ConfigItem.IntegerConfigItem anInt = (ConfigItem.IntegerConfigItem) configItem;
            IntegerGuiItem integerGuiItem;
            if (anInt == null) {
                integerGuiItem = new IntegerGuiItem(Integer.MIN_VALUE, Integer.MAX_VALUE, configuration.getFileConfiguration().getInt(key));
            } else {
                integerGuiItem = new IntegerGuiItem(anInt.getMinBound(), anInt.getMaxBound(), configuration.getFileConfiguration().getInt(key));
            }
            integerGuiItem.setItemStack(new GuiItemStack(new ItemStack(Material.IRON_INGOT), translation.getFileConfiguration().getString(key), Arrays.asList("%size%")));
            integerGuiItem.getItemStack().setOnClick(click -> configuration.getFileConfiguration().set(key, integerGuiItem.getValue()));
            return integerGuiItem;
        }
        if (configItem instanceof ConfigItem.EnumConfigItem) {
            ConfigItem.EnumConfigItem anEnum = (ConfigItem.EnumConfigItem) configItem;
            Enum<?> current = null;
            for (Enum<?> lEnum : anEnum.getValue().getEnumConstants()) {
                if (lEnum.toString().equals(object.toString())) {
                    current = lEnum;
                    break;
                }
            }
            EnumGuiItem enumGuiItem = new EnumGuiItem(anEnum.getValue().getEnumConstants());
            GuiItemStack guiItem = new GuiItemStack(new ItemStack(Material.CRAFTING_TABLE), translation.getFileConfiguration().getString(key), Arrays.asList("%enum%", "", ChatColor.WHITE + "Click to shift"));
            guiItem.setOnClick(click -> configuration.getFileConfiguration().set(key, enumGuiItem.getValue().toString()));
            enumGuiItem.populate(guiItem);
            enumGuiItem.setShowing(current);
            return enumGuiItem;
        }
        return null;
    }
}
