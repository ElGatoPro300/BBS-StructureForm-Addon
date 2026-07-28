package elgatopro300.bbsstructureform.client.gui.forms;

import elgatopro300.bbsstructureform.form.StructureForm;

import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.forms.editors.panels.UIFormPanel;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.colors.Color;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class UIStructureFormPanel extends UIFormPanel<StructureForm>
{
    public UIButton pickStructure;
    public UIButton pickBiome;
    public UITextbox structureFile;
    public UIColor color;
    public UIToggle toggleLight;
    public UITrackpad lightIntensity;
    public UIToggle toggleFluid;

    public UIStructureFormPanel(UIForm editor)
    {
        super(editor);

        this.pickStructure = new UIButton(L10n.lang("bbs.structureform.ui.pick_structure"), (b) -> this.pickStructure());
        this.structureFile = new UITextbox(100, (s) -> this.form.structureFile.set(s)).path().border();
        this.color = new UIColor((c) -> this.form.color.set(Color.rgba(c))).withAlpha();
        this.pickBiome = new UIButton(L10n.lang("bbs.structureform.ui.pick_biome"), (b) -> this.pickBiome());

        this.toggleLight = new UIToggle(L10n.lang("bbs.structureform.ui.emit_light"), false, (t) -> this.form.emitLight.set(t.getValue()));
        this.lightIntensity = new UITrackpad((v) -> this.form.lightIntensity.set(v.intValue()))
            .integer()
            .limit(1D, 15D);
        this.toggleFluid = new UIToggle(L10n.lang("bbs.structureform.ui.render_fluid"), false, (t) -> this.form.renderFluid.set(t.getValue()));

        this.options.add(
            UI.label(UIKeys.FORMS_EDITORS_GENERAL),
            this.color
        );
        this.options.add(this.pickStructure);
        this.options.add(this.pickBiome);
        this.options.add(this.toggleLight);
        this.options.add(this.toggleFluid);
        this.options.add(UI.label(L10n.lang("bbs.structureform.ui.light_intensity")).marginTop(6), this.lightIntensity);
    }

    private void pickStructure()
    {
        UIStructureOverlayPanel overlay = new UIStructureOverlayPanel(
            L10n.lang("bbs.structureform.ui.pick_structure"),
            (link) -> this.setStructure(link)
        );

        String current = this.form.structureFile.get();
        if (current == null || current.isEmpty())
        {
            overlay.set("");
        }
        else
        {
            try
            {
                overlay.set(Link.create(current));
            }
            catch (Exception e)
            {
                overlay.set("");
            }
        }
        UIOverlay.addOverlay(this.getContext(), overlay, 280, 0.5F);
    }

    private void pickBiome()
    {
        UIListOverlayPanel overlay = new UIListOverlayPanel(L10n.lang("bbs.structureform.ui.pick_biome"), (value) ->
        {
            String id = value == null ? "" : value;
            this.form.biomeId.set(id);
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
        overlay.setValue(this.form.biomeId.get());
        UIOverlay.addOverlay(this.getContext(), overlay, 280, 0.5F);
    }

    private void setStructure(Link link)
    {
        String path = link == null ? "" : link.toString();

        this.form.structureFile.set(path);
        this.structureFile.setText(path);
    }

    @Override
    public void startEdit(StructureForm form)
    {
        super.startEdit(form);

        this.structureFile.setText(form.structureFile.get());
        this.color.setColor(form.color.get().getARGBColor());
        this.toggleLight.setValue(form.emitLight.get());
        this.lightIntensity.setValue((double) form.lightIntensity.get());
        this.toggleFluid.setValue(form.renderFluid.get());
    }
}
