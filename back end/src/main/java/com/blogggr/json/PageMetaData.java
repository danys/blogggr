package com.blogggr.json;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Daniel Sunnen on 12.01.17.
 */
public class PageMetaData {

    private Long totalCount; //total count of items
    private Integer pageItemsCount; //items per page
    private Integer pageId; //the page id
    private Integer nPages; //number of pages
    private String pageUrl; //this page url

    public PageMetaData(){
        //
    }

    public Integer getPageItemsCount() {
        return pageItemsCount;
    }

    public void setPageItemsCount(Integer pageItemsCount) {
        this.pageItemsCount = pageItemsCount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
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
