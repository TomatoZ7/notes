package com.cbh.utils;

import java.util.List;

public class Page<T> {

    private int pageSize;//每页显示的记录数

    private int totalCount;//总记录数

    private int totalPage;//总页数

    private List<T> list;//封装某个页面
    
	private int currPage;//当前页数
	
    public Page() {
        super();
    }
    
    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list= list;
    }
}
