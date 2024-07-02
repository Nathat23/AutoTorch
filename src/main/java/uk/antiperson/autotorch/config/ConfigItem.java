package uk.antiperson.autotorch.config;

public class ConfigItem {

    private final String path;
    public ConfigItem(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static class IntegerConfigItem extends ConfigItem {

        private final int minBound;
        private final int maxBound;

        public IntegerConfigItem(String path, int minBound, int maxBound) {
            super(path);
            this.minBound = minBound;
            this.maxBound = maxBound;
        }

        public int getMinBound() {
            return minBound;
        }

        public int getMaxBound() {
            return maxBound;
        }
    }

    public static class EnumConfigItem extends ConfigItem {

        private final Class<? extends Enum<?>> representedEnum;
        public EnumConfigItem(String path, Class<? extends Enum<?>> value) {
            super(path);
            this.representedEnum = value;
        }

        public Class<? extends Enum<?>> getValue() {
            return representedEnum;
        }

    }
}
