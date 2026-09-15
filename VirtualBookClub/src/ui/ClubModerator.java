
package ui;


class ClubModerator extends Reader{
    public ClubModerator(String username, String email, String password) { 
        super(username, email, password); 
        this.role = "Club Moderator";
    }
}
