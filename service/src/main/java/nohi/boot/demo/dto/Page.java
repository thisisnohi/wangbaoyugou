package nohi.boot.demo.dto;

import lombok.Data;

@Data
public class Page {

    public int pageIndex;

    public int pageSize;

    public int totalPage;

    public int totalRecords;

}
