package elgatopro300.bbsstructureform.mixin.client;

import elgatopro300.bbsstructureform.form.StructureForm;
import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.forms.categories.FormCategory;
import mchorse.bbs_mod.forms.sections.ExtraFormSection;
import mchorse.bbs_mod.resources.Link;
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
      try {
         String preferred = "structures/tree.nbt";
         boolean foundPreferred = false;
         for (Link link : BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures"))) {
            if (!foundPreferred && preferred.equals(link.path)) {
               form.structureFile.set("bbs-structureform:" + preferred);
               foundPreferred = true;
            }
         }
         if (!foundPreferred) {
            for (Link link : BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures"))) {
               if (link.path.toLowerCase().endsWith(".nbt")) {
                  form.structureFile.set("bbs-structureform:" + link.path);
                  break;
               }
            }
         }
      } catch (Exception ignored) {}
      this.extra.addForm(form);
   }
}
