package com.example.ismail.hiddenfounderstest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by ismail on 03.01.2018.
 *
 * A custom list view, extend from basic listview.
 * It charge automatically the next page of the list when it reach the bottom.
 * It has by default our costume Adapter as a default adapter
 */

public class CustomListView extends ListView {

    Adapter adapter;
    public CustomListView(final Context context) {
        super(context);
        adapter = new Adapter(context);
        new GitHubRepoGetter(context,this).Next(); // lunching the method "next" from the class GitHubRepoGetter to populate list view
        this.setOnScrollListener(new OnScrollListener() { // add an on scroll listener inorder to add the specification of getting new items when reaching
            @Override                                      // the bottom of the list
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int threshold = 1; // initializing the list view threshold
                int count = getCount(); // and the number of current items in the list

                if (scrollState == SCROLL_STATE_IDLE) { // waiting until the scrolling has finished to execute the verification
                    if (getLastVisiblePosition() >= count - threshold) {    // if the last visible item is the las item in the list view
                        (new GitHubRepoGetter(context,CustomListView.this)).Next(); // then we execute again the method "next" from the  asyncTask GitHubRepoGetter
                                                                                                // in order to charge more items (the next page if there is)
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        this.setAdapter(adapter);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Adapter getCustomAdapter(){
        return this.adapter;
    }

}
