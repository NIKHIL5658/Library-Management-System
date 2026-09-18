package com.library.dao;

import com.library.db.DatabaseConnectionManager;
import com.library.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public int addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone, membership_id, max_books_allowed, borrowed_books_count) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getMembershipId());
            ps.setInt(5, member.getMaxBooksAllowed());
            ps.setInt(6, member.getBorrowedBooksCount());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("addMember failed: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET name=?, email=?, phone=?, max_books_allowed=?, borrowed_books_count=? " +
                "WHERE member_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setInt(4, member.getMaxBooksAllowed());
            ps.setInt(5, member.getBorrowedBooksCount());
            ps.setInt(6, member.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateMember failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE member_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteMember failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("getMemberById failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllMembers failed: " + e.getMessage());
        }
        return members;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("membership_id"),
                rs.getInt("max_books_allowed")
        );
        int borrowed = rs.getInt("borrowed_books_count");
        for (int i = 0; i < borrowed; i++) {
            m.incrementBorrowedCount();
        }
        return m;
    }
}
