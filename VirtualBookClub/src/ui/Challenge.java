
package ui;


abstract class Challenge {

    protected String title;
    protected String description;
    protected boolean isCompleted;

    public Challenge(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public abstract void checkProgress(Reader reader);

    public abstract String getProgressText();
}
