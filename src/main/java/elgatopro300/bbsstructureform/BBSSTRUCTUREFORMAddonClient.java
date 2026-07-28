package elgatopro300.bbsstructureform;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.events.register.RegisterL10nEvent;
import mchorse.bbs_mod.resources.Link;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.Collections;

@Environment(EnvType.CLIENT)
public class BBSSTRUCTUREFORMAddonClient implements ClientModInitializer {
   private static boolean L10N_DONE = false;
   @Override
   public void onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         if (!L10N_DONE && BBSModClient.getL10n() != null) {
            BBSModClient.getL10n().register((lang) -> {
               return Collections.singletonList(new Link("bbs-structureform", "lang/" + lang + ".json"));
            });
            BBSModClient.getL10n().reload();
            L10N_DONE = true;
         }
      });
   }
}
