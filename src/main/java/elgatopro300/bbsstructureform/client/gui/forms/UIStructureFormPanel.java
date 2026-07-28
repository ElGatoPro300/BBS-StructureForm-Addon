package elgatopro300.bbsstructureform.client.gui.forms;

import elgatopro300.bbsstructureform.form.StructureForm;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.colors.Colors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtTagSizeTracker;
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

public class UIStructureFormPanel extends UIFormPanel<StructureForm>
{
    public UIButton pickStructure;
    public UIButton pickBiome;
    public UITextbox structureFile;
    public UIColor color;
    /* Pivot controls removed per request; structure pivots automatically */

    public UIStructureFormPanel(UIForm editor)
    {
        super(editor);

        this.pickStructure = new UIButton(L10n.lang("bbs.structureform.ui.pick_structure"), (b) -> this.pickStructure());
        this.structureFile = new UITextbox(100, (s) -> this.form.structureFile.set(s)).path().border();
        this.color = new UIColor((c) -> this.form.color.set(Color.rgba(c))).withAlpha();
        this.pickBiome = new UIButton(L10n.lang("bbs.structureform.ui.pick_biome"), (b) -> this.pickBiome());
        // Pivot UI removed; calculate center moved to Transform panel

        /* Quitar etiquetas; mostrar solo los controles */
        this.options.add(this.color);
        this.options.add(this.pickStructure);
        this.options.add(this.pickBiome);

        // Pivot controls removed
    }

    private void pickStructure()
    {
        List<String> list = new ArrayList<>();
        try {
            for (Link l : BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures"))) {
                if (l.path.toLowerCase().endsWith(".nbt")) {
                    list.add("bbs-structureform:" + l.path);
                }
            }
            for (Link l : BBSMod.getProvider().getLinksFromPath(new Link("world", ""))) {
                if (l.path.toLowerCase().endsWith(".nbt")) {
                    list.add("world:" + l.path);
                }
            }
        } catch (Throwable ignored) {}
        list.sort(null);
        UIStringOverlayPanel overlay = new UIStringOverlayPanel(L10n.lang("bbs.structureform.ui.pick_structure"), list, (value) -> {
            if (value == null || value.isEmpty() || value.equals("None")) {
                this.setStructure(null);
            } else {
                this.setStructure(Link.create(value));
            }
        });
        String current = this.form.structureFile.get();
        if (current != null && !current.isEmpty()) {
            overlay.set(current);
        }
        UIOverlay.addOverlay(this.getContext(), overlay, 280, 0.5F);
    }

    private void pickBiome()
    {
        UIListOverlayPanel overlay = new UIListOverlayPanel(IKey.raw("Pick Biome"), (value) ->
        {
            String id = value == null ? "" : value;
            this.form.biomeId.set(id);
        });

        // Construir lista de biomas de forma segura
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
        overlay.setValue(this.form.biomeId.get());
        UIOverlay.addOverlay(this.getContext(), overlay, 280, 0.5F);
    }

    /* calculate center moved to Transform panel */


    private void setStructure(Link link)
    {
        String path = link == null ? "" : link.toString();

        this.form.structureFile.set(path);
        this.structureFile.setText(path);
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

    @Override
    public void startEdit(StructureForm form)
    {
        super.startEdit(form);

        this.structureFile.setText(form.structureFile.get());
        this.color.setColor(form.color.get().getARGBColor());
        // Pivot controls removed
    }
}
