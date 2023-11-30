package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.player.Player;

@Command(label = "energy", permission = "player.energy", desc = "/energy. Refills all characters energy in current lineup.")
public class RefillSPCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }

        PlayerLineup lineup = args.getTarget().getCurrentLineup();
        lineup.forEachAvatar(avatar -> {
            avatar.setCurrentSp(lineup, 10000);
            avatar.save();
        });
        lineup.refreshLineup();
        
        this.sendMessage(sender, "Refilled SP for " + args.getTarget().getName());
    }

}
