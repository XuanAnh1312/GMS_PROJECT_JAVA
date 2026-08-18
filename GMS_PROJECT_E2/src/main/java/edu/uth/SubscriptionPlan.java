package edu.uth;

public class SubscriptionPlan {
    private String planId;
    private String planName;
    private double price;
    private int durationMonths;

    public SubscriptionPlan(String planId, String planName, double price, int durationMonths) {
        this.planId = planId;
        this.planName = planName;
        this.price = price;
        this.durationMonths = durationMonths;
    }

    public void updatePlan() {
        System.out.println("Subscription plan " + planId + " (" + planName + ") updated.");
    }

    public void updatePlan(String planName, double price, int durationMonths) {
        this.planName = planName;
        this.price = price;
        this.durationMonths = durationMonths;
        updatePlan();
    }

    public String getPlanId() {
        return planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    @Override
    public String toString() {
        return "planId: " + planId + "\n" +
               "planName: " + planName + "\n" +
               "price: $" + String.format("%.2f", price) + "\n" +
               "durationMonths: " + durationMonths;
    }
}
