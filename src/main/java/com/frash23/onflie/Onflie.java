package com.frash23.onflie;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Onflie extends Plugin {

	@Override public void onEnable() {

		/* Register our custom auth packet */
		try {

			Field packetMapField = Class.forName("net.md_5.bungee.protocol.Protocol$DirectionData").getDeclaredField("packetMap");
			packetMapField.setAccessible(true);
			Object reflectedPacketMap = packetMapField.get(Protocol.LOGIN.TO_SERVER);
			Method packetMapRemove = reflectedPacketMap.getClass().getDeclaredMethod("remove", Object.class);
			packetMapRemove.invoke(reflectedPacketMap, EncryptionResponse.class);

			//System.out.println(packetMap.keySet());


			Method registerPacket = Protocol.LOGIN.TO_SERVER.getClass().getDeclaredMethod("registerPacket", int.class, Class.class);
			registerPacket.setAccessible(true);

			registerPacket.invoke(Protocol.LOGIN.TO_SERVER, 0x01, EncryptionResponsePacket.class);
		} catch(NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
			throw new RuntimeException("Error while injecting auth packet", e);
		}

		System.out.println("hello friends what is up!!");
		//getProxy().getPluginManager().registerListener( this, new PreLoginListener(this) );
	}
}
