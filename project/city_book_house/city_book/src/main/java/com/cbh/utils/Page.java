package com.cbh.utils;

import java.util.List;

public class Page<T> {

    private int pageSize;//ÿҳ��ʾ�ļ�¼��

    private int totalCount;//�ܼ�¼��

    private int totalPage;//��ҳ��

    private List<T> list;//��װĳ��ҳ��
    
	private int currPage;//��ǰҳ��
	
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
