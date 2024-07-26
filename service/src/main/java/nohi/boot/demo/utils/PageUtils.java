package nohi.boot.demo.utils;

import com.github.pagehelper.PageInfo;
import nohi.boot.common.dto.page.Page;

import java.util.List;

public class PageUtils {

    public static Page getPages(List<?> list, Page page){
        PageInfo<?> pageInfo = new PageInfo<>(list);
        Page page1 = new Page();
        page1.setTotalPage(pageInfo.getPages());
        page1.setPageSize(page.getPageSize());
        page1.setPageIndex(page.getPageIndex());
        page1.setTotalRecords((int) pageInfo.getTotal());
        return page1;
    }

    public static List<?> getData(List<?> list){
        PageInfo<?> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
    }

}
