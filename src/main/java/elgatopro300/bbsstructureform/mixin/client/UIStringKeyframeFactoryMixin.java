package elgatopro300.bbsstructureform.mixin.client;

import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIKeyframeFactory;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs_mod.utils.keyframes.Keyframe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(value = mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIStringKeyframeFactory.class)
public abstract class UIStringKeyframeFactoryMixin extends UIKeyframeFactory<String>
{
    private UITextbox bbsstructureform$textField;

    public UIStringKeyframeFactoryMixin(Keyframe<String> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(Keyframe<String> keyframe, UIKeyframes editor, CallbackInfo ci)
    {
        UIKeyframeSheet sheet = editor.getGraph().getSheet(keyframe);

        boolean isStructureFile = sheet != null && (
            "structure_file".equals(sheet.id) ||
            sheet.id.endsWith("/structure_file") ||
            "structure".equals(sheet.id) ||
            sheet.id.endsWith("/structure")
        );

        boolean isBiomeId = sheet != null && (
            "biome_id".equals(sheet.id) ||
            sheet.id.endsWith("/biome_id") ||
            "biome".equals(sheet.id) ||
            sheet.id.endsWith("/biome")
        );

        if (isStructureFile)
        {
            UIButton pickStructure = new UIButton(IKey.raw("Pick Structure"), (b) ->
            {
                java.util.List<String> items = new java.util.ArrayList<>();

                try
                {
                    for (Link l : mchorse.bbs_mod.BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures")))
                    {
                        if (l.path.toLowerCase().endsWith(".nbt"))
                        {
                            items.add("bbs-structureform:" + l.path);
                        }
                    }

                    for (Link l : mchorse.bbs_mod.BBSMod.getProvider().getLinksFromPath(new Link("world", "")))
                    {
                        if (l.path.toLowerCase().endsWith(".nbt"))
                        {
                            items.add("world:" + l.path);
                        }
                    }
                }
                catch (Throwable ignored) {}

                items.sort(null);

                UIStringOverlayPanel panel = new UIStringOverlayPanel(IKey.raw("Pick Structure"), items, (value) ->
                {
                    String v = value == null ? "" : value;
                    editor.getGraph().setValue(v, true);
                });

                String current = keyframe.getValue();

                if (current != null && !current.isEmpty())
                {
                    panel.set(current);
                }

                UIOverlay.addOverlay(this.getContext(), panel, 280, 0.5F);
            });

            this.scroll.add(pickStructure);
        }
        else if (isBiomeId)
        {
            UIButton pickBiome = new UIButton(IKey.raw("Pick Biome"), (b) ->
            {
                UIListOverlayPanel overlay = new UIListOverlayPanel(IKey.raw("Pick Biome"), (value) ->
                {
                    String id = value == null ? "" : value;
                    editor.getGraph().setValue(id, true);
                });

                List<String> ids = new ArrayList<>();

                try
                {
                    if (MinecraftClient.getInstance().world != null)
                    {
                        Registry<Biome> reg = MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME);

                        for (Identifier id : reg.getIds())
                        {
                            ids.add(id.toString());
                        }
                    }
                }
                catch (Throwable ignored) {}

                overlay.addValues(ids);
                overlay.setValue(keyframe.getValue());
                UIOverlay.addOverlay(this.getContext(), overlay, 280, 0.5F);
            });

            this.scroll.add(pickBiome);
        }

        if (isStructureFile || isBiomeId)
        {
            java.util.List<?> children = this.scroll.getChildren();

            for (Object child : children)
            {
                if (child instanceof UITextbox)
                {
                    this.bbsstructureform$textField = (UITextbox) child;
                }
            }

            if (this.bbsstructureform$textField != null)
            {
                this.bbsstructureform$textField.removeFromParent();
            }
        }
    }
}
