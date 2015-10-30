package CS172_Info_Ret.webCrawler;

public class Pair {
    private String url = "";
    private Long depth = 0L;

    public Pair(String x, Long y)
    {
        this.url = x;
        this.depth = y;
    }

    public Pair()
    {

    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String x) {
        this.url = x;
    }

    public Long getDepth() {
        return this.depth;
    }

    public void setDepth(Long y) {
        this.depth = y;
    }


}
