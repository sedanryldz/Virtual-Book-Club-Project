
package ui;

import ui.Book;

class GraphicNovel extends Book{
    public GraphicNovel(String t, String a, String g, int p) { super(t, a, g, p); }
    @Override public String getBookTypeIcon() { return "[Comic/Graphic Novel]"; }
}
