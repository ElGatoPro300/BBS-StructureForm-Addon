package elgatopro300.bbsstructureform;

import elgatopro300.bbsstructureform.client.packs.MinecraftSourcePack;
import elgatopro300.bbsstructureform.client.packs.WorldStructuresSourcePack;
import elgatopro300.bbsstructureform.form.StructureForm;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.addons.BBSAddon;
import mchorse.bbs_mod.events.Subscribe;
import mchorse.bbs_mod.events.register.RegisterFormsEvent;
import mchorse.bbs_mod.events.register.RegisterSourcePacksEvent;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.resources.packs.InternalAssetsSourcePack;

import net.fabricmc.api.ModInitializer;

public class BBSStructureFormAddon extends BBSAddon implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        try
        {
            BBSMod.getForms().register(Link.bbs("structure"), StructureForm.class);
            BBSMod.getProvider().register(new InternalAssetsSourcePack("bbs-structureform", "assets/bbs_structureform", BBSStructureFormAddon.class));
            BBSMod.getProvider().register(new WorldStructuresSourcePack());
            BBSMod.getProvider().register(new MinecraftSourcePack());
        }
        catch (Throwable ignored) {}
    }

    @Override
    @Subscribe
    protected void registerForms(RegisterFormsEvent event)
    {
        event.getForms().register(Link.bbs("structure"), StructureForm.class);
    }

    @Override
    @Subscribe
    protected void registerSourcePacks(RegisterSourcePacksEvent event)
    {
        try
        {
            event.provider.register(new InternalAssetsSourcePack("bbs-structureform", "assets/bbs_structureform", BBSStructureFormAddon.class));
            event.provider.register(new WorldStructuresSourcePack());
            event.provider.register(new MinecraftSourcePack());
        }
        catch (Throwable ignored) {}
    }
}
