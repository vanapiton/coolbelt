package fi._1up.coolbelt.config;

public enum HotbarAlgorithm {
    ALWAYS_PREFER_FASTEST_TOOL("Always prefer the fastest tool"),
    ALWAYS_PREFER_HOTBAR_TOOL("Always prefer the hotbar tool"),
    ALWAYS_PREFER_BELT_TOOL("Always prefer the toolbelt tool"),
    ALWAYS_PREFER_HAND_TOOL("Always prefer the selected tool");

    final String stringValue;

    HotbarAlgorithm(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
