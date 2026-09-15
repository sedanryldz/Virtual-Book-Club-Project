
package ui;

import ui.Reader;
import ui.Challenge;

class PageCountChallenge extends Challenge {

    private int targetPages;
    private int currentPages;

    public PageCountChallenge(String title, String description, int targetPages) {
        super(title, description);
        this.targetPages = targetPages;
    }

    @Override
    public void checkProgress(Reader reader) {
        if (isCompleted) {
            return;
        }
        this.currentPages = reader.getTotalPagesRead();
        if (this.currentPages >= targetPages) {
            this.isCompleted = true;
            reader.unlockBadge(title);
            reader.gainExp(500);
        }
    }

    @Override
    public String getProgressText() {
        return (currentPages > targetPages ? targetPages : currentPages) + " / " + targetPages + " Pages";
    }
}
