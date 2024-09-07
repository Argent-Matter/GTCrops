package argent_matter.gtcrops.common.blocks;

import lombok.Getter;

@Getter
public enum CropType {
    WHEAT("wheat", 1),
    CARROT("carrot", 1),
    POTATO("potato", 1),
    BEETROOT("beetroot", 1),
    IRON("iron", 2),
    CANE("cane", 2),
    GOO_CANE("goo_cane",4);

    private final String name;
    private final int tier;  // Crop Tier

    CropType(String name, int tier) {
        this.name = name;
        this.tier = tier;
    }

    public static CropType fromName(String name) {
        for (CropType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
