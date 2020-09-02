package net.xilla.discordcore.command.cmd;

import com.tobiassteely.tobiasapi.command.Command;
import com.tobiassteely.tobiasapi.command.CommandExecutor;
import net.xilla.discordcore.CoreObject;
import net.xilla.discordcore.command.CommandBuilder;
import net.xilla.discordcore.command.response.CoreCommandResponse;

import java.util.ArrayList;

public class RestartCommand extends CoreObject {

    public RestartCommand() {
        restartCommand();
    }

    public void restartCommand() {
        CommandBuilder builder = new CommandBuilder("Core", "Restart");
        builder.setDescription("Perform a soft-restart on the bot.");
        builder.setPermission("core.restart");
        builder.setCommandExecutor((data) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getDiscordCore().restart();
            }).start();
            return new CoreCommandResponse(data).setDescription("Restarting the bot in 3 seconds! Please keep in mind, this does not update the core.");
        });
        builder.build();
    }

}