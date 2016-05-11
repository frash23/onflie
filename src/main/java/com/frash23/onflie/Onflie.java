package com.frash23.onflie;

import net.md_5.bungee.api.plugin.Plugin;

public class Onflie extends Plugin {

	@Override public void onEnable() {
		getProxy().getPluginManager().registerListener( this, new PreLoginListener(this) );
	}
}
