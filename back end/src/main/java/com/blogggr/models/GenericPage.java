package com.blogggr.models;

import com.blogggr.json.PageData;

import java.util.List;

/**
 * Created by Daniel Sunnen on 07.01.17.
 */
public class GenericPage<T> {

    List<T> pageItems;
    PageData pageData;

    public GenericPage(List<T> pageItems, PageData pageData){
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
