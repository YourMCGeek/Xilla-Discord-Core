package net.xilla.discordcore.core.staff;

import com.tobiassteely.tobiasapi.api.manager.ManagerCache;
import com.tobiassteely.tobiasapi.api.manager.ManagerParent;
import com.tobiassteely.tobiasapi.config.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.xilla.discordcore.DiscordCore;
import org.json.simple.JSONObject;

import java.util.*;

public class GroupManager extends ManagerParent<Group> {

    public GroupManager() {
        super(true);
    }
    private HashMap<String, List<Group>> serverCache;

    @Override
    public void reload() {
        super.reload();

        addCache("id", new ManagerCache());

        Config config = DiscordCore.getInstance().getTobiasAPI().getConfigManager().getConfig("staff/groups.json");

        JSONObject userDefault = new JSONObject();
        userDefault.put("groupID", "default applies to all users");
        userDefault.put("name", "Default");
        userDefault.put("permissions", Arrays.asList(""));
        config.loadDefault("Default", userDefault);

        JSONObject adminDefault = new JSONObject();
        adminDefault.put("groupID", "example");
        adminDefault.put("name", "Admin");
        adminDefault.put("permissions", Arrays.asList("*"));
        config.loadDefault("Admin", adminDefault);

        config.save();

        JSONObject json = config.toJson();
        for(Object key : json.keySet()) {
            Group staff = new Group((Map<String, Object>)config.get(key.toString()));
            addGroup(staff);
        }
    }

    public void save() {
        Config config = DiscordCore.getInstance().getTobiasAPI().getConfigManager().getConfig("staff/groups.json");
        for(Group staff : getList()) {
            config.toJson().put(staff.getKey(), staff.toJson());
        }
        config.save();
    }

    public ArrayList<Group> getStaffByUserId(Guild guild, String id) {
        ArrayList<Group> staffList = new ArrayList<>();
        for(Group staff : getList()) {
            if(staff.isMember(guild, id)) {
                staffList.add(staff);
            }
        }
        return staffList;
    }

    public Group getGroup(String name) {
        return (Group)getObject(name);
    }

    public Group getGroupByID(String id) {
        return (Group)getCache("id").getObject(id);
    }

    public void addGroup(Group staff) {
        super.addObject(staff);

        getCache("id").putObject(staff.getGroupID(), staff);
    }
    
}
