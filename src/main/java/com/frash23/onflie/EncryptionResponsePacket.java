package com.frash23.onflie;

import com.google.common.base.Preconditions;
import com.google.common.math.BigIntegerMath;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;

import javax.crypto.SecretKey;
import javax.management.RuntimeErrorException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class EncryptionResponsePacket extends EncryptionResponse {

	static Field requestField;
	static Field loginProfileField;
	static Field thisStateField;
	static Field uniqueIdField;
	static Field chField;
	static Method finishMethod;
	private static boolean accessible = false;

	static {
		try {
			requestField = InitialHandler.class.getDeclaredField("request");
			loginProfileField = InitialHandler.class.getDeclaredField("loginProfile");
			thisStateField = InitialHandler.class.getDeclaredField("thisState");
			uniqueIdField = InitialHandler.class.getDeclaredField("uniqueId");
			chField = InitialHandler.class.getDeclaredField("ch");
			finishMethod = InitialHandler.class.getDeclaredMethod("finish");
		} catch(NoSuchFieldException | NoSuchMethodException e) { /* HANDLE THIS SOMETIME PLEASE */ }
	}


	@Override
	public void handle(AbstractPacketHandler handler) throws Exception {
		if(!accessible) setAccessibility();

		EncryptionRequest request = (EncryptionRequest)requestField.get(handler);
		ChannelWrapper ch = (ChannelWrapper)chField.get(handler);
		InitialHandler conn = (InitialHandler)handler; /* CHECK THIS PROBABLY */
		//Preconditions.checkState( (InitialHandler.State)thisStateField.get(handler), "Not expecting ENCRYPT" );

		SecretKey sharedKey = EncryptionUtil.getSecret(this, request);
		BungeeCipher decrypt = EncryptionUtil.getCipher(false, sharedKey);
		ch.addBefore( PipelineUtils.FRAME_DECODER, PipelineUtils.DECRYPT_HANDLER, new CipherDecoder(decrypt) );
		BungeeCipher encrypt = EncryptionUtil.getCipher(true, sharedKey);
		ch.addBefore( PipelineUtils.FRAME_DECODER, PipelineUtils.ENCRYPT_HANDLER, new CipherEncoder(encrypt) );

		String encName = URLEncoder.encode( conn.getName(), "UTF-8" );

		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[][] bits = new byte[][] {
				request.getServerId().getBytes("ISO_8859_1"),
				sharedKey.getEncoded(),
				EncryptionUtil.keys.getPublic().getEncoded()
		};
		for(byte[] bit : bits) sha.update(bit);
		String encodedHash = URLEncoder.encode( new BigInteger( sha.digest() ).toString(16), "UTF-8" );

		String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash;

		Callback<String> httpHandler = new Callback<String>() {
			@Override public void done(String result, Throwable error) {

				System.out.println( error == null? "Setting online" : "OFFLINE D: D:D :D");
				if(error == null) {
					/* Handle online users */
					System.out.println("Got response");
					LoginResult obj = BungeeCord.getInstance().gson.fromJson(result, LoginResult.class);
					try {
						loginProfileField.set(handler, obj);
						//conn.setOnlineMode(true);
						uniqueIdField.set(handler, Util.getUUID(obj.getId()));

						System.out.println("Finishing login");
						finishMethod.invoke(handler);
					} catch (IllegalAccessException | InvocationTargetException e) { /* HANDLE THIS PLEASE */ }

				} else {
					/* Handle offline users */

					System.out.println("Error while getting auth result: " + error);
					try {
						uniqueIdField.set(handler, OnflieIdUtil.onflieId(conn.getName()));
					} catch(IllegalAccessException e) { /* HANDLE THIS MAYBE???? */ }
				}

			}
		};

		HttpClient.get( authURL, ch.getHandle().eventLoop(), httpHandler );
	}

	private void setAccessibility() {
		requestField.setAccessible(true);
		loginProfileField.setAccessible(true);
		thisStateField.setAccessible(true);
		uniqueIdField.setAccessible(true);
		chField.setAccessible(true);
		finishMethod.setAccessible(true);

		accessible = true;
	}
}
