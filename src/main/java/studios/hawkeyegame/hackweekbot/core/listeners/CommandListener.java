package studios.hawkeyegame.hackweekbot.core.listeners;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import studios.hawkeyegame.hackweekbot.HWBMain;
import studios.hawkeyegame.hackweekbot.core.command.Command;
import studios.hawkeyegame.hackweekbot.core.cooldown.CooldownManager;
import studios.hawkeyegame.hackweekbot.core.listener.IListener;
import studios.hawkeyegame.hackweekbot.core.servers.ServerModel;

import java.time.Instant;
import java.util.Optional;

public class CommandListener implements MessageCreateListener, IListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isBotUser())return;

        if (event.getMessageContent().equalsIgnoreCase(getDiscordBot().getYourself().getMentionTag() + " ily")) {
            event.getChannel().sendMessage("ily2");
        }

        Optional<Server> opServer = event.getServer();
        if (opServer.isPresent()) {
            Server server = opServer.get();
            ServerModel serverModel = getServerRegistry().getServer(server.getIdAsString());

            String message = event.getMessageContent();
            if (message.startsWith(serverModel.getCommandPrefix())) {
                String[] split = message.split(" ");
                String cmdLabel = split[0].replaceFirst(serverModel.getCommandPrefix(), "");
                StringBuilder builder = new StringBuilder();

                String[] args = new String[]{};
                String argString = "";
                if (split.length > 1) {
                    for (int i = 1; i < split.length; i++) {
                        builder.append(split[i]).append(" ");
                    }
                    argString = builder.toString().substring(0, builder.toString().length() - 1);
                    args = argString.split(" ");
                }
                TextChannel channel = event.getChannel();
                if (HWBMain.getCommandManager().commandRegistered(cmdLabel)) {
                    Command command;
                    if (getCommandManager().isAlias(cmdLabel)) {
                        command = getCommandManager().getCommandFromAlias(cmdLabel);
                    } else {
                        command = getCommandManager().getCommand(cmdLabel);
                    }

                    //if (!CooldownManager.cooledDown(command.getCommand())) {
                    //    EmbedBuilder cooldown = new EmbedBuilder()
                    //            .setDescription("There is another " + CooldownManager.remainging(command.getCommand()) + " seconds left before this command can be ran again.")
                    //            .setColor(serverModel.getColor())
                    //            .setFooter(serverModel.getFooter())
                    //            .setTimestamp(Instant.now());
                    //    channel.sendMessage(cooldown);
                    //    return;
                    //} else if (CooldownManager.cooledDown(command.getCommand())) {
                    //    CooldownManager.removeCooldown(command.getCommand());
                    //}

                    if (getCommandManager().callCommand(event.getMessageAuthor(), command, event.getMessage(), channel, args)) {
                        if (command.getCooldown() > 0) {
                            CooldownManager.newCooldown(command.getCommand(), command.getCooldown());
                        }
                    }

                    if (!serverModel.getCommandLogChannel().isEmpty()) {
                        Optional<ServerTextChannel> opCommandLogChannel = getChannel(serverModel.getCommandLogChannel()).asServerTextChannel();
                        if (opCommandLogChannel.isPresent() && opCommandLogChannel.get() != null) {
                            ServerTextChannel commandLogChannel = opCommandLogChannel.get();
                            EmbedBuilder log = new EmbedBuilder()
                                    .setTitle("Command Execution Log")
                                    .addField("User", event.getMessageAuthor().asUser().get().getMentionTag() + " (" + event.getMessageAuthor().asUser().get().getDiscriminatedName() + ")")
                                    .addField("Channel", (channel.asServerTextChannel().isPresent() ? channel.asServerTextChannel().get().getMentionTag() + " (" + channel.getIdAsString() + ")" : channel.getIdAsString()))
                                    .addInlineField("Command", command.getCommand())
                                    .addInlineField("Arguments", argString)
                                    .setColor(serverModel.getColor())
                                    .setFooter(serverModel.getFooter())
                                    .setTimestamp(Instant.now());
                            commandLogChannel.sendMessage(log);
                        }
                    }
                    event.getMessage().delete();
                }
            }
        }
    }
}
