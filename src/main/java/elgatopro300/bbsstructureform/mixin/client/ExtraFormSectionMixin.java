package elgatopro300.bbsstructureform.mixin.client;

import mchorse.bbs_mod.forms.categories.FormCategory;
import mchorse.bbs_mod.forms.sections.ExtraFormSection;
import mchorse.bbs_mod.forms.forms.StructureForm;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ExtraFormSection.class})
public class ExtraFormSectionMixin {
   @Shadow
   private FormCategory extra;

   @Inject(
      method = {"initiate()V"},
      at = {@At("TAIL")},
      remap = false
   )
   public void onInitiate(CallbackInfo info) {
      StructureForm form = new StructureForm();
      this.extra.addForm(form);
   }
}
