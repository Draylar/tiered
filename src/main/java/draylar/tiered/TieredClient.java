package draylar.tiered;

import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.data.AttributeDataLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TieredClient implements ClientModInitializer {

    // map for storing attributes before logging into a server
    public static final Map<Identifier, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();

    @Override
    public void onInitializeClient() {
        registerAttributeSyncHandler();
    }

    public static void registerAttributeSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Tiered.ATTRIBUTE_SYNC_PACKET, (client, play, packet, packetSender) -> {
            // save old attributes
            CACHED_ATTRIBUTES.putAll(Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes());
            Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().clear();

            // for each id/attribute pair, load it
            int size = packet.readInt();
            for(int i = 0; i < size; i++) {
                Identifier id = new Identifier(packet.readString());
                PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(packet.readString(), PotentialAttribute.class);
                Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().put(id, pa);
            }
        });
    }
}
