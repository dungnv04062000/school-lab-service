package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.excel.ExcelUtil;
import com.schoollab.common.util.FileUtil;
import com.schoollab.dto.*;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.ClassGroupMapper;
import com.schoollab.mapper.ClassStudentMapper;
import com.schoollab.model.*;
import com.schoollab.repository.*;
import com.schoollab.service.ClassGroupService;
import com.schoollab.service.S3Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class ClassGroupImpl implements ClassGroupService {

    @Autowired
    ClassGroupRepository classGroupRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    ClassStudentRepository classStudentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassStudentMapper classStudentMapper;

    @Autowired
    ClassGroupMapper classGroupMapper;

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    EvaluationTeammateRepository evaluationTeammateRepository;

    @Autowired
    EvaluationGroupRepository evaluationGroupRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    GenericMapper genericMapper;

    @Override
    public List<ClassGroupDto> getAllGroups(String classId, String lessonId) {
        List<ClassGroup> classGroups = classGroupRepository.findAllByClassIdAndLessonId(classId, lessonId);
        List<ClassGroupDto> classGroupDtos = genericMapper.mapToListOfTypeNotNullProperty(classGroups, ClassGroupDto.class);

        for (ClassGroupDto classGroupDto : classGroupDtos) {
            List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(classGroupDto.getId());
            List<GroupMemberDto> groupMemberDtos = genericMapper.mapToListOfTypeNotNullProperty(groupMembers, GroupMemberDto.class);
            for (GroupMemberDto groupMemberDto : groupMemberDtos) {
                Optional<User> optUser = userRepository.findById(groupMemberDto.getMemberId());
                optUser.ifPresent(user -> groupMemberDto.setMemberName(user.getFirstName() + " " + user.getLastName()));
            }
            classGroupDto.setMembers(groupMemberDtos);
        }

        return classGroupDtos;
    }

    @Override
    public ClassGroupDto getOwnerGroups(String lessonId, String studentId) {
        ClassGroupDto classGroupDto = classGroupMapper.getOwnerGroup(lessonId, studentId);
        if(classGroupDto == null){
            throw new NotFoundException("Nhóm không tồn tại");
        }
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(classGroupDto.getId());
        classGroupDto.setMembers(genericMapper.mapToListOfTypeNotNullProperty(groupMembers, GroupMemberDto.class));
        for(GroupMemberDto member : classGroupDto.getMembers()) {
            Optional<User> optUser = userRepository.findById(member.getMemberId());
            optUser.ifPresent(user -> member.setMemberName(user.getFirstName() + " " + user.getLastName()));
        }
        return classGroupDto;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public List<ClassGroupDto> randomGroup(String classId, String lessonId, int numberOfGroup, Boolean isOverride) {
        List<ClassGroup> alreadyClassGroups = classGroupRepository.findAllByClassIdAndLessonId(classId, lessonId);
        if(alreadyClassGroups != null && !alreadyClassGroups.isEmpty()){
            if(!isOverride){
                throw new BadRequestException("Dự án này đã được chia lớp");
            } else {
                if(deleteGroup(classId, lessonId) == 0){
                    throw new RuntimeException("Có lỗi xảy ra");
                }
            }
        }
        List<ClassStudent> students = classStudentRepository.findAllByClassId(classId);
        if (students == null || students.isEmpty()) {
            throw new BadRequestException("Lớp này chưa có học sinh nào để xếp nhóm");
        }

        if (students.size() < numberOfGroup) {
            throw new BadRequestException("Số lượng học sinh không đủ để chia thành " + numberOfGroup + " nhóm");
        }
        try {
            //Xóa hết đánh giá cũ
            evaluationRepository.deleteAllByLessonId(lessonId);
            evaluationTeammateRepository.deleteAllByLessonId(lessonId);
            evaluationGroupRepository.deleteAllByLessonId(lessonId);
        } catch (Exception e){
            e.printStackTrace();
        }

        List<ClassGroupDto> classGroupDtos = new ArrayList<>();

        //tính số ng tối thiểu trong 1 nhóm
        int memberSize = students.size() / numberOfGroup;
        //xáo trộn danh sách
        Collections.shuffle(students);

        int index = 0;
        ClassGroup classGroup = null;
        ClassGroupDto classGroupDto = null;
        String groupId = null;
        int outOfGroupIndex = -1;
        for (int i = 0; i < students.size(); i++) {
            if (i > (memberSize * numberOfGroup - 1)) {
                //các nhóm đã đủ người
                outOfGroupIndex = i;
                break;
            } else {
                if (i % memberSize == 0) {
                    //tạo nhóm mới
                    classGroup = new ClassGroup();
                    classGroup.setClassId(classId);
                    classGroup.setLessonId(lessonId);
                    classGroup.setName("Nhóm " + ++index);
                    classGroup.setCreateAt(Instant.now());
                    ClassGroup savedClassGroup = classGroupRepository.save(classGroup);
                    groupId = savedClassGroup.getId();

                    classGroupDto = genericMapper.mapToTypeNotNullProperty(savedClassGroup, ClassGroupDto.class);
                    classGroupDto.setMembers(new ArrayList<>());
                }
                //tạo member mới
                GroupMember groupMember = new GroupMember()
                        .setGroupId(groupId)
                        .setMemberId(students.get(i).getStudentId());
                GroupMember savedGroupMember = groupMemberRepository.save(groupMember);

                GroupMemberDto groupMemberDto = genericMapper.mapToTypeNotNullProperty(savedGroupMember, GroupMemberDto.class);
                classGroupDto.getMembers().add(groupMemberDto);

                if (i % memberSize == (memberSize - 1)) {
                    classGroupDtos.add(classGroupDto);
                }
            }
        }

        if(outOfGroupIndex != -1 && outOfGroupIndex < students.size()){
            List<ClassGroup> classGroups = classGroupRepository.findAllByClassIdAndLessonId(classId, lessonId);
            int _index = 0;
            for (int i = outOfGroupIndex; i < students.size(); i++) {
                GroupMember groupMember = new GroupMember()
                        .setGroupId(classGroups.get(_index).getId())
                        .setMemberId(students.get(i).getStudentId());
                GroupMember savedGroupMember = groupMemberRepository.save(groupMember);
                GroupMemberDto groupMemberDto = genericMapper.mapToTypeNotNullProperty(savedGroupMember, GroupMemberDto.class);
                classGroupDtos.get(_index).getMembers().add(groupMemberDto);
                _index++;
            }
        }

        return classGroupDtos;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public List<ClassGroupDto> createGroupByOrder(String classId, String lessonId, int numberOfGroup, Boolean isOverride) {
        List<ClassGroup> alreadyClassGroups = classGroupRepository.findAllByClassIdAndLessonId(classId, lessonId);
        if(alreadyClassGroups != null && !alreadyClassGroups.isEmpty()){
            if(!isOverride){
                throw new BadRequestException("Lớp này đã được chia lớp");
            } else {
                if(deleteGroup(classId, lessonId) == 0){
                    throw new RuntimeException("Có lỗi xảy ra");
                }
            }
        }
        List<ClassStudent> students = classStudentRepository.findAllByClassId(classId);
        if (students == null || students.isEmpty()) {
            throw new BadRequestException("Lớp này chưa có học sinh nào để xếp nhóm");
        }
        if (students.size() < numberOfGroup) {
            throw new BadRequestException("Số lượng học sinh không đủ để chia thành " + numberOfGroup + " nhóm");
        }
        try {
            //Xóa hết đánh giá cũ
            evaluationRepository.deleteAllByLessonId(lessonId);
            evaluationTeammateRepository.deleteAllByLessonId(lessonId);
            evaluationGroupRepository.deleteAllByLessonId(lessonId);
        } catch (Exception e){
            e.printStackTrace();
        }

        List<ClassGroupDto> classGroupDtos = new ArrayList<>();

        //tính số ng tối thiểu trong 1 nhóm
        int memberSize = students.size() / numberOfGroup;

        int index = 0;
        ClassGroup classGroup = null;
        ClassGroupDto classGroupDto = null;
        String groupId = null;
        int outOfGroupIndex = -1;
        for (int i = 0; i < students.size(); i++) {
            if (i > (memberSize * numberOfGroup - 1)) {
                //các nhóm đã đủ người
                outOfGroupIndex = i;
                break;
            } else {
                if (i % memberSize == 0) {
                    //tạo nhóm mới
                    classGroup = new ClassGroup();
                    classGroup.setClassId(classId);
                    classGroup.setLessonId(lessonId);
                    classGroup.setName("Nhóm " + ++index);
                    classGroup.setCreateAt(Instant.now());
                    ClassGroup savedClassGroup = classGroupRepository.save(classGroup);
                    groupId = savedClassGroup.getId();

                    classGroupDto = genericMapper.mapToTypeNotNullProperty(savedClassGroup, ClassGroupDto.class);
                    classGroupDto.setMembers(new ArrayList<>());
                }
                //tạo member mới
                GroupMember groupMember = new GroupMember()
                        .setGroupId(groupId)
                        .setMemberId(students.get(i).getStudentId());
                GroupMember savedGroupMember = groupMemberRepository.save(groupMember);

                GroupMemberDto groupMemberDto = genericMapper.mapToTypeNotNullProperty(savedGroupMember, GroupMemberDto.class);
                classGroupDto.getMembers().add(groupMemberDto);

                if (i % memberSize == (memberSize - 1)) {
                    classGroupDtos.add(classGroupDto);
                }
            }
        }

        if(outOfGroupIndex != -1 && outOfGroupIndex < students.size()){
            List<ClassGroup> classGroups = classGroupRepository.findAllByClassIdAndLessonId(classId, lessonId);
            int _index = 0;
            for (int i = outOfGroupIndex; i < students.size(); i++) {
                GroupMember groupMember = new GroupMember()
                        .setGroupId(classGroups.get(_index).getId())
                        .setMemberId(students.get(i).getStudentId());
                GroupMember savedGroupMember = groupMemberRepository.save(groupMember);
                GroupMemberDto groupMemberDto = genericMapper.mapToTypeNotNullProperty(savedGroupMember, GroupMemberDto.class);
                classGroupDtos.get(_index).getMembers().add(groupMemberDto);
                _index++;
            }
        }

        return classGroupDtos;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public List<ClassGroupDto> createGroupByTemplate(String classId, String lessonId, MultipartFile multipartFile) {
        if(deleteGroup(classId, lessonId) == 0){
            throw new RuntimeException("Có lỗi xảy ra");
        }
        try {
            //Xóa hết đánh giá cũ
            evaluationRepository.deleteAllByLessonId(lessonId);
            evaluationTeammateRepository.deleteAllByLessonId(lessonId);
            evaluationGroupRepository.deleteAllByLessonId(lessonId);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(Objects.isNull(multipartFile)){
            throw new BadRequestException("Không tìm thấy file");
        }
        File file = null;
        XSSFWorkbook workbook = null;
        try {
            file = FileUtil.convertMultipartFileToFile(multipartFile);
            workbook = new XSSFWorkbook(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheetAt(0);

        List<GroupExcelRowDto> groupExcelRows = new ArrayList<>();
        // Get all rows
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (row.getRowNum() == 0) {
                // Ignore header
                continue;
            }

            int groupNumber = -1;
            try{
                groupNumber = (int)row.getCell(0).getNumericCellValue();
            } catch (Exception e){
                file.delete();
                throw new BadRequestException("Dữ liệu sai, vui lòng nhập lại");
            }
            String rollNumber = row.getCell(1).getStringCellValue();
            GroupExcelRowDto groupExcelRow = new GroupExcelRowDto()
                    .setGroup(groupNumber)
                    .setRollNumber(rollNumber);
            groupExcelRows.add(groupExcelRow);
        }

        groupExcelRows.sort((Comparator.comparingInt(GroupExcelRowDto::getGroup)));

        List<ClassGroupDto> classGroupDtos = new ArrayList<>();
        int currentGroup = -1;
        String groupId = null;
        ClassGroupDto classGroupDto = null;
        for (GroupExcelRowDto row : groupExcelRows){
            if(row.getGroup() != currentGroup){
                if(classGroupDto != null) {
                    classGroupDtos.add(classGroupDto);
                }
                currentGroup = row.getGroup();
                ClassGroup classGroup = new ClassGroup()
                        .setName("Nhóm " + currentGroup)
                        .setClassId(classId)
                        .setLessonId(lessonId)
                        .setCreateAt(Instant.now());
                ClassGroup savedClassGroup = classGroupRepository.save(classGroup);
                classGroupDto = genericMapper.mapToTypeNotNullProperty(savedClassGroup, ClassGroupDto.class);
                classGroupDto.setMembers(new ArrayList<>());
                groupId = savedClassGroup.getId();
            }
            Optional<User> optUser = userRepository.findById(row.getRollNumber());
            if(!optUser.isPresent()){
                continue;
            }
            GroupMember groupMember = new GroupMember()
                    .setMemberId(row.getRollNumber())
                    .setGroupId(groupId);
            GroupMember savedGroupMember = groupMemberRepository.save(groupMember);
            GroupMemberDto groupMemberDto = genericMapper.mapToTypeNotNullProperty(savedGroupMember, GroupMemberDto.class);
            classGroupDto.getMembers().add(groupMemberDto);
        }
        classGroupDtos.add(classGroupDto);

        try {
            file.delete();
        } catch (Exception e){
        }
        return classGroupDtos;
    }

    @Override
    public String createGroupTemplate(String classId) {
        List<StudentInClassDto> students = classStudentMapper.getAll(classId, null, null);

        XSSFWorkbook workbook = new XSSFWorkbook();
        CellStyle titleStyle = ExcelUtil.createStyleForTitle(workbook);
        Sheet sheet = workbook.createSheet("Group Template");
        int rowNumber = 0;
        //tạo row tiêu đề
        Row titleRow = sheet.createRow(rowNumber);
        Cell groupTitleCell = titleRow.createCell(0, CellType.STRING);
        groupTitleCell.setCellValue("Group");
        groupTitleCell.setCellStyle(titleStyle);
        Cell rollNumberTitleCell = titleRow.createCell(1, CellType.STRING);
        rollNumberTitleCell.setCellValue("Roll Number");
        rollNumberTitleCell.setCellStyle(titleStyle);
        Cell studentNameTitleCell = titleRow.createCell(2, CellType.STRING);
        studentNameTitleCell.setCellValue("Student Name");
        studentNameTitleCell.setCellStyle(titleStyle);

        for(StudentInClassDto student : students){
            rowNumber ++;
            Row studentRow = sheet.createRow(rowNumber);
            Cell rollNumberCell = studentRow.createCell(1);
            rollNumberCell.setCellValue(student.getRollNumber());
            Cell studentNameCell = studentRow.createCell(2);
            studentNameCell.setCellValue(student.getFullName());
        }
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        String fileName = "Group_" + classId + ".xlsx";
        File file = new File(fileName);
        FileOutputStream os = null;
        String s3Result;
        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            s3Result = s3Service.saveFile(file, "templates/groups", fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Đường dẫn không hợp lệ");
        } catch (IOException e) {
            throw new RuntimeException("Có lỗi khi xuất file");
        } finally {
            try {
                os.close();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s3Result;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public int deleteGroup(String classId, String lessonId) {
        List<ClassGroup> classGroups = classGroupRepository.findAllByClassIdAndLessonId(classId, lessonId);
        try{
            for(ClassGroup classGroup: classGroups){
                List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(classGroup.getId());
                groupMemberRepository.deleteAll(groupMembers);
            }
            classGroupRepository.deleteAll(classGroups);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
