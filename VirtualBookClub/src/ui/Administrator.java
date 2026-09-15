
package ui;

import java.util.Map;

public class Administrator extends Reader{
    public Administrator(String username, String email, String password) { 
        super(username, email, password); 
        this.role = "System Administrator";
    }

    
    
    public void deleteUser(String userToDelete, Map<String, Reader> db) {
        if(db.containsKey(userToDelete) && !userToDelete.equals(this.username)) {
            db.remove(userToDelete);
        }
    }
}
