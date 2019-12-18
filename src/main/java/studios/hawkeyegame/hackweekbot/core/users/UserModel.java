package studios.hawkeyegame.hackweekbot.core.users;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import studios.hawkeyedev.datasection.DataSection;
import studios.hawkeyedev.datasection.Model;
import studios.hawkeyegame.hackweekbot.HWBMain;

import java.util.List;
import java.util.Map;

public class UserModel implements Model {

    private String invited;
    private String key;
    private String voucher;

    private int banCount;
    private int joinCount;
    private int kickCount;
    private int messageCount;

    private List<String> roles;
    private List<String> warnings;

    public UserModel(String key) {
        this.invited = "";
        this.key = key;
        this.voucher = "";

        this.banCount = 0;
        this.joinCount = 0;
        this.kickCount = 0;
        this.messageCount = 0;

        this.roles = Lists.newArrayList();
        this.warnings = Lists.newArrayList();
    }

    public UserModel(String key, DataSection data) {
        this.invited = data.getString("invited");
        this.key = key;
        if (data.isSet("voucher"))
            this.voucher = data.getString("voucher");
        else
            this.voucher = "";

        this.banCount = data.getInt("ban-count");
        this.joinCount = data.getInt("join-count");
        this.kickCount = data.getInt("kick-count");
        this.messageCount = data.getInt("message-count");

        this.roles = data.getStringList("roles");
        this.warnings = data.getStringList("warnings");
    }

    public String getInvited() {
        return invited;
    }

    public void setInvited(String invited) {
        this.invited = invited;
        HWBMain.getUserRegistry().register(this);
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
        HWBMain.getUserRegistry().register(this);
    }

    public int getBanCount() {
        return banCount;
    }

    public void addBanCount() {
        banCount++;
        HWBMain.getUserRegistry().register(this);
    }

    public int getJoinCount() {
        return joinCount;
    }

    public void addJoinCount() {
        joinCount++;
        HWBMain.getUserRegistry().register(this);
    }

    public int getKickCount() {
        return kickCount;
    }

    public void addKickCount() {
        kickCount++;
        HWBMain.getUserRegistry().register(this);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void addMessageCount() {
        messageCount++;
        HWBMain.getUserRegistry().register(this);
    }

    public ImmutableList<String> getWarnings() {
        return ImmutableList.copyOf(warnings);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
        HWBMain.getUserRegistry().register(this);
    }

    public void removeWarning(String warning) {
        warnings.remove(warning);
        HWBMain.getUserRegistry().register(this);
    }

    public void clearWarnings() {
        warnings.clear();
        HWBMain.getUserRegistry().register(this);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("invited", invited);
        map.put("voucher", voucher);

        map.put("ban-count", banCount);
        map.put("join-count", joinCount);
        map.put("kick-count", kickCount);
        map.put("message-count", messageCount);

        map.put("roles", roles);
        map.put("warnings", warnings);
        return map;
    }
}
