
package ui;

import ui.Book;
import ui.Challenge;



class GenreChallenge extends Challenge {

    private String targetGenre;
    private int targetCount;
    private int currentCount;

    public GenreChallenge(String title, String description, String genre, int count) {
        super(title, description);
        this.targetGenre = genre;
        this.targetCount = count;
    }

    @Override
    public void checkProgress(Reader reader) {
        if (isCompleted) {
            return;
        }
        currentCount = 0;
        for (Book b : reader.getCompletedBooks()) {
            if (b.getGenre().equalsIgnoreCase(targetGenre)) {
                currentCount++;
            }
        }
        if (currentCount >= targetCount) {
            this.isCompleted = true;
            reader.unlockBadge(title);
            reader.gainExp(800);
        }
    }

    @Override
    public String getProgressText() {
        return currentCount + " / " + targetCount + " Books (" + targetGenre + ")";
    }
}
