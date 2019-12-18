package studios.hawkeyegame.hackweekbot.core.servers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Model;
import studios.hawkeyegame.hackweekbot.HWBMain;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ServerModel implements Model {

    private String color;
    private String commandLogChannel;
    private String commandPrefix;
    private String footer;
    private String gameCategoryId;
    private String key;
    private String logChannel;
    private String unverifiedRole;
    private String verifiedRole;
    private String welcomeMessage;

    private List<String> autoRoles;
    private List<String> inviteRoles;
    private List<String> invites;
    private List<String> vouchRoles;

    public ServerModel(String key) {
        this.color = "FFFFFF";
        this.commandLogChannel = "";
        this.commandPrefix = ".";
        this.footer = "Footer";
        this.gameCategoryId = "";
        this.key = key;
        this.logChannel = "";
        this.unverifiedRole = "";
        this.verifiedRole = "";
        this.welcomeMessage = "Welcome %USER% to %SERVER%";

        this.autoRoles = Lists.newArrayList();
        this.inviteRoles = Lists.newArrayList();
        this.invites = Lists.newArrayList();
        this.vouchRoles = Lists.newArrayList();
    }

    public ServerModel(String key, DataSection data) {
        this.color = data.getString("color");
        if (data.isSet("command-log-channel"))
            this.commandLogChannel = data.getString("command-log-channel");
        else
            this.commandLogChannel = "";
        this.commandPrefix = data.getString("command-prefix");
        this.footer = data.getString("footer");
        if (data.isSet("game-category-id"))
            this.gameCategoryId = data.getString("game-category-id");
        else
            this.gameCategoryId = "";
        this.key = key;
        this.logChannel = data.getString("log-channel");
        if (data.isSet("unverified-role"))
            this.unverifiedRole = data.getString("unverified-role");
        else
            this.unverifiedRole = "";
        if (data.isSet("verified-role"))
            this.verifiedRole = data.getString("verified-role");
        else
            this.verifiedRole = "";
        if (data.isSet("welcome-message"))
            this.welcomeMessage = data.getString("welcome-message");
        else
            this.welcomeMessage = "Welcome %USER% to %SERVER%";

        this.autoRoles = data.getStringList("auto-roles");
        this.inviteRoles = data.getStringList("invite-roles");
        this.invites = data.getStringList("invites");
        if (data.isSet("vouch-roles"))
            this.vouchRoles = data.getStringList("vouch-roles");
        else
            this.vouchRoles = Lists.newArrayList();
    }

    public String getColorHex() {
        return color;
    }

    public Color getColor() {
        return Color.decode("#" + getColorHex());
    }

    public void setColorHex(String color) {
        this.color = color;
        HWBMain.getServerRegistry().register(this);
    }

    public String getCommandLogChannel() {
        return commandLogChannel;
    }

    public void setCommandLogChannel(String commandLogChannel) {
        this.commandLogChannel = commandLogChannel;
        HWBMain.getServerRegistry().register(this);
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
        HWBMain.getServerRegistry().register(this);
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
        HWBMain.getServerRegistry().register(this);
    }

    public String getGameCategoryId() {
        return gameCategoryId;
    }

    public void setGameCategoryId(String gameCategoryId) {
        this.gameCategoryId = gameCategoryId;
        HWBMain.getServerRegistry().register(this);
    }

    public String getLogChannel() {
        return logChannel;
    }

    public void setLogChannel(String logChannel) {
        this.logChannel = logChannel;
        HWBMain.getServerRegistry().register(this);
    }

    public String getUnverifiedRole() {
        return unverifiedRole;
    }

    public void setUnverifiedRole(String unverifiedRole) {
        this.unverifiedRole = unverifiedRole;
        HWBMain.getServerRegistry().register(this);
    }

    public String getVerifiedRole() {
        return verifiedRole;
    }

    public void setVerifiedRole(String verifiedRole) {
        this.verifiedRole = verifiedRole;
        HWBMain.getServerRegistry().register(this);
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
        HWBMain.getServerRegistry().register(this);
    }

    public ImmutableList<String> getAutoRoles() {
        return ImmutableList.copyOf(autoRoles);
    }

    public void addRole(String role) {
        autoRoles.add(role);
        HWBMain.getServerRegistry().register(this);
    }

    public void addRole(Role role) {
        addRole(role.getIdAsString());
    }

    public void removeRole(String role) {
        autoRoles.remove(role);
        HWBMain.getServerRegistry().register(this);
    }

    public void removeRole(Role role) {
        removeRole(role.getIdAsString());
    }

    public ImmutableList<String> getInviteRoles() {
        return ImmutableList.copyOf(inviteRoles);
    }

    public void addInviteRole(String inviteRole) {
        inviteRoles.add(inviteRole);
        HWBMain.getServerRegistry().register(this);
    }

    public void addInviteRole(Role role) {
        addInviteRole(role.getIdAsString());
    }

    public void removeInviteRole(String inviteRole) {
        inviteRoles.remove(inviteRole);
        HWBMain.getServerRegistry().register(this);
    }

    public void removeInviteRole(Role role) {
        removeInviteRole(role.getIdAsString());
    }

    public boolean hasInviteRole(Server server, User user) {
        for (Role role : server.getRoles(user))
            if (getInviteRoles().contains(role.getIdAsString()))
                return true;
        return false;
    }

    public ImmutableList<String> getInvites() {
        return ImmutableList.copyOf(invites);
    }

    public void addInvite(String invite) {
        invites.add(invite);
        HWBMain.getServerRegistry().register(this);
    }

    public void addInvite(User user) {
        addInvite(user.getIdAsString());
    }

    public void removeInvite(String invite) {
        invites.remove(invite);
        HWBMain.getServerRegistry().register(this);
    }

    public void removeInvite(User user) {
        removeInvite(user.getIdAsString());
    }

    public boolean isInvited(String invite) {
        return invites.contains(invite);
    }

    public boolean isInvited(User user) {
        return isInvited(user.getIdAsString());
    }

    public ImmutableList<String> getVouchRoles() {
        return ImmutableList.copyOf(vouchRoles);
    }

    public void addVouchRole(String inviteRole) {
        vouchRoles.add(inviteRole);
        HWBMain.getServerRegistry().register(this);
    }

    public void addVouchRole(Role role) {
        addVouchRole(role.getIdAsString());
    }

    public void removeVouchRole(String inviteRole) {
        vouchRoles.remove(inviteRole);
        HWBMain.getServerRegistry().register(this);
    }

    public void removeVouchRole(Role role) {
        removeVouchRole(role.getIdAsString());
    }

    public boolean hasVouchRole(Server server, User user) {
        for (Role role : server.getRoles(user))
            if (getVouchRoles().contains(role.getIdAsString()))
                return true;
        return false;
    }

    public String getKey() {
        return key;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("color", color);
        map.put("command-log-channel", commandLogChannel);
        map.put("command-prefix", commandPrefix);
        map.put("footer", footer);
        map.put("game-category-id", gameCategoryId);
        map.put("log-channel", logChannel);
        map.put("unverified-role", unverifiedRole);
        map.put("verified-role", verifiedRole);
        map.put("welcome-message", welcomeMessage);

        map.put("auto-roles", autoRoles);
        map.put("invite-roles", inviteRoles);
        map.put("invites", invites);
        map.put("vouch-roles", vouchRoles);
        return map;
    }
}