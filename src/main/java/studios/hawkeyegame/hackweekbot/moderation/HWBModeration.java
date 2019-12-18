package studios.hawkeyegame.hackweekbot.moderation;

import studios.hawkeyegame.hackweekbot.core.plugin.IPlugin;
import studios.hawkeyegame.hackweekbot.moderation.commands.*;
import studios.hawkeyegame.hackweekbot.moderation.listeners.MemberJoinListener;

public class HWBModeration implements IPlugin {

    private boolean enabled = false;

    @Override
    public void onEnable() {

        // REGISTER COMMANDS
        //getCommandManager().getCommand("ban").setExecutor(new BanCommand());
        //getCommandManager().getCommand("invite").setExecutor(new InviteCommand());
        //getCommandManager().getCommand("inviterole").setExecutor(new InviteRoleCommand());
        getCommandManager().getCommand("autorole").setExecutor(new AutoRoleCommand()).addAlias("ar");
        getCommandManager().getCommand("ban").setExecutor(new BanCommand());
        getCommandManager().getCommand("kick").setExecutor(new KickCommand());
        getCommandManager().getCommand("sayembed").setExecutor(new SayEmbedCommand()).addAlias("sayem");
        getCommandManager().getCommand("spam").setExecutor(new SpamCommand()).setCooldown(60);
        getCommandManager().getCommand("vouchrole").setExecutor(new VouchRoleCommand());
        getCommandManager().getCommand("unverifiedrole").setExecutor(new UnverifiedRoleCommand());
        getCommandManager().getCommand("verifiedrole").setExecutor(new VerifiedRoleCommand());
        getCommandManager().getCommand("verify").setExecutor(new VerifyCommand());
        getCommandManager().getCommand("voucher").setExecutor(new VoucherCommand());
        getCommandManager().getCommand("userinfo").setExecutor(new UserInfoCommand()).addAlias("ui");

        // REGISTER LISTENERS
        getListenerManager().registerListener(new MemberJoinListener());

        enabled = true;
    }

    @Override
    public void onDisable() {

        enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "Moderation";
    }
}
