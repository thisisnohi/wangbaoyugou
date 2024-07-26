package nohi.boot.common.dto.page;

import lombok.Data;

@Data
public class Page {

    public int pageIndex;

    public int pageSize;

    public int totalPage;

    public int totalRecords;

}
