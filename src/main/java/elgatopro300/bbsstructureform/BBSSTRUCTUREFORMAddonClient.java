package elgatopro300.bbsstructureform;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.events.register.RegisterL10nEvent;
import mchorse.bbs_mod.resources.Link;
import java.util.Collections;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class BBSSTRUCTUREFORMAddonClient implements ClientModInitializer {
   @Override
   public void onInitializeClient() {
      BBSModClient.getL10n().register((lang) -> {
         return Collections.singletonList(new Link("bbs-structureform", "lang/" + lang + ".json"));
      });
      BBSModClient.getL10n().reload();
   }
}
