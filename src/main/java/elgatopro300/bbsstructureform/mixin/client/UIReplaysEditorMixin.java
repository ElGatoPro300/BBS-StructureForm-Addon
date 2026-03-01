package elgatopro300.bbsstructureform.mixin.client;

import java.util.Map;
import mchorse.bbs_mod.ui.film.replays.UIReplaysEditor;
import mchorse.bbs_mod.ui.utils.icons.Icon;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({UIReplaysEditor.class})
public class UIReplaysEditorMixin {
   @Shadow
   private static Map<String, Integer> COLORS;
   @Shadow
   private static Map<String, Icon> ICONS;

   @Inject(
      method = {"<clinit>()V"},
      at = {@At("TAIL")},
      remap = false
   )
   private static void onStaticInit(CallbackInfo info) {
      COLORS.put("intensity", 16777011);
      COLORS.put("flare", 16716947);
      COLORS.put("fog_enabled", 16746530);
      COLORS.put("spot_inner_angle", 3407667);
      COLORS.put("point_radius", 16724787);
      ICONS.put("intensity", Icons.FADING);
      ICONS.put("flare", Icons.SUN);
      ICONS.put("fog_enabled", Icons.SPRAY);
      ICONS.put("spot_inner_angle", Icons.ARC);
      ICONS.put("point_radius", Icons.OUTLINE_SPHERE);
   }
}
