
package ui;


class AudioBook extends Book{
    public AudioBook(String t, String a, String g, int durationInMins) { super(t, a, g, durationInMins); }
    @Override public String getBookTypeIcon() { return "[Audiobook]"; }
}
