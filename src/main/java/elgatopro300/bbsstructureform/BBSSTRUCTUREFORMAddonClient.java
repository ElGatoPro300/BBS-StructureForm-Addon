package elgatopro300.bbsstructureform;

import elgatopro300.bbsstructureform.importers.StructureImporter;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.importers.Importers;
import mchorse.bbs_mod.resources.Link;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Collections;

@Environment(EnvType.CLIENT)
public class BBSSTRUCTUREFORMAddonClient implements ClientModInitializer {
   private static boolean L10N_DONE = false;

   @Override
   public void onInitializeClient() {
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         if (!L10N_DONE && BBSModClient.getL10n() != null) {
            try {
               BBSModClient.getL10n().register((lang) -> {
                  return Collections.singletonList(new Link("bbs-structureform", "lang/" + lang + ".json"));
               });
               BBSModClient.getL10n().reload();
               Importers.getImporters().add(new StructureImporter());
            } catch (Throwable ignored) {}
            L10N_DONE = true;
         }
      });
   }
}
