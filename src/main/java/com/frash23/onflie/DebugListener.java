package com.frash23.onflie;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DebugListener implements Listener {
    private Onflie plugin;

    DebugListener(Onflie pl) {
        plugin = pl;
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        e.registerIntent(plugin);
        PendingConnection conn = e.getConnection();
        System.out.println( conn.getName() + " joined with UUID " + conn.getUniqueId().toString() );
        System.out.println( conn.getName() + "'s online-mode is: " + conn.isOnlineMode() );

        e.completeIntent(plugin);
    }
}
