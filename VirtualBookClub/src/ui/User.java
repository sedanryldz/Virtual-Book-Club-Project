
package ui;

public abstract class User {
    protected String username;
    protected String email;
    protected String password;
    protected String role;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
