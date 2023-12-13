package com.schoollab.repository;

import com.schoollab.model.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, String> {
    ClassStudent findClassStudentByStudentId(String studentId);

    @Query(value="select * " +
            "from class_student cs " +
            "left join classes c on c.id = cs.class_id " +
            "where cs.student_id = :studentId " +
            "and c.semester_id = :semesterId", nativeQuery=true)
    ClassStudent findByStudentIdAndSemesterId(String studentId, String semesterId);

    List<ClassStudent> findAllByClassId(String classId);

    ClassStudent findByClassIdAndStudentId(String classId, String studentId);
}
