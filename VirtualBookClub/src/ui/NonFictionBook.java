
package ui;

import ui.Book;


class NonFictionBook extends Book{
    public NonFictionBook(String t, String a, String g, int p) { super(t, a, g, p); }
    @Override public String getBookTypeIcon() { return "[Non-Fiction]"; }
}
