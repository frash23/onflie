package com.frash23.onflie;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class PreLoginListener implements Listener {
	private Onflie plugin;
	PreLoginListener(Onflie pl) { plugin = pl; }

	@EventHandler(priority = EventPriority.LOWEST)
	public void onlogin(final PreLoginEvent e) {
		e.registerIntent(plugin);

		PendingConnection conn = e.getConnection();
		conn.setOnlineMode(false);
		String username = conn.getName();

		if( !username.matches("[a-zA-Z0-9_]{3,16}") ) { e.setCancelled(true); e.setCancelReason("Invalid usernam"); }
		else try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setReadTimeout(2500);
			urlConn.setConnectTimeout(2500);

			if(urlConn.getResponseCode() == 204) {

				conn.setUniqueId( nameToOnflieId(username) );
				System.out.println("Set offline UUID");

			/* If the username is valid and shows up on mojang's servers, assume it's valid */
			} else conn.setOnlineMode(true);
		}
		catch(MalformedURLException ignored) { /* We already verified the username so we'll ignore malformed URL errors */ }
		catch(IOException err) {  }

		e.completeIntent(plugin);
	}

	/* 96 zeros we use to prefix and substring with */
	final private static String zeroPrefix = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	final private static String nameArray = "abcdefghijklmnopqrstuvwxyz0123456789_";
	private UUID offlineUuid(String name) {
		BigInteger bint = new BigInteger( new byte[16] );
//		bint.mask & 0x3F;

		return null;
	}

	private UUID nameToOnflieId(String name) {
		String encodedName = "";
		for( char c : name.toCharArray() ) {
			String binString = zeroPrefix + Integer.toBinaryString( nameArray.indexOf(c) );
			encodedName += binString.substring( binString.length() - 6 );
		}

		/* Prepend 95 zeros and truncate everything except the last 96 digits.
		 * This way the length of the name doesn't matter */
		encodedName = zeroPrefix + encodedName;
		encodedName = encodedName.substring( encodedName.length() - 96 );
		String encodedNameHex = new BigInteger(encodedName, 2).toString(16);
		String rawUuid = "BABECAFE" + encodedNameHex;
		String hyphenated = rawUuid.replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5" );

		return UUID.fromString(hyphenated);
	}


}
