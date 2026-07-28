package elgatopro300.bbsstructureform.mixin.client;

import elgatopro300.bbsstructureform.client.forms.renderer.StructureFormRenderer;
import elgatopro300.bbsstructureform.form.StructureForm;

import mchorse.bbs_mod.forms.FormUtilsClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({FormUtilsClient.class})
public class FormUtilsClientMixin {
   @Inject(
      method = {"<clinit>()V"},
      at = {@At("TAIL")},
      remap = false
   )
   private static void onStatic(CallbackInfo info) {
      FormUtilsClient.register(StructureForm.class, StructureFormRenderer::new);
   }
}
