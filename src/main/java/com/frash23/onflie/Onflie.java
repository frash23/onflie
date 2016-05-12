package com.frash23.onflie;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Onflie extends Plugin {

	@Override public void onEnable() {

		/* Register our custom auth packet */
		try {
			for(int v : ProtocolConstants.SUPPORTED_VERSION_IDS) injectListener(v);
		} catch(NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException("Error while injecting auth packet", e);
		}

		System.out.println("hello friends what is up!!");
		//getProxy().getPluginManager().registerListener( this, new PreLoginListener(this) );
	}

	private void injectListener(int version) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {

		Field protocolsField = Protocol.LOGIN.TO_SERVER.getClass().getDeclaredField("protocols");
		protocolsField.setAccessible(true);
		TIntObjectMap<?> protocols = (TIntObjectMap)protocolsField.get(Protocol.LOGIN.TO_SERVER);

		Object protocolData = protocols.get(version);
		Field packetMapField = protocolData.getClass().getDeclaredField("packetMap");
		Field packetConstructorsField = protocolData.getClass().getDeclaredField("packetConstructors");
		packetMapField.setAccessible(true);
		packetConstructorsField.setAccessible(true);
		TObjectIntMap packetMap =  (TObjectIntMap)packetMapField.get(protocolData);
		TIntObjectMap packetConstructors =  (TIntObjectMap)packetConstructorsField.get(protocolData);

		packetMap.remove(EncryptionResponse.class);
		packetConstructors.remove(0x01);
		packetMap.put( EncryptionResponsePacket.class, 0x01);
		packetConstructors.put( 0x01, EncryptionResponsePacket.class.getDeclaredConstructor() );

		packetMapField.set(protocolData, packetMap);
		packetConstructorsField.set(protocolData, packetConstructors);
		protocolsField.set(Protocol.LOGIN.TO_SERVER, protocols);
	}
}
