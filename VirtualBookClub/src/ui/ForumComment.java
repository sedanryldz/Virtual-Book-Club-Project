
package ui;


public class ForumComment {
    private String author; private String content; private boolean hasSpoiler;
    public ForumComment(String author, String content, boolean hasSpoiler) {
        this.author = author; this.content = content; this.hasSpoiler = hasSpoiler;
    }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public boolean hasSpoiler() { return hasSpoiler; }
}
