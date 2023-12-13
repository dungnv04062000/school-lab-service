package com.schoollab.repository;

import com.schoollab.model.ClassGroup;
import com.schoollab.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {

    List<GroupMember> findAllByGroupId(String groupId);
}
