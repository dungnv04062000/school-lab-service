package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.constants.PageConstant;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.common.util.FileUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.ClassManyStudentRequest;
import com.schoollab.controller.request.ClassStudentAddRequestBody;
import com.schoollab.dto.ClassStudentDto;
import com.schoollab.dto.GroupExcelRowDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.UserDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassStudentMapper;
import com.schoollab.model.Class;
import com.schoollab.model.ClassStudent;
import com.schoollab.model.User;
import com.schoollab.repository.ClassRepository;
import com.schoollab.repository.ClassStudentRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.ClassStudentService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassStudentImpl implements ClassStudentService {
    @Autowired
    ClassStudentRepository classStudentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    ClassStudentMapper classStudentMapper;

    @Autowired
    GenericMapper genericMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public ClassStudentDto saveStudentToClass(ClassStudentAddRequestBody req) {
        Optional<Class> optClass = classRepository.findById(req.getClassId());
        if (!optClass.isPresent()) throw new NotFoundException("Không tìm thấy lớp học này");

        Optional<User> student = userRepository.findById(req.getStudentId());
        if (!optClass.isPresent()) throw new NotFoundException("Không tìm thấy học sinh này");

        ClassStudent classStudent = classStudentRepository
                .findByStudentIdAndSemesterId(req.getStudentId(), optClass.get().getSemesterId());
        if (classStudent != null) {
            if (classStudent.getClassId().equals(req.getClassId())) {
                throw new BadRequestException("Học sinh đã có trong lớp này");
            }
            throw new BadRequestException("Học sinh đang ở trong lớp học khác");
        }
        ClassStudent newClassStd = new ClassStudent()
                .setClassId(req.getClassId())
                .setStudentId(req.getStudentId())
                .setCreateAt(Instant.now());
        ClassStudent saveClass = classStudentRepository.save(newClassStd);
        return genericMapper.mapToTypeNotNullProperty(saveClass, ClassStudentDto.class);
    }


    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public List<ClassStudentDto> saveManyStudentToClass(ClassManyStudentRequest requestBody) {
        Optional<Class> optClass = classRepository.findById(requestBody.getClassId());
        if (!optClass.isPresent()) throw new NotFoundException("Không tìm thấy lớp học này");

        if(Objects.isNull(requestBody.getFile())){
            throw new BadRequestException("Không tìm thấy file");
        }
        File file = null;
        XSSFWorkbook workbook = null;
        try {
            file = FileUtil.convertMultipartFileToFile(requestBody.getFile());
            workbook = new XSSFWorkbook(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Xảy ra lỗi trong quá trình đọc file");
        }
        Sheet sheet = workbook.getSheetAt(0);
        if(sheet == null){
            file.delete();
            throw new BadRequestException("Dữ liệu sai, vui lòng nhập lại");
        }
        List<String> studentIds = new ArrayList<>();
        // Get all rows
        for (Row row : sheet) {
            if (row.getRowNum() < 2) {
                // Ignore header
                continue;
            }
            String studendId = row.getCell(0).getStringCellValue();
            studentIds.add(studendId);
        }

        List<ClassStudent> students = classStudentRepository.findAllByClassId(requestBody.getClassId());
        List<ClassStudentDto> result = new ArrayList<>();
        if(requestBody.getIsOverride() != null && requestBody.getIsOverride().equals("true")){
            //ghi đè lên lớp cũ
            //xóa hết học sinh trong lớp
            if(!students.isEmpty()){
                classStudentRepository.deleteAll(students);
            }

            //thêm vào lớp
            for (String studentId : studentIds) {
                ClassStudent _classStudent = classStudentRepository
                        .findByStudentIdAndSemesterId(studentId, optClass.get().getSemesterId());
                if (_classStudent != null) {
                    continue;
                }
                Optional<User> optStudent = userRepository.findById(studentId);
                if(optStudent.isPresent()){
                    ClassStudent classStudent = new ClassStudent()
                            .setClassId(requestBody.getClassId())
                            .setStudentId(studentId)
                            .setCreateAt(Instant.now());
                    ClassStudent savedStudent = classStudentRepository.save(classStudent);
                    result.add(genericMapper.mapToTypeNotNullProperty(savedStudent, ClassStudentDto.class));
                }
            }
        } else {
            for (String studentId : studentIds) {
                ClassStudent _classStudent = classStudentRepository
                        .findByStudentIdAndSemesterId(studentId, optClass.get().getSemesterId());
                if (_classStudent != null) {
                    continue;
                }

                Optional<User> optStudent = userRepository.findById(studentId);
                if(optStudent.isPresent()){
                    ClassStudent classStudent = new ClassStudent()
                            .setClassId(requestBody.getClassId())
                            .setStudentId(studentId)
                            .setCreateAt(Instant.now());
                    ClassStudent savedStudent = classStudentRepository.save(classStudent);
                    result.add(genericMapper.mapToTypeNotNullProperty(savedStudent, ClassStudentDto.class));
                }
            }
        }
        try {
            file.delete();
        } catch (Exception ignored){
        }
        return result;
    }

    @Override
    public void removeStudent(String classStudentId) {
        try{
            classStudentRepository.deleteById(classStudentId);
        } catch (Exception e){
            throw new RuntimeException("Có lỗi xảy ra khi xóa học sinh khỏi lớp");
        }
    }

    //hàm này bỏ chưa dùng
    @Override
    public List<StudentInClassDto> searchAllStudent(String campusId, String student, String gender, Integer page, Integer rowNumber) {
        return classStudentMapper
                .getAllByCampusForRootAdmin(campusId, Constants.ROLE_STUDENT_ID, student, gender, page, PageConstant.STUDENT_ROW_NUMBER);
    }

    @Override
    public int countAllStudent(String campusId, String student, String gender) {
        return classStudentMapper
                .countAllByCampusForRootAdmin(campusId, Constants.ROLE_STUDENT_ID, student, gender);
    }

    @Override
    public List<StudentInClassDto> filterStudent(String classId, String student, String gender) {
        List<StudentInClassDto> listStudent = classStudentMapper.getAll(classId, student, gender);
        return genericMapper.mapToListOfTypeNotNullProperty(listStudent, StudentInClassDto.class);
    }

    @Override
    public List<StudentInClassDto> filterStudentInCampus(String campusId, String semesterId, Integer gradeId, Integer roleId, String className, String student, String gender, Integer page, Integer rowNumber) {
        return classStudentMapper.getAllInCampus(campusId, semesterId, gradeId, roleId, className, student, gender, page, rowNumber);
    }

    @Override
    public int countAllStudentInCampus(String campusId, String semesterId, Integer gradeId, Integer roleId, String className, String student, String gender) {
        return classStudentMapper.countAllInCampus(campusId, semesterId, gradeId, roleId, className, student, gender);
    }
}
