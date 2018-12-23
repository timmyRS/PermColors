# PermColors

Show who's boss on your Spigot server using colors.

## Color Parsing

Colors are parsed like in any other plugin where you use an ampersand (`&`) and then a character to determine the looks of the text following it. [Here's a list of colors and styles that can be used.](https://wiki.vg/Chat#Colors)
Using two ampersands will result in a single ampersand.

## Configuration

- `schemes`: A named list of color-prefix pairs
  - `default`: The scheme applied to anyone without another scheme applied to them.
  - `op`: The scheme applied to OPs.
  - Other keys can be used to define custom schemes which you can give players by giving them the `permcolors.scheme.<scheme>` permission.
- `playerList`
  - `enabled`: Modify player list? (true/false)
  - `format`: How player names are formatted in the player list.
- `chat`:
  - `enabled`: Modify chat messages? (true/false)
  - `message`: The message sent when a player sends a chat message.
- `join`:
  - `enabled`: Modify join messages? (true/false)
  - `message`: The message sent when a player joins the server.
- `quit`:
  - `enabled`: Modify quit messages? (true/false)
  - `message`: The message sent when a player leaves the server.

## Permissions

Permission | Description
-----------|------------
`permcolors.reload` | Allows the player to use `/permcolors reload` which reloads the config and recolors every player.
`permcolors.recolor` | Allows the player to use `/permcolors recolor <player>` so the given player's scheme will be refreshed.
`permcolors.colorfulchatmessages` | Allows the player to use color in their chat messages.

All permissions are granted only to OPs by default.
