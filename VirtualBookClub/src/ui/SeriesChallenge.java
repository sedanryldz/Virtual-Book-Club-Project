
package ui;

import java.util.List;


class SeriesChallenge extends Challenge {

    private java.util.List<String> seriesBooks;
    private int currentCount;

    public SeriesChallenge(String title, String description, List<String> seriesBooks) {
        super(title, description);
        this.seriesBooks = seriesBooks;
        this.currentCount = 0;
    }

    @Override
    public void checkProgress(Reader reader) {
        if (isCompleted) {
            return;
        }
        currentCount = 0;

        
        for (Book b : reader.getCompletedBooks()) {
            if (seriesBooks.contains(b.getTitle())) {
                currentCount++;
            }
        }

        if (currentCount >= seriesBooks.size()) {
            this.isCompleted = true;
            reader.unlockBadge(title);
            reader.gainExp(1000); 
        }
    }

    @Override
    public String getProgressText() {
        return currentCount + " / " + seriesBooks.size() + " Books";
    }
}
