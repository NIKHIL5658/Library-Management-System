package com.library.dao;

import com.library.model.Member;
import java.util.List;

public interface MemberDAO {

    int addMember(Member member);

    boolean updateMember(Member member);

    boolean deleteMember(int memberId);

    Member getMemberById(int memberId);

    List<Member> getAllMembers();
}
