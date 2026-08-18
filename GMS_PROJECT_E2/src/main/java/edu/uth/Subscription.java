package edu.uth;

import java.time.LocalDate;

public class Subscription {
    private String subscriptionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private SubscriptionPlan plan;
    private double amountPaid = 0.0;

    public Subscription(String subscriptionId, LocalDate startDate, LocalDate endDate, String status, SubscriptionPlan plan) {
        this.subscriptionId = subscriptionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.plan = plan;
    }

    public void renew() {
        this.startDate = LocalDate.now();
        if (plan != null) {
            this.endDate = this.startDate.plusMonths(plan.getDurationMonths());
        } else {
            this.endDate = this.startDate.plusMonths(1);
        }
        this.status = "active";
    }

    public void renewForMonths(int months, double amountPaid) {
        this.startDate = LocalDate.now();
        this.endDate = this.startDate.plusMonths(months);
        this.status = "active";
        this.amountPaid = amountPaid;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    @Override
    public String toString() {
        return "subscriptionId: " + subscriptionId + "\n" +
               "startDate: " + startDate + "\n" +
               "endDate: " + endDate + "\n" +
               "status: " + status + "\n" +
               "planName: " + (plan != null ? plan.getPlanName() : "N/A") + "\n" +
               "amountPaid: $" + String.format("%.2f", amountPaid);
    }
}
