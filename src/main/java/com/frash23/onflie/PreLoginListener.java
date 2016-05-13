package com.frash23.onflie;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.IOException;
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
		else e.completeIntent(plugin);
	}

}
