package elgatopro300.bbsstructureform;

import elgatopro300.bbsstructureform.client.packs.WorldStructuresSourcePack;
import elgatopro300.bbsstructureform.form.StructureForm;

import mchorse.bbs_mod.addons.BBSAddon;
import mchorse.bbs_mod.events.register.RegisterFormsEvent;
import mchorse.bbs_mod.events.register.RegisterSourcePacksEvent;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.resources.packs.InternalAssetsSourcePack;

public class BBSStructureFormAddon extends BBSAddon
{
    @Override
    protected void registerForms(RegisterFormsEvent event)
    {
        event.getForms().register(Link.bbs("structure"), StructureForm.class);
    }

    @Override
    protected void registerSourcePacks(RegisterSourcePacksEvent event)
    {
        try
        {
            event.provider.register(new InternalAssetsSourcePack("bbs-structureform", "assets/bbs_structureform", BBSStructureFormAddon.class));
            event.provider.register(new WorldStructuresSourcePack());
        }
        catch (Throwable ignored) {}
    }
}
