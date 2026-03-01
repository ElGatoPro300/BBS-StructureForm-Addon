package elgatopro300.bbsstructureform.mixin;

import mchorse.bbs_mod.BBSMod;
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
      /* No-op initialization hook */
   }
}
