package com.example.cashin.web.rest.vm;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageVM<T> {
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;

    private List<T> content;

    public PageVM(Page<T> source) {
        this.totalPages = source.getTotalPages();
        this.totalElements = source.getTotalElements();
        this.size = source.getSize();
        this.number = source.getNumber();
        this.content = source.getContent();
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
