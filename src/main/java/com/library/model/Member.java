package com.library.model;

public class Member extends Person {

    private String membershipId;
    private int maxBooksAllowed;
    private int borrowedBooksCount;

    public Member(int id, String name, String email, String phone,
                  String membershipId, int maxBooksAllowed) {
        super(id, name, email, phone);
        this.membershipId = membershipId;
        this.maxBooksAllowed = maxBooksAllowed;
        this.borrowedBooksCount = 0;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }

    public void incrementBorrowedCount() {
        this.borrowedBooksCount++;
    }

    public void decrementBorrowedCount() {
        if (this.borrowedBooksCount > 0) {
            this.borrowedBooksCount--;
        }
    }

    public boolean canBorrowMore() {
        return borrowedBooksCount < maxBooksAllowed;
    }

    @Override
    public String displayRole() {
        return "Member";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", MembershipID=%s, Borrowed=%d/%d",
                membershipId, borrowedBooksCount, maxBooksAllowed);
    }
}
