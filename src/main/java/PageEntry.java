public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    protected PageEntry(PageEntry pageEntry, int count) {
        this.pdfName = pageEntry.getPdfName();
        this.page = pageEntry.getPage();
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "PageEntry{" +
                "\n\tpdfName='" + pdfName +
                "\n\t page=" + page +
                "\n\t count=" + count +
                "\n\t" + '}';
    }

    @Override
    public int compareTo(PageEntry o) {

        return Integer.compare(o.count, this.count);
    }

    public boolean compareNamePage(PageEntry o) {
        if (this.pdfName.equals(o.pdfName) && this.page == o.page) {
            return true;
        } else {
            return false;
        }

    }
}
