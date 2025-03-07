package emu.lunarcore.game.inventory;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemUseExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;

public class ItemUseHandler {
    
    public static boolean handleFixedRewardGift(Player player, ItemUseExcel excel, int avatarId, int count) {
        // Sanity check
        if (excel.getUseParam() == null) {
            return false;
        }
        
        // Add reward items
        for (int rewardId : excel.getUseParam()) {
            var rewardExcel = GameData.getRewardExcelMap().get(rewardId);
            if (rewardExcel == null) continue;
            
            player.getInventory().addItemParams(rewardExcel.getRewards(), count);
        }
        
        return true;
    }
    
    public static boolean handleTeamSpecificFoodBenefit(Player player, ItemUseExcel excel, int avatarId, int count) {
        // Get lineup
        var lineup = player.getCurrentLineup();
        
        // TODO check if we can give dead avatars food
        
        // Add hp
        if (excel.getPreviewHPRecoveryPercent() != 0) {
            GameAvatar avatar = player.getAvatarById(avatarId);
            if (avatar == null) return false;
            
            int amount = (int) (excel.getPreviewHPRecoveryPercent() * 10000);
            avatar.setCurrentHp(lineup, avatar.getCurrentHp(lineup) + amount);
            
            // Clamp so our avatar doesnt die
            if (avatar.getCurrentHp(lineup) <= 0) {
                avatar.setCurrentHp(lineup, 100);
            }
            
            avatar.save();
        }
        
        // Add avatar energy
        if (excel.getPreviewPowerPercent() != 0) {
            GameAvatar avatar = player.getAvatarById(avatarId);
            if (avatar == null) return false;
            
            int amount = (int) (excel.getPreviewHPRecoveryPercent() * 10000);
            avatar.setCurrentSp(lineup, avatar.getCurrentHp(lineup) + amount);
            avatar.save();
        }

        // Add lineup technique points
        if (excel.getPreviewSkillPoint() > 0) {
            lineup.addMp(excel.getPreviewSkillPoint());
        }
        
        // Refresh
        lineup.refreshLineup();
        return true;
    }
    
    public static boolean handleExternalSystemFoodBenefit(Player player, ItemUseExcel excel, int avatarId, int count) {
        // Add food buffs
        player.getFoodBuffs().put(excel.getConsumeType(), excel.getMazeBuffID());
        player.save();
        
        // TODO send buff refresh packet
        
        return true;
    }
}
