package com.blogggr.json;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Daniel Sunnen on 06.01.17.
 */
public class PageData {

    private Long totalCount;
    private Integer pageCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String next;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String previous;

    public PageData(){
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

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
