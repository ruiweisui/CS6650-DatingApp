package model;

public class SwipeObject {
    private String swiper;
    private String swipee;
    private String comment;
    private boolean likes;

    public String getSwiper() {
        return swiper;
    }

    public String getSwipee() {
        return swipee;
    }

    public String getComment() {
        return comment;
    }

    public boolean isLikes() {
        return likes;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "SwipeObject{" +
                "swiper='" + swiper + '\'' +
                ", swipee='" + swipee + '\'' +
                ", comment='" + comment + '\'' +
                ", likes=" + likes +
                '}';
    }
}
