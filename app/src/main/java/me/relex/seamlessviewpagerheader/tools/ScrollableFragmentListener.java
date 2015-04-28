package me.relex.seamlessviewpagerheader.tools;

public interface ScrollableFragmentListener {

    public void onFragmentAttached(ScrollableListener fragment, int position);

    public void onFragmentDetached(ScrollableListener fragment, int position);
}
