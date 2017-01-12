package com.blogggr.json;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Daniel Sunnen on 12.01.17.
 */
public class PageMetaData {

    private Long totalCount;
    private Integer pageCount;
    private Integer nPages;
    private String pageUrl;

    public PageMetaData(){
        //
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getnPages() {
        return nPages;
    }

    public void setnPages(Integer nPages) {
        this.nPages = nPages;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
}
