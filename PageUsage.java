//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class PageUsage {
    private int start;
    private int end;

    public PageUsage() {
        this.start = -1;
        this.end = -1;
    }

    public PageUsage(int var1, int var2) {
        this.start = var1;
        this.end = var2;
    }

    public PageUsage(PageUsage var1) {
        this.start = var1.getStart();
        this.end = var1.getEnd();
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setStart(int var1) {
        this.start = var1;
    }

    public void setEnd(int var1) {
        this.end = var1;
    }
}
