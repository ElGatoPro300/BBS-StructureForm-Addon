package elgatopro300.bbsstructureform.client.gui.forms;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIKeyframeFactory;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs_mod.utils.keyframes.Keyframe;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class UIStringKeyframeFactory extends UIKeyframeFactory<String>
{
    private UITextbox string;

    public UIStringKeyframeFactory(Keyframe<String> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        this.string = new UITextbox(1000, this::setValue);
        this.string.setText(keyframe.getValue());

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
                List<String> items = new ArrayList<>();
                try {
                    for (Link l : BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures"))) {
                        if (l.path.toLowerCase().endsWith(".nbt")) items.add("bbs-structureform:" + l.path);
                    }
                    for (Link l : BBSMod.getProvider().getLinksFromPath(new Link("world", ""))) {
                        if (l.path.toLowerCase().endsWith(".nbt")) items.add("world:" + l.path);
                    }
                } catch (Throwable ignored) {}
                items.sort(null);
                UIStringOverlayPanel panel = new UIStringOverlayPanel(IKey.raw("Pick Structure"), items, (value) -> {
                    String v = value == null ? "" : value;
                    this.editor.getGraph().setValue(v, true);
                });
                String current = this.keyframe.getValue();
                if (current != null && !current.isEmpty()) {
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
                    this.editor.getGraph().setValue(id, true);
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
                overlay.setValue(this.keyframe.getValue());
                UIOverlay.addOverlay(this.getContext(), overlay, 280, 0.5F);
            });

            this.scroll.add(pickBiome);
        }

        /* Solo permitir escritura manual para pistas genéricas;
           para structure_file y biome_id se fuerza uso de los botones. */
        if (!isStructureFile && !isBiomeId)
        {
            this.scroll.add(this.string);
        }
    }

    private static Set<String> getSavedStructureFiles()
    {
        Set<String> locations = new HashSet<>();
        File savedFolder = new File(BBSMod.getAssetsFolder(), "structures");
        if (savedFolder.exists() && savedFolder.isDirectory())
        {
            try (Stream<Path> paths = Files.walk(savedFolder.toPath()))
            {
                paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".nbt"))
                    .forEach(path -> {
                        try
                        {
                            String relativePath = savedFolder.toPath().relativize(path).toString().replace("\\", "/");
                            locations.add(relativePath);
                        }
                        catch (Exception ignored) {}
                    });
            }
            catch (Exception e)
            {
                System.err.println("Failed to scan folder: " + savedFolder + " - " + e.getMessage());
            }
        }
        return locations;
    }
}
