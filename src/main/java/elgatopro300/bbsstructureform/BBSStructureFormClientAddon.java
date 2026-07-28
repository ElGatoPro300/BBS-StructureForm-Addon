package elgatopro300.bbsstructureform;

import elgatopro300.bbsstructureform.client.forms.renderer.StructureFormRenderer;
import elgatopro300.bbsstructureform.client.gui.forms.UIStructureForm;
import elgatopro300.bbsstructureform.form.StructureForm;
import elgatopro300.bbsstructureform.importers.StructureImporter;

import mchorse.bbs_mod.addons.BBSClientAddon;
import mchorse.bbs_mod.events.register.RegisterFormCategoriesEvent;
import mchorse.bbs_mod.events.register.RegisterFormsRenderersEvent;
import mchorse.bbs_mod.events.register.RegisterImportersEvent;
import mchorse.bbs_mod.events.register.RegisterL10nEvent;
import mchorse.bbs_mod.forms.FormCategories;
import mchorse.bbs_mod.forms.categories.FormCategory;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.utils.icons.Icons;

import java.util.Collections;

import elgatopro300.bbsaddonengine.utils.HelperUIReplaysEditor;

public class BBSStructureFormClientAddon extends BBSClientAddon
{
    public BBSStructureFormClientAddon()
    {
        try
        {
            HelperUIReplaysEditor.registerColor("structure", 0x4CAF50);
            HelperUIReplaysEditor.registerColor("structure_file", 0x4CAF50);
            HelperUIReplaysEditor.registerColor("biome", 0x8BC34A);
            HelperUIReplaysEditor.registerColor("biome_id", 0x8BC34A);

            HelperUIReplaysEditor.registerIcon("structure", Icons.FOLDER);
            HelperUIReplaysEditor.registerIcon("structure_file", Icons.FOLDER);
        }
        catch (Throwable ignored) {}
    }

    @Override
    protected void registerFormsRenderers(RegisterFormsRenderersEvent event)
    {
        event.registerRenderer(StructureForm.class, StructureFormRenderer::new);
        event.registerPanel(StructureForm.class, UIStructureForm::new);
    }

    @Override
    protected void registerImporters(RegisterImportersEvent event)
    {
        event.register(new StructureImporter());
    }

    @Override
    protected void registerL10n(RegisterL10nEvent event)
    {
        try
        {
            event.l10n.register((lang) -> Collections.singletonList(new Link("bbs-structureform", "lang/" + lang + ".json")));
            event.l10n.reload();
        }
        catch (Throwable ignored) {}
    }

    @Override
    protected void registerFormCategories(RegisterFormCategoriesEvent event)
    {
        try
        {
            FormCategories categories = event.getCategories();
            if (categories != null && categories.getAllCategories() != null)
            {
                for (FormCategory category : categories.getAllCategories())
                {
                    if (category != null && category.title != null && category.title.get() != null)
                    {
                        if (category.title.get().toLowerCase().contains("extra"))
                        {
                            StructureForm form = new StructureForm();
                            form.structureFile.set("bbs-structureform:structures/tree.nbt");
                            category.addForm(form);
                            break;
                        }
                    }
                }
            }
        }
        catch (Throwable ignored) {}
    }
}
