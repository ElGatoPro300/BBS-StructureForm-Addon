package elgatopro300.bbsstructureform;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.events.register.RegisterSourcePacksEvent;
import mchorse.bbs_mod.resources.packs.InternalAssetsSourcePack;
import net.fabricmc.api.ModInitializer;

public class BBSSTRUCTUREFORMAddon implements ModInitializer {
   @Override
   public void onInitialize() {
      BBSMod.getProvider().register(new InternalAssetsSourcePack("bbs-structureform", "assets/bbs-structureform", this.getClass()));
   }
}
