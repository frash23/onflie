package com.frash23.onflie;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(callSuper = false)
public class EncryptionResponsePacket extends EncryptionResponse {

	static Field requestField;
	static Field thisStateField;
	static Field loginProfileField;
	static Method finishMethod;

	static {
		try {
			requestField = InitialHandler.class.getDeclaredField("request");
			loginProfileField = InitialHandler.class.getDeclaredField("loginProfile");
			thisStateField = InitialHandler.class.getDeclaredField("thisState");
			finishMethod = InitialHandler.class.getDeclaredMethod("finish");

			System.out.println( InitialHandler.class.getDeclaredField("State") );

			requestField.setAccessible(true);
			loginProfileField.setAccessible(true);
			thisStateField.setAccessible(true);
			finishMethod.setAccessible(true);
		} catch(NoSuchFieldException | NoSuchMethodException e) { /* HANDLE THIS SOMETIME PLEASE */ }
	}

	@Override
	public void handle(AbstractPacketHandler handler) throws Exception {
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
}
