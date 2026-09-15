
package ui;

import ui.Book;

class FictionBook extends Book{
    public FictionBook(String t, String a, String g, int p) { super(t, a, g, p); }
    @Override public String getBookTypeIcon() { return "[Fiction]"; }
}
