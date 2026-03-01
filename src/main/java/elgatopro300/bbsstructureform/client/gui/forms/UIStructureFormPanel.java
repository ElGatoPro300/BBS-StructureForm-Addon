package elgatopro300.bbsstructureform.client.gui.forms;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.forms.forms.StructureForm;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.colors.Colors;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class UIStructureFormPanel extends UIFormPanel<StructureForm>
{
    public UIButton pickStructure;
    public UIButton pickBiome;
    public UITextbox structureFile;
    public UIColor color;
    public UIToggle toggleLight;
    public UITrackpad lightIntensity;
    /* Pivot controls removed per request; structure pivots automatically */

    public UIStructureFormPanel(UIForm editor)
    {
        super(editor);

        this.pickStructure = new UIButton(IKey.raw("Pick Structure"), (b) -> this.pickStructure());
        this.structureFile = new UITextbox(100, (s) -> this.form.structureFile.set(s)).path().border();
        this.color = new UIColor((c) -> this.form.color.set(Color.rgba(c))).withAlpha();
        this.pickBiome = new UIButton(IKey.raw("Pick Biome"), (b) -> this.pickBiome());
        // Inicializar con valor por defecto; se sincroniza en startEdit
        this.toggleLight = new UIToggle(IKey.raw("Emit Light"), false, (t) -> this.toggleLight(t));
        this.lightIntensity = new UITrackpad((v) -> this.setLightIntensity(v.intValue()))
                .integer()
                .limit(1D, 15D);

        // Pivot UI removed; calculate center moved to Transform panel

        /* Quitar etiquetas; mostrar solo los controles */
        this.options.add(this.color);
        this.options.add(this.pickStructure);
        this.options.add(this.pickBiome);
        this.options.add(this.toggleLight);
        this.options.add(UI.label(IKey.raw("Light Intensity")).marginTop(6), this.lightIntensity);

        // Pivot controls removed
    }

    private void pickStructure()
    {
        java.util.Set<String> files = getSavedStructureFiles();
        java.util.List<String> list = new java.util.ArrayList<>(files);
        list.sort(null);
        UIStringOverlayPanel overlay = new UIStringOverlayPanel(IKey.raw("Pick Structure"), list, (value) -> {
            if (value == null || value.isEmpty() || value.equals("None")) {
                this.setStructure(null);
            } else {
                this.setStructure(Link.create("structures/" + value));
            }
        });
        String current = this.form.structureFile.get();
        if (current != null && !current.isEmpty()) {
            if (current.startsWith("structures/")) {
                overlay.set(current.substring("structures/".length()));
            } else {
                overlay.set(current);
            }
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
        java.util.List<String> ids = new java.util.ArrayList<>();
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

    private void toggleLight(UIToggle t)
    {
        this.form.emitLight.set(t.getValue());
    }

    private void setLightIntensity(int v)
    {
        int clamped = Math.max(1, Math.min(15, v));
        this.form.lightIntensity.set(clamped);
    }


    /* calculate center moved to Transform panel */


    private void setStructure(Link link)
    {
        String path = link == null ? "" : link.toString();

        this.form.structureFile.set(path);
        this.structureFile.setText(path);
    }

    private static java.util.Set<String> getSavedStructureFiles()
    {
        java.util.Set<String> locations = new java.util.HashSet<>();
        java.io.File savedFolder = new java.io.File(mchorse.bbs_mod.BBSMod.getAssetsFolder(), "structures");
        if (savedFolder.exists() && savedFolder.isDirectory())
        {
            try (java.util.stream.Stream<java.nio.file.Path> paths = java.nio.file.Files.walk(savedFolder.toPath()))
            {
                paths.filter(java.nio.file.Files::isRegularFile)
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
        this.toggleLight.setValue(form.emitLight.get());
        this.lightIntensity.setValue((double) form.lightIntensity.get());
        // Pivot controls removed
    }
}
