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

				conn.setUniqueId( onflieId(username) );

			/* If the username is valid and shows up on mojang's servers, assume it's valid */
			} else conn.setOnlineMode(true);
		}
		catch(MalformedURLException ignored) { /* We already verified the username so we'll ignore malformed URL errors */ }
		catch(IOException err) {  }

		e.completeIntent(plugin);
	}

	final private static String babecafe = "10111010101111101100101011111110";
	final private static String nameChars = " abcdefghijklmnopqrstuvwxyz0123456789_";
	private UUID onflieId(String name) {

		name = "             " + name.toLowerCase();
		name = name.substring( name.length() - 16 );

		String mostSigString = babecafe;
		String leastSigString = "";
		long mostSigBits = 0xBABECAFE;
		long leastSigBits = 0;

		for(byte i=0; i<16; i++) {
			String toAdd = "00000" + Integer.toBinaryString( nameChars.indexOf( name.charAt(i) ) );
			toAdd = toAdd.substring( toAdd.length() - 6 );

			if( mostSigString.length() < 61 ) mostSigString += toAdd;
			else if( mostSigString.length() >= 64 ) leastSigString += toAdd;
			else if( mostSigString.length() == 62 ) {
				mostSigString += toAdd.substring(0, 2);
				leastSigString += toAdd.substring(2);
			}
		}

		return new UUID( binaryLong(mostSigString), binaryLong(leastSigString) );
	}

	private static long binaryLong(String in) {
		return in.substring(0, 1).equals("1")
			? Long.parseLong(in.substring(1), 2) + Long.MIN_VALUE
			: Long.parseLong(in, 2);
	}

}
