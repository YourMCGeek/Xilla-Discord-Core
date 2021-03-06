package net.xilla.discordcore;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.xilla.core.library.XillaLibrary;
import net.xilla.discordcore.command.ServerSettings;
import net.xilla.discordcore.core.permission.PermissionAPI;
import net.xilla.discordcore.core.CoreSettings;
import net.xilla.discordcore.core.Platform;
import net.xilla.discordcore.core.permission.group.DiscordGroup;
import net.xilla.discordcore.core.permission.group.GroupManager;
import net.xilla.discordcore.embed.JSONEmbed;
import net.xilla.discordcore.module.ModuleManager;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.Date;
import java.util.List;

public class CoreObject implements XillaLibrary {

    public DiscordCore getDiscordCore() {
        return DiscordCore.getInstance();
    }

    public GroupManager getGroupManager() {
        return DiscordCore.getInstance().getGroupManager();
    }

    public ModuleManager getModuleManager() {
        return DiscordCore.getInstance().getModuleManager();
    }

    public CoreSettings getCoreSetting() {
        return DiscordCore.getInstance().getSettings();
    }

    public JDA getBot() {
        return DiscordCore.getInstance().getBot();
    }

    public Platform getPlatform() {
        return DiscordCore.getInstance().getPlatform();
    }

    public ServerSettings getServerSettings() {
        return DiscordCore.getInstance().getServerSettings();
    }

    public Message getMessage(String channelID, String messageID) {
        TextChannel textChannel = getBot().getTextChannelById(channelID);
        if(textChannel != null) {
            return textChannel.retrieveMessageById(messageID).complete();
        }
        return null;
    }

    public void hasPermission(Member user, String permission) {
        PermissionAPI.hasPermission(user, permission);
    }

    public void hasPermission(Guild guild, String user, String permission) {
        PermissionAPI.hasPermission(getMember(guild, user), permission);
    }

    public User getUser(String id) {
        return DiscordAPI.getUser(id);
    }

    public Role getRole(String id) {
        Role role = null;
        String roleID = id.replace("<@&", "").replace("<@", "").replace(">", "");
        try {
            role = getBot().getRoleById(roleID);
        } catch (Exception ignored) {}

        if(role == null) {
            try {
                role = getBot().getRolesByName(id, true).get(0);
            } catch (Exception ignored) {
            }
        }
        return role;
    }

    public String getPrefix() {
        return getCoreSetting().getCommandPrefix();

    }

    public DiscordGroup getGroup(Guild guild, String name) {
        DiscordGroup group = getGroupManager().getManager(guild).get(name.replace("<@&", "").replace("<@", "").replace(">", ""));

        if(group != null) {
            return group;
        }

        List<DiscordGroup> groups = getGroupManager().getGroupsByName(name);
        if(groups != null) {
            for (DiscordGroup loopGroup : groups) {
                if (loopGroup.getServerID().equalsIgnoreCase(guild.getId())) {
                    return loopGroup;
                }
            }
        }

        return null;
    }

    public Member getMember(Guild guild, String id) {
        return DiscordAPI.getMember(guild, id);
    }

    /**
     * Deprecated to warn that this is not always 100% accurate and should only be used in admin situations
     */
    @Deprecated
    public Guild getGuild(String name) {
        Guild guild = null;

        try {
            guild = getBot().getGuildById(name);
        } catch (Exception ex) {}

        if(guild != null) {
            return guild;
        }

        List<Guild> guilds = getBot().getGuildsByName(name, false);
        if(guilds.size() > 0) {
            return guilds.get(0);
        }

        guilds = getBot().getGuildsByName(name, true);
        if(guilds.size() > 0) {
            return guilds.get(0);
        }

        return guild;
    }

    public Color getColor() {
        return Color.decode(getCoreSetting().getEmbedColor());
    }

    public Color getColor(String guildID) {
        return DiscordCore.getInstance().getServerSettings().getColor(guildID);
    }

    public Color getColor(Guild guild) {
        return DiscordCore.getInstance().getServerSettings().getColor(guild);
    }

    public boolean sendPM(User user, EmbedBuilder embedBuilder) {
        try {
            user.openPrivateChannel().complete().sendMessage(embedBuilder.build()).queue();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean sendPM(User user, String string) {
        try {
            user.openPrivateChannel().complete().sendMessage(string).queue();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public EmbedBuilder getEmbed(MessageReceivedEvent event) {
        JSONEmbed jsonEmbed = new JSONEmbed("default", (JSONObject)getServerSettings().get(event.getGuild(), "core-embed"));
        EmbedBuilder embedBuilder = jsonEmbed.getEmbedBuilder();

        MessageEmbed embed = embedBuilder.build();
        if(embed.getFooter() != null && embed.getFooter().getText() != null) {
            embedBuilder.setFooter(embed.getFooter().getText().replace("%user%", event.getAuthor().getAsTag()).replace("%date%", new Date().toString()));
        }
        embedBuilder.setColor(getColor(event.getGuild()));
        return embedBuilder;
    }

    public EmbedBuilder getEmbed(String user, Guild guild) {
        JSONEmbed jsonEmbed = new JSONEmbed("default", (JSONObject)getServerSettings().get(guild, "core-embed"));
        EmbedBuilder embedBuilder = jsonEmbed.getEmbedBuilder();

        MessageEmbed embed = embedBuilder.build();
        if(embed.getFooter() != null && embed.getFooter().getText() != null) {
            embedBuilder.setFooter(embed.getFooter().getText().replace("%user%", user).replace("%date%", new Date().toString()));
        }
        embedBuilder.setColor(getColor(guild));
        return embedBuilder;
    }

    public EmbedBuilder getEmbed(String user, String guild) {
        JSONEmbed jsonEmbed = new JSONEmbed("default", (JSONObject)getServerSettings().get(guild, "core-embed"));
        EmbedBuilder embedBuilder = jsonEmbed.getEmbedBuilder();

        MessageEmbed embed = embedBuilder.build();
        if(embed.getFooter() != null && embed.getFooter().getText() != null) {
            embedBuilder.setFooter(embed.getFooter().getText().replace("%user%", user).replace("%date%", new Date().toString()));
        }
        embedBuilder.setColor(getColor(guild));
        return embedBuilder;
    }


}
