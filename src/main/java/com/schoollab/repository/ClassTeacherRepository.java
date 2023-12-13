package com.schoollab.repository;

import com.schoollab.model.ClassTeacher;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, String> {

    @Query("SELECT n FROM ClassTeacher n WHERE n.classId = :classId AND n.teacherId = :teacherId")
    ClassTeacher findTeacherByClass(@Param("classId") String classId, @Param("teacherId") String teacherId);
}
