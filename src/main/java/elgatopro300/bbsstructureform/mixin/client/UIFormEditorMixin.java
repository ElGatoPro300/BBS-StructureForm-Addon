package elgatopro300.bbsstructureform.mixin.client;

import elgatopro300.bbsstructureform.form.StructureForm;
import elgatopro300.bbsstructureform.client.gui.forms.UIStructureForm;
import mchorse.bbs_mod.ui.forms.editors.UIFormEditor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({UIFormEditor.class})
public class UIFormEditorMixin {
   @Inject(
      method = {"<clinit>()V"},
      at = {@At("TAIL")},
      remap = false
   )
   private static void onStatic(CallbackInfo info) {
      UIFormEditor.register(StructureForm.class, UIStructureForm::new);
   }
}
