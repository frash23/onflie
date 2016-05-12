package com.frash23.onflie;

import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EncryptionResponsePacket extends EncryptionResponse {

	static Field requestField;
	static Field thisStateField;
	static Field loginProfileField;
	static Method finishMethod;
	private static boolean accessible = false;

	static {
		try {
			requestField = InitialHandler.class.getDeclaredField("request");
			loginProfileField = InitialHandler.class.getDeclaredField("loginProfile");
			thisStateField = InitialHandler.class.getDeclaredField("thisState");
			finishMethod = InitialHandler.class.getDeclaredMethod("finish");
		} catch(NoSuchFieldException | NoSuchMethodException e) { /* HANDLE THIS SOMETIME PLEASE */ }
	}


	@Override
	public void handle(AbstractPacketHandler handler) throws Exception {
		if(!accessible) setAccessibility();
		//requestField.setAccessible(true);
		//loginProfileField.setAccessible(true);
		//thisStateField.setAccessible(true);
		//finishMethod.setAccessible(true);
		//EncryptionRequest request = (EncryptionRequest)requestField.get(handler);

		//InitialHandler.State.HANDSHAKE
		//Preconditions.checkState( s)

		//SecretKey sharedKey = EncryptionUtil.getSecret(this, request);
		//BungeeCipher decrypt = EncryptionUtil.getCipher(false, sharedKey);


		//finishMethod.invoke(handler);

		handler.handle( this );
	}

	private void setAccessibility() {
		requestField.setAccessible(true);
		loginProfileField.setAccessible(true);
		thisStateField.setAccessible(true);
		finishMethod.setAccessible(true);

		accessible = true;
	}
}
