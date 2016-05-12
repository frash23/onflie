package com.frash23.onflie;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class AuthManager {

	final private static Random random = new Random();
	static KeyPair keys;

	static {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance( "RSA" );
			generator.initialize(1024);
			keys = generator.generateKeyPair();
		} catch(NoSuchAlgorithmException ignored) { }
	}

	UUID verifyUser(PendingConnection conn) throws Exception {

		EncryptionRequest request = encryptRequst();
		//SecretKey sharedKey = getSecret(request, request);
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[][] bits = new byte[][] {
				request.getServerId().getBytes("ISO_8859_1")/*,
				keys.get*/
		};
		//for( byte[] bit : new byte[][] { conn. }) sha.update(bit);

		//String encName = URLEncoder.encode( conn.getName(), "UTF-8" );
		//String encodedHash = URLEncoder.encode( new BigInteger( sha.digest() ).toString( 16 ), "UTF-8" );
		//String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash;

		return null;
	}

	static EncryptionRequest encryptRequst() {
		String hash = Long.toString( random.nextLong(), 16 );
		byte[] pubKey = keys.getPublic().getEncoded();
		byte[] verify = new byte[4];
		random.nextBytes(verify);

		return new EncryptionRequest(hash, pubKey, verify);
	}

	static SecretKey getSecret(EncryptionResponse resp, EncryptionRequest request) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init( Cipher.DECRYPT_MODE, keys.getPrivate() );
		byte[] decrypted = cipher.doFinal( resp.getVerifyToken() );

		if( !Arrays.equals( request.getVerifyToken(), decrypted ) ) {
			throw new IllegalStateException("Key pairs do not match!");
		}

		cipher.init( Cipher.DECRYPT_MODE, keys.getPrivate() );
		return new SecretKeySpec( cipher.doFinal( resp.getSharedSecret() ), "AES" );
	}

}
