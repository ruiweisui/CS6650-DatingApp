package model;

public class SwipeObject {
    private String swiper;
    private String swipee;
    private String comment;
    private boolean likes;

    public String getSwiper() {
        return this.swiper;
    }

    public String getSwipee() {
        return this.swipee;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isLikes() {
        return this.likes;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }

    public String toString() {
        return "SwipeObject{swiper='" + this.swiper + "', swipee='" + this.swipee + "', comment='" + this.comment + "', likes=" + this.likes + "}";
    }
}
