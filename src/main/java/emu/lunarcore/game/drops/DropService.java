package emu.lunarcore.game.drops;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.LunarCore;
import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class DropService extends BaseGameService {

    public DropService(GameServer server) {
        super(server);
    }

    public void calculateDrops(Battle battle) {
        // TODO this isnt the right way drops are calculated on the official server... but its good enough for now
        if (battle.getNpcMonsters().size() == 0) {
            return;
        }
        
        var dropMap = new Int2IntOpenHashMap();
        
        // Get drops from monsters
        for (EntityMonster monster : battle.getNpcMonsters()) {
            var dropExcel = GameData.getMonsterDropExcel(monster.getExcel().getId(), monster.getWorldLevel());
            if (dropExcel == null || dropExcel.getDisplayItemList() == null) {
                continue;
            }
            
            
            for (ItemParam param : dropExcel.getDisplayItemList()) {
                int id = param.getId();
                int count = Utils.randomRange(0, 3);
                
                if (id == 2) {
                    count = dropExcel.getAvatarExpReward();
                }
                
                dropMap.put(id, count + dropMap.get(id));
            }
        }
        
        for (var entry : dropMap.int2IntEntrySet()) {
            if (entry.getIntValue() <= 0) {
                continue;
            }
            
            // Create item and add it to player
            GameItem item = new GameItem(entry.getIntKey(), entry.getIntValue());

            if (battle.getPlayer().getInventory().addItem(item)) {
                battle.getDrops().add(item);
            }
        }
    }
    
    
    
    private String getPropName(int propId) {
        if (propId == 60001 || propId == 60004 || propId == 60101 || propId == 60104 || propId == 60201 || propId == 60204) {
            return "Basic Treasure";
        } else if (propId == 60002 || propId == 60005 || propId == 60102 || propId == 60105 || propId == 60202 || propId == 60205) {
            return "Bountiful Treasure";
        } else if (propId == 60003 || propId == 60006 || propId == 60103 || propId == 60106 || propId == 60203 || propId == 60206) {
            return "Precious Treasure";
        } else {
            return "Unknown";
        }
    }

    // TODO filler
    public List<GameItem> calculateDropsFromProp(int propId) {
        List<GameItem> drops = new ArrayList<>();
        LunarCore.getLogger().info("propId: " + propId);

        String propName = getPropName(propId);
        switch (propName) {
        case "Basic Treasure":
            calculateDropsForBasicTreasure(drops);
            break;
        case "Bountiful Treasure":
            calculateDropsForBountifulTreasure(drops);
            break;
        case "Precious Treasure":
            calculateDropsForPreciousTreasure(drops);
            break;
        case "Unknown":
            LunarCore.getLogger().info("Unknown Prop:" + propId);
            break;
        }
        return drops;
    }
    private void calculateDropsForBasicTreasure(List<GameItem> drops) {
        drops.add(new GameItem(GameConstants.MATERIAL_HCOIN_ID, 5));
        drops.add(new GameItem(GameConstants.TRAILBLAZER_EXP_ID, 5));
        drops.add(new GameItem(GameConstants.MATERIAL_COIN_ID, Utils.randomRange(20, 100)));
    }
    private void calculateDropsForBountifulTreasure(List<GameItem> drops) {
        drops.add(new GameItem(GameConstants.MATERIAL_HCOIN_ID, Utils.randomRange(50, 150)));
        drops.add(new GameItem(GameConstants.TRAILBLAZER_EXP_ID, Utils.randomRange(10, 30)));
        drops.add(new GameItem(GameConstants.MATERIAL_COIN_ID, Utils.randomRange(50, 100)));
    }
    private void calculateDropsForPreciousTreasure(List<GameItem> drops) {
        drops.add(new GameItem(GameConstants.MATERIAL_HCOIN_ID, Utils.randomRange(100, 200)));
        drops.add(new GameItem(GameConstants.TRAILBLAZER_EXP_ID, Utils.randomRange(15, 50)));
        drops.add(new GameItem(GameConstants.MATERIAL_COIN_ID, Utils.randomRange(80, 150)));
    }

}
