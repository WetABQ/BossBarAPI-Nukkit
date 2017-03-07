package top.dreamcity.bossbarapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.Map;
import java.util.UUID;

/**
 * Copyright © 2016 WetAQB&DreamCityAdminGroup All right reserved.
 * Welcome to DreamCity Server Address:dreamcity.top:19132
 * Created by WetAQB(Administrator) on 2017/2/3.
 * |||    ||    ||||                           ||        ||||||||     |||||||
 * |||   |||    |||               ||         ||  |      |||     ||   |||    |||
 * |||   |||    ||     ||||||  ||||||||     ||   ||      ||  ||||   |||      ||
 * ||  |||||   ||   |||   ||  ||||        ||| |||||     ||||||||   |        ||
 * ||  || ||  ||    ||  ||      |        |||||||| ||    ||     ||| ||      ||
 * ||||   ||||     ||    ||    ||  ||  |||       |||  ||||   |||   ||||||||
 * ||     |||      |||||||     |||||  |||       |||| ||||||||      |||||    |
 * ||||
 */
public class BossBarAPI extends PluginBase{
    private static BossBarAPI plugin;
    public static BossBarAPI getPlugin() {
        return plugin;
    }
    @Override
    public void onLoad(){
        plugin = this;
        /**
         *  乱改版权狗自重 如果您觉得这样很快乐 开心就好
         *  修改本插件请在适当地方添加您的信息 谢谢！
         */
        this.getLogger().info(TextFormat.AQUA + "Starting BossBar-API....");
        this.getLogger().notice("Copyright © WetABQ rights reserved.");
        this.getLogger().info(TextFormat.AQUA + "正在加载BossBar-API 插件");
        this.getLogger().notice("版权所有 © WetABQ .保留所有权利.");
    }
    @Override
    public void onEnable() {
        this.getLogger().notice("BossBarAPI enable");
        this.getLogger().warning("注意: 本插件只是作用于其他插件的牵制插件无实质作用");
        this.getLogger().warning("加载本插件如果没有装调用本插件的插件则是浪费空间！");
    }
    /**
     * @param players ServerPlayers
     * @param title   Text
     * @return EntityId
     */
    public static long addBossBar(Map<UUID, Player> players, String title) {
        if (players.isEmpty()) return 1L;
        long eid = Entity.entityCount++;
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityRuntimeId = pk.entityUniqueId = eid;
        pk.type = 52;
        pk.yaw = 0;
        pk.pitch = 0;
        EntityMetadata metadata = new EntityMetadata()
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putLong(Entity.DATA_FLAGS, 0 ^ 1 << Entity.DATA_FLAG_SILENT ^ 1 << Entity.DATA_FLAG_INVISIBLE ^ 1 << Entity.DATA_FLAG_NO_AI)
                .putFloat(Entity.DATA_SCALE, 0)
                .putString(Entity.DATA_NAMETAG, title)
                .putString(Entity.DATA_TYPE_STRING, title)
                .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0)
                .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0);
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.metadata = metadata;
        AddEntityPacket pkp;
        Player player;
        for (Map.Entry<UUID, Player> entry : players.entrySet()) {
            player = entry.getValue();
            pkp = (AddEntityPacket) pk.clone();
            pkp.x = (float) player.getX();
            pkp.y = (float) player.getY() - 28;
            pkp.z = (float) player.getZ();
            player.dataPacket(pkp);
            pkp.clean();
            pkp = null;
        }
        BossEventPacket bpk = new BossEventPacket(); // This updates the bar
        bpk.eid = eid;
        bpk.type = 0;
        Server.broadcastPacket(players.values(), bpk);
        return eid;
    }

    /**
     * @param player Who Player
     * @param eid    EntityId
     * @param title  Text
     */
    public static void sendBossBarToPlayer(Player player, long eid, String title) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.entityRuntimeId = packet.entityUniqueId = eid;
        packet.type = 52;
        packet.yaw = 0;
        packet.pitch = 0;
        EntityMetadata metadata = new EntityMetadata()
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putLong(Entity.DATA_FLAGS, 0 ^ 1 << Entity.DATA_FLAG_SILENT ^ 1 << Entity.DATA_FLAG_INVISIBLE ^ 1 << Entity.DATA_FLAG_NO_AI)
                .putFloat(Entity.DATA_SCALE, 0)
                .putString(Entity.DATA_NAMETAG, title)
                .putString(Entity.DATA_TYPE_STRING, title)
                .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0)
                .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0);
        packet.metadata = metadata;
        packet.x = (float) player.getX();
        packet.y = (float) player.getY();
        packet.z = (float) player.getZ();
        packet.speedX = 0;
        packet.speedY = 0;
        packet.speedZ = 0;
        player.dataPacket(packet);
        BossEventPacket bpk = new BossEventPacket();
        bpk.eid = eid;
        bpk.type = 0;
        player.dataPacket(bpk);
    }

    /**
     * @param title Text
     * @param eid   EntityId
     */
    public static void setTitle(String title, long eid) {
        if (BossBarAPI.getPlugin().getServer().getOnlinePlayers().keySet().toArray().length < 0) return;
        SetEntityDataPacket npk = new SetEntityDataPacket();
        npk.metadata = new EntityMetadata()
                .putString(Entity.DATA_NAMETAG, title);
        npk.eid = eid;
        Server.broadcastPacket(BossBarAPI.getPlugin().getServer().getOnlinePlayers().values(), npk);
        BossEventPacket bpk = new BossEventPacket(); // This updates the bar
        bpk.eid = eid;
        bpk.type = 0;
        Server.broadcastPacket(BossBarAPI.getPlugin().getServer().getOnlinePlayers().values(), bpk);
    }

    /**
     * @param players ServerPlayers
     * @param eid     EntityId
     * @return Boolean
     */
    public static boolean removeBossBar(Map<UUID, Player> players, long eid) {
        if (players.isEmpty()) return false;
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = eid;
        Server.broadcastPacket(players.values(), pk);
        return true;
    }

    /**
     * @param pos Location
     * @param eid EntityId
     * @return MoveEntityPacket
     */
    public static MoveEntityPacket playerMove(Location pos, long eid) {
        MoveEntityPacket pk = new MoveEntityPacket();
        pk.x = pos.x;
        pk.y = pos.y;
        pk.z = pos.z;
        pk.eid = eid;
        pk.yaw = pk.pitch = pk.headYaw = 0;
        return pk;
    }
}
