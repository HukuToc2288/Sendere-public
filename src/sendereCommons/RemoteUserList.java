package sendereCommons;

import java.util.ArrayList;

public class RemoteUserList extends ArrayList<RemoteUser> {
    public RemoteUser getByHash(long hash) {
        for (RemoteUser user: this){
            if (user != null && user.getHash() == hash)
                return user;
        }
        return null;
    }

    public void put(RemoteUser sender) {
        int size = size();
        for (int i=0; i<size; i++){
            if (get(i) == null){
                set(i, sender);
                return;
            }
        }
        add(sender);
    }

    public void removeByHash(long hash){
        set(indexOf(getByHash(hash)), null);
        if (get(size()-1) == null){
            remove(size()-1);
        }
    }
}
