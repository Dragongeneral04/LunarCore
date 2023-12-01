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
    
    private int getRandomWeaponsId(String type) {
         List<Integer> weaponsId = List.of();
        if (type.equalsIgnoreCase("yellow")) {
            weaponsId = List.of(23000, 23002, 23003, 23004, 23005, 23012, 23013); // 5 star Weapons
        } else if (type.equalsIgnoreCase("purple")) {
            weaponsId = List.of(21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21015, 21016, 21017, 21018, 21019, 21020);
        } 
        int randomIndex = ThreadLocalRandom.current().nextInt(weaponsId.size());
        return weaponsId.get(randomIndex);
    }
    
   
    // TODO filler
    public List<GameItem> calculateDropsFromProp(int propId, int playerLevel) {
        List<GameItem> drops = new ArrayList<>();
        LunarCore.getLogger().info("propId: " + propId + ", playerLevel: " + playerLevel);

        double baseDropRate = 0.10;
        double levelIncreaseRate = 0.001; 
        double totalDropRate = baseDropRate + (playerLevel * levelIncreaseRate);

        if (Utils.randomChance(totalDropRate * 100)) {
            if(Utils.randomChance(50)){
                int randomYellowWeapon = getRandomWeaponsId("yellow");
                drops.add(new GameItem(randomYellowWeapon, 1));
            } 
            else {
                drops.add(new GameItem(1, Utils.randomRange(100,500)));
                int randomPurpleWeapon = getRandomWeaponsId("purple");
                drops.add(new GameItem(randomPurpleWeapon, 1));
            }

        }  

        drops.add(new GameItem(GameConstants.MATERIAL_HCOIN_ID, 5));
        drops.add(new GameItem(GameConstants.TRAILBLAZER_EXP_ID, 5));
        drops.add(new GameItem(GameConstants.MATERIAL_COIN_ID, Utils.randomRange(20, 100)));
        
        return drops;
    }
}
