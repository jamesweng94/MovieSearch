package com.team47.fabflix;

import java.util.ArrayList;

public class Pagination {
    private final int itemsPerPage, lastPageItem, lastPage;
    private final ArrayList<Movie> movieList;

    public Pagination(int itemsPerPage, ArrayList<Movie> movieList){
        this.itemsPerPage = itemsPerPage;
        this.movieList = movieList;
        int totalItems = movieList.size();
        this.lastPage = totalItems / itemsPerPage;
        this.lastPageItem = totalItems % itemsPerPage;
    }

    public ArrayList<Movie> generateData(int currentPage) {
        int startItem = currentPage * itemsPerPage;
        ArrayList<Movie> newPageData = new ArrayList<>();
        if(currentPage == lastPage){
            //collecting data seperately for the last page
            for(int count = 0; count < lastPageItem; ++count){
                newPageData.add(movieList.get(startItem+count));
            }
        }else{
            // for all other pages
            for(int count = 0; count < itemsPerPage; ++count){
                newPageData.add(movieList.get(startItem + count));
            }
        }
        return newPageData;
    }

    int getLastPage(){
        return lastPage;
    }
}
