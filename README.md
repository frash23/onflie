Onflie
===
Mixed online-mode for BungeeCord

What?
---
This plugin allows both online and offline players to log in.
It does **not** require you to use offline-mode for your server! The server runs in online-mode as usual, but uses a
custom authentication method for offline-mode players.

How?
---
By replacing the standard authentication method, we can differentiate online and offline-mode players (offline players
will not have a valid session ID). We use this to *turn offline player's connection into offline-mode* - this means
each player has their own "online-mode" value (instead of one, server-wide one). Online players can use online-mode as
normal, offline-mode users use offline-mode as expected.

What about name collisions?
---
Not to worry! Onflie allows you to choose two different methods of filtering out offline-mode names:
* Compare username against existing player data
* Check if username exists as premium username on Mojang's servers

The first will deny offline-mode players from logging in if a player.day for the same username with a valid session
exists. The second will deny offline players logging in with a username that exists as a premium username.

Both options have configurable disconnect messages.

What about UUID collisions?
---
This is where it gets interesting!

Onflie sets a **custom UUID** for offline-mode players.
The username is encoded in 96 bits. Yes, the longest possible username is 128 bits, **however** you don't need 8 bits
to represent a character in a Minecraft username as the range of characters is limited (36 in total, if you don' count
upper-case letters). This lets me encode each character with 6 bits, resulting in a total of 96 bits.

What about the last 32 bits? The first 32 bits of an Onflie UUID is a static piece of data plugins can use to recognize
offline-mode players. An Onflie UUID thus consists of 32 bits that always remain the same, then 96 bits representing the
username.

An Onflie UUID looks like this: `babecafe-aab1-40e2-8ccd-e145f4a64fab`, so just check if a UUID starts with `babecafe`
to check if it's an offline-mode player.