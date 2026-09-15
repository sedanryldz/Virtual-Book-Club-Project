
package ui;

import ui.Rateable;
import ui.Searchable;
import ui.Shareable;

abstract class Book implements Rateable, Shareable, Searchable {

    protected String title;
    protected String author;
    protected String genre;
    protected int totalPages;
    protected int pagesRead = 0;
    protected int userRating = 0;
    protected String personalNote = "";

    public Book(String title, String author, String genre, int totalPages) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalPages = totalPages;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    public int getRemainingPages() {
        return totalPages - pagesRead;
    }

    public void addPagesRead(int pages) {
        this.pagesRead += pages;
        if (this.pagesRead > this.totalPages) {
            this.pagesRead = this.totalPages;
        }
    }

    public void setPersonalNote(String note) {
        this.personalNote = note;
    }

    public String getPersonalNote() {
        return personalNote;
    }

    @Override
    public void rate(int score) {
        this.userRating = java.lang.Math.max(1, java.lang.Math.min(5, score));
    }

    @Override
    public double getAverageRating() {
        return userRating == 0 ? 4.5 : userRating;
    }

    public int getUserRating() {
        return userRating;
    }

    @Override
    public String generateShareText() {
        return "Currently reading '" + title + "'. Genre: " + genre;
    }

    @Override
    public boolean matches(String keyword) {
        return title.toLowerCase().contains(keyword.toLowerCase()) || author.toLowerCase().contains(keyword.toLowerCase());
    }

    @Override
    public String toString() {
        if (pagesRead == totalPages) {
            return getBookTypeIcon() + " " + title + " - Rating: " + userRating + "/5";
        }
        return getBookTypeIcon() + " " + title + " (" + genre + ") - Remaining: " + getRemainingPages() + " pgs";
    }

    public abstract String getBookTypeIcon();
}
