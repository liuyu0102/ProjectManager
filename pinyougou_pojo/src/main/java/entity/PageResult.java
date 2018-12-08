package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Liuyu
 * @Date: 2018/12/4 0004 17:38
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class PageResult  implements Serializable{
    private long total;
    private List rows;

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
