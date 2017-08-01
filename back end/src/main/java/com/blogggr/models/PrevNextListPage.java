package com.blogggr.models;

import com.blogggr.json.PageData;

import java.util.List;

/**
 * Created by Daniel Sunnen on 07.01.17.
 */
public class PrevNextListPage<T> {

    private List<T> pageItems;
    private PageData pageData;

    public PrevNextListPage(List<T> pageItems, PageData pageData){
        this.pageItems = pageItems;
        this.pageData = pageData;
    }

    public List<T> getPageItems() {
        return pageItems;
    }

    public PageData getPageData() {
        return pageData;
    }
}
