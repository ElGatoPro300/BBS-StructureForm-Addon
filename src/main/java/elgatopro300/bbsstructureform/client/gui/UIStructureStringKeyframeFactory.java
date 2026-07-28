package elgatopro300.bbsstructureform.client.gui;

import elgatopro300.bbsstructureform.client.gui.forms.UIStructureOverlayPanel;

import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIKeyframeFactory;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.utils.keyframes.Keyframe;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class UIStructureStringKeyframeFactory extends UIKeyframeFactory<String>
{
    public UIStructureStringKeyframeFactory(Keyframe<String> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

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
                UIStructureOverlayPanel panel = new UIStructureOverlayPanel(IKey.raw("Pick Structure"), (value) ->
                {
                    String v = value == null ? "" : value.toString();
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
    }
}
