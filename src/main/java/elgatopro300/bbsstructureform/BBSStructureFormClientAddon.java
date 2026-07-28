package elgatopro300.bbsstructureform;

import elgatopro300.bbsaddonengine.BBSAddonEngineClient;
import elgatopro300.bbsaddonengine.utils.HelperUIReplaysEditor;
import elgatopro300.bbsstructureform.client.forms.renderer.StructureFormRenderer;
import elgatopro300.bbsstructureform.client.gui.forms.UIStructureForm;
import elgatopro300.bbsstructureform.form.StructureForm;
import elgatopro300.bbsstructureform.importers.StructureImporter;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.addons.AddonInfo;
import mchorse.bbs_mod.addons.BBSClientAddon;
import mchorse.bbs_mod.events.Subscribe;
import mchorse.bbs_mod.events.register.RegisterFormCategoriesEvent;
import mchorse.bbs_mod.events.register.RegisterFormsRenderersEvent;
import mchorse.bbs_mod.events.register.RegisterImportersEvent;
import mchorse.bbs_mod.events.register.RegisterL10nEvent;
import mchorse.bbs_mod.forms.FormCategories;
import mchorse.bbs_mod.forms.categories.FormCategory;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.importers.Importers;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.forms.editors.UIFormEditor;
import mchorse.bbs_mod.ui.utils.icons.Icons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Collections;
import java.util.List;

public class BBSStructureFormClientAddon extends BBSClientAddon implements ClientModInitializer
{
    private static boolean L10N_DONE = false;

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
    public void onInitializeClient()
    {
        try
        {
            FormUtilsClient.register(StructureForm.class, StructureFormRenderer::new);
            UIFormEditor.register(StructureForm.class, UIStructureForm::new);
        }
        catch (Throwable ignored) {}

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            if (!L10N_DONE && BBSModClient.getL10n() != null)
            {
                try
                {
                    BBSModClient.getL10n().register((lang) -> Collections.singletonList(new Link("bbs-structureform", "lang/" + lang + ".json")));
                    BBSModClient.getL10n().reload();
                    Importers.getImporters().add(new StructureImporter());
                }
                catch (Throwable ignored) {}
                L10N_DONE = true;
            }

            if (BBSModClient.getFormCategories() != null)
            {
                addStructurePreset(BBSModClient.getFormCategories());
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
        {
            try
            {
                FormUtilsClient.register(StructureForm.class, StructureFormRenderer::new);
                UIFormEditor.register(StructureForm.class, UIStructureForm::new);

                if (BBSModClient.getFormCategories() != null)
                {
                    addStructurePreset(BBSModClient.getFormCategories());
                }
            }
            catch (Throwable ignored) {}
        });
    }

    private static void addStructurePreset(FormCategories categories)
    {
        if (categories == null || categories.getAllCategories() == null || categories.getAllCategories().isEmpty())
        {
            return;
        }

        FormCategory targetCategory = null;

        for (FormCategory category : categories.getAllCategories())
        {
            if (category != null && category.title != null)
            {
                String val = String.valueOf(category.title.get()).toLowerCase();

                if (val.contains("extra") || val.contains("miscel") || val.contains("misc"))
                {
                    targetCategory = category;
                    break;
                }
            }
        }

        if (targetCategory == null)
        {
            targetCategory = categories.getAllCategories().get(0);
        }

        if (targetCategory != null)
        {
            boolean exists = false;
            if (targetCategory.getForms() != null)
            {
                for (Object f : targetCategory.getForms())
                {
                    if (f instanceof StructureForm)
                    {
                        exists = true;
                        break;
                    }
                }
            }

            if (!exists)
            {
                StructureForm form = new StructureForm();
                form.structureFile.set("bbs-structureform:structures/tree.nbt");
                targetCategory.addForm(form);
            }
        }
    }

    @Override
    @Subscribe
    protected void registerFormsRenderers(RegisterFormsRenderersEvent event)
    {
        event.registerRenderer(StructureForm.class, StructureFormRenderer::new);
        event.registerPanel(StructureForm.class, UIStructureForm::new);
    }

    @Override
    @Subscribe
    protected void registerFormCategories(RegisterFormCategoriesEvent event)
    {
        addStructurePreset(event.getCategories());
    }

    @Override
    @Subscribe
    protected void registerImporters(RegisterImportersEvent event)
    {
        event.register(new StructureImporter());
    }

    @Override
    @Subscribe
    protected void registerL10n(RegisterL10nEvent event)
    {
        try
        {
            event.l10n.register((lang) -> Collections.singletonList(new Link("bbs-structureform", "lang/" + lang + ".json")));
            event.l10n.reload();
        }
        catch (Throwable ignored) {}
    }
}
