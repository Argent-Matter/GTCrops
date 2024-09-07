package argent_matter.gtcrops.data.lang;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler extends com.gregtechceu.gtceu.data.lang.LangHandler {

    public static void init(RegistrateLangProvider provider) {
        // tag prefixes
   provider.add("tooltip.gtcrops.growtth", "Growth: %s");
   provider.add("tooltip.gtcrops.gain", "Gain: %s");


    }
}