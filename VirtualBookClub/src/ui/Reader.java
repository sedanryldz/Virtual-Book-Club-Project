
package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import database.DatabaseManager;


class Reader extends User {

    protected List<Book> currentlyReading = new ArrayList<>();
    protected List<Book> wantToRead = new ArrayList<>();
    protected List<Book> completedBooks = new ArrayList<>();

    protected int totalPagesRead = 0;
    protected int readingDaysSimulated = 1;
    protected Map<String, Integer> pageHistory = new LinkedHashMap<>();
    protected List<String> following = new ArrayList<>();
    protected int level = 1;
    protected int exp = 0;
    protected int expToNextLevel = 100;
    protected List<String> badges = new ArrayList<>();
    protected List<Challenge> activeChallenges = new ArrayList<>();

    protected List<BookClub> myClubs = new ArrayList<>();
    protected ImageIcon profileImage = null;

    public Reader(String username, String email, String password) {
        super(username, email, password);
        this.role = "Standard Reader";

        activeChallenges.add(new PageCountChallenge("Novice Reader", "Read 100 pages to complete your first challenge.", 100));
        activeChallenges.add(new PageCountChallenge("Bookworm", "Reach 500 pages read.", 500));
        activeChallenges.add(new GenreChallenge("Sci-Fi Explorer", "Finish 3 Science Fiction books.", "Sci-Fi", 3));
        activeChallenges.add(new SeriesChallenge(
                "Dune Master",
                "Complete the classic Dune trilogy.",
                java.util.Arrays.asList("Dune", "Dune Messiah", "Children of Dune")
        ));
        activeChallenges.add(new TimeSprintChallenge(
                "Weekend Sprint",
                "Read 300 pages before the deadline.",
                300,
                java.time.LocalDate.now().plusDays(3) // Bugünden itibaren 3 gün süre verir
        ));
    }

    public void startReading(Book book) {
        currentlyReading.add(book);
        wantToRead.remove(book);
    }

    public void addToWantToRead(Book book) {
        wantToRead.add(book);
    }

    public void logPages(Book book, int pages) {
        book.addPagesRead(pages);
        this.totalPagesRead += pages;
        pageHistory.put(book.getTitle(), pageHistory.getOrDefault(book.getTitle(), 0) + pages);

        if (totalPagesRead % 50 == 0) {
            readingDaysSimulated++;
        }
        gainExp(pages * 2);
        for (Challenge c : activeChallenges) {
            c.checkProgress(this);
        }
    }

    public void gainExp(int amount) {
        this.exp += amount;
        while (this.exp >= expToNextLevel) {
            this.exp -= expToNextLevel;
            this.level++;
            this.expToNextLevel = (int) (this.expToNextLevel * 1.5);
            if (this.level >= 5) {
                unlockBadge("Level 5 Scholar");
            }
        }
    }

    public void unlockBadge(String badge) {
        if (!badges.contains(badge)) {
            badges.add(badge);
            DatabaseManager.unlockBadge(this.username, badge);
        }
    }

    public int getDailyPace() {
        return totalPagesRead == 0 ? 0 : java.lang.Math.max(1, totalPagesRead / readingDaysSimulated);
    }

    public Map<String, Integer> getGenreDistribution(List<Book> bookList) {
        Map<String, Integer> distribution = new HashMap<>();
        for (Book b : bookList) {
            distribution.put(b.getGenre(), distribution.getOrDefault(b.getGenre(), 0) + 1);
        }
        return distribution;
    }

    public Map<String, Integer> getGenreDistribution() {
        return getGenreDistribution(completedBooks);
    }

    public ImageIcon getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageIcon img) {
        this.profileImage = img;
    }

    public int getTotalPagesRead() {
        return totalPagesRead;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }

    public List<Book> getCurrentlyReading() {
        return currentlyReading;
    }

    public List<Book> getWantToRead() {
        return wantToRead;
    }

    public List<Book> getCompletedBooks() {
        return completedBooks;
    }

    public List<String> getBadges() {
        return badges;
    }

    public List<Challenge> getActiveChallenges() {
        return activeChallenges;
    }

    public Map<String, Integer> getPageHistory() {
        return pageHistory;
    }

    public List<BookClub> getMyClubs() {
        return myClubs;
    }

    public void finishBook(Book b) {
        currentlyReading.remove(b);
        completedBooks.add(b);
        unlockBadge("First Book Finished");
        for (Challenge c : activeChallenges) {
            c.checkProgress(this);
        }
    }

    public List<String> getFollowing() {
        return following;
    }
}
