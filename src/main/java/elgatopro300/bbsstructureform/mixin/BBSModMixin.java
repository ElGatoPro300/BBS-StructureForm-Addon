package elgatopro300.bbsstructureform.mixin;

import elgatopro300.bbsstructureform.form.StructureForm;
import elgatopro300.bbsstructureform.client.packs.WorldStructuresSourcePack;
import elgatopro300.bbsstructureform.BBSSTRUCTUREFORMAddon;
import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.resources.packs.InternalAssetsSourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BBSMod.class})
public class BBSModMixin {
   @Inject(
      method = {"onInitialize()V"},
      at = {@At("TAIL")},
      remap = false
   )
   public void onOnInitialize(CallbackInfo info) {
      BBSMod.getForms().register(Link.bbs("structure"), StructureForm.class);
      try {
         BBSMod.getProvider().register(new InternalAssetsSourcePack("bbs-structureform", "assets/bbs_structureform", BBSSTRUCTUREFORMAddon.class));
         BBSMod.getProvider().register(new WorldStructuresSourcePack());
      } catch (Throwable ignored) {}
   }
}
