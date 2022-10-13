package model;

public class ReportItem {
    private String x;
    private double y1;
    private double y2;

    public ReportItem(String x, double y1, double y2) {
        this.x = x;
        this.y1 = y1;
        this.y2 = y2;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }
}
