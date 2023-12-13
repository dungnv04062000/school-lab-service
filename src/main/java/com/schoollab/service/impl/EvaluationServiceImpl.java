package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.enums.LessonStatusEnum;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.excel.ExcelUtil;
import com.schoollab.common.util.FileUtil;
import com.schoollab.dto.*;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.EvaluationMapper;
import com.schoollab.model.*;
import com.schoollab.model.Class;
import com.schoollab.repository.*;
import com.schoollab.service.EvaluationService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    EvaluationMapper evaluationMapper;

    @Autowired
    EvaluationCriteriaRepository evaluationCriteriaRepository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    ClassStudentRepository classStudentRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    GenericMapper genericMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public EvaluationDto evaluateStudent(String fromId, String toId, String lessonId, Float grade) {
        Evaluation alreadyEvaluation = evaluationRepository.findByFromIdAndToIdAndLessonId(fromId, toId, lessonId);
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.FINISHED.name())){
            throw new BadRequestException("Dự án này đã kết thúc");
        }
        if(grade < Constants.MIN_MARK || grade > Constants.MAX_MARK){
            throw new BadRequestException("Điểm không hợp lệ");
        }

        if(alreadyEvaluation == null){
            //chưa có đánh giá -> tạo mới
            Evaluation evaluation = new Evaluation()
                    .setFromId(fromId)
                    .setToId(toId)
                    .setLessonId(lessonId)
                    .setGrade(grade)
                    .setCreateAt(Instant.now());
            Evaluation savedEvaluation = evaluationRepository.save(evaluation);
            return genericMapper.mapToTypeNotNullProperty(savedEvaluation, EvaluationDto.class);
        } else {
            //đã có đánh giá -> update
            alreadyEvaluation
                    .setGrade(grade)
                    .setUpdateAt(Instant.now());
            Evaluation savedEvaluation = evaluationRepository.save(alreadyEvaluation);
            return genericMapper.mapToTypeNotNullProperty(savedEvaluation, EvaluationDto.class);
        }
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public List<EvaluationDto> evaluateStudentByTemplate(String fromId, String lessonId, MultipartFile multipartFile) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.FINISHED.name())){
            throw new BadRequestException("Dự án này đã kết thúc");
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
        Sheet sheet = null;
        try{
            sheet = workbook.getSheetAt(0);
        } catch (Exception e){
            file.delete();
            throw new BadRequestException("Dữ liệu sai, vui lòng nhập lại");
        }

        List<GradeExcelRowDto> gradeExcelRows = new ArrayList<>();
        // Get all rows
        for (Row row : sheet) {
            if (row.getRowNum() < 4) {
                // Ignore header
                continue;
            }
            String rollNumber = row.getCell(0).getStringCellValue();

            float grade = 0f;
            try {
                grade = (float) row.getCell(12).getNumericCellValue();
            } catch (Exception e) {
                file.delete();
                throw new BadRequestException("Dữ liệu sai (Điểm), vui lòng nhập lại");
            }
            GradeExcelRowDto gradeExcelRow = new GradeExcelRowDto()
                    .setGrade(grade)
                    .setRollNumber(rollNumber);
            gradeExcelRows.add(gradeExcelRow);
        }

        List<EvaluationDto> results = new ArrayList<>();
        for (GradeExcelRowDto row : gradeExcelRows){
            EvaluationDto evaluationDto = evaluateStudent(fromId, row.getRollNumber(), lessonId, row.getGrade());
            if(evaluationDto != null){
                results.add(evaluationDto);
            }
        }
        try {
            file.delete();
        } catch (Exception e){
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public List<EvaluationDto> evaluateByGroup(String fromId, String groupId, String lessonId, Float grade) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.FINISHED.name())){
            throw new BadRequestException("Dự án này đã kết thúc");
        }
        if(grade < Constants.MIN_MARK || grade > Constants.MAX_MARK){
            throw new BadRequestException("Điểm không hợp lệ");
        }

        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(groupId);
        List<EvaluationDto> results = new ArrayList<>();
        for (GroupMember member : groupMembers){
            EvaluationDto evaluationDto = evaluateStudent(fromId, member.getMemberId(), lessonId, grade);
            if(evaluationDto != null){
                results.add(evaluationDto);
            }
        }
        return results;
    }

    @Override
    public List<LessonGradeDto> getEvaluationGradeList(String lessonId) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }

//        Lesson lesson = optLesson.get();
//        Optional<Class> optClass = classRepository.findById(lesson.getClassId());
//        Class _class = optClass.get();
//
//        float preparationPercentage = 0.25f;
//        float implementationPercentage = 0.25f;
//        float presentationPercentage = 0.25f;
//        float productionPercentage = 0.25f;
//        Optional<EvaluationCriteria> optEvaluationCriteria = evaluationCriteriaRepository
//                .findById(lesson.getEvaluationCriteriaId());
//        if(optEvaluationCriteria.isPresent()){
//            EvaluationCriteria evaluationCriteria = optEvaluationCriteria.get();
//            preparationPercentage = evaluationCriteria.getPreparation() / 100;
//            implementationPercentage = evaluationCriteria.getImplementation() / 100;
//            presentationPercentage = evaluationCriteria.getPresentation() / 100;
//            productionPercentage = evaluationCriteria.getProduction() / 100;
//        }

        return evaluationMapper.getAllStudentGrade(lessonId);
    }

    @Override
    public LessonGradeDto getOneGrade(String studentId, String lessonId) {
        Optional<User> optionalUser = userRepository.findById(studentId);
        if(!optionalUser.isPresent()){
            throw new NotFoundException("Không tìm thấy người dùng này");
        }
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        List<LessonGradeDto> grades = evaluationMapper.getAllStudentGrade(lessonId);
        LessonGradeDto ownerGrade = null;
        for (LessonGradeDto grade : grades){
            if(grade.getStudentId().equals(studentId)){
                ownerGrade = grade;
                break;
            }
        }
        return ownerGrade;
    }

    @Override
    public List<OwnerGradeStatisticDto> getOwnerGradeStatistic(String studentId, String semesterId, String subjectId) {
        Optional<Semester> optionalSemester = semesterRepository.findById(semesterId);
        if(!optionalSemester.isPresent()){
            throw new BadRequestException("Không tìm thấy dữ liệu về học kỳ này");
        }
        Date startAt = Date.from(optionalSemester.get().getStartAt());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(startAt);

        List<OwnerGradeStatisticDto> data = new ArrayList<>();
        List<OwnerGradeStatisticDto> rawData = evaluationMapper.getOwnerGradeStatistic(studentId, semesterId, subjectId);

        data.add(new OwnerGradeStatisticDto().setX(formattedDate).setY("0"));
        String preY = "0";
        for (OwnerGradeStatisticDto item : rawData) {
            try {
                OwnerGradeStatisticDto previousGrade = new OwnerGradeStatisticDto()
                        .setX(getPreviousDateString(item.getX())).setY(preY);
                data.add(previousGrade);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            float value = (float)Math.round(Float.parseFloat(item.getY()) * 10) / 10;
            item.setY(value + "");
            data.add(item);
            preY = item.getY();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        data.add(new OwnerGradeStatisticDto().setX(sdf.format(new Date())).setY(preY));
        return data;
    }

    String getPreviousDateString(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
        return sdf.format(new Date(date.getTime() - MILLIS_IN_A_DAY));
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public String downloadEvaluationTemplate(String lessonId, Boolean isResult) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }

        Lesson lesson = optLesson.get();
        Optional<Class> optClass = classRepository.findById(lesson.getClassId());
        if(!optClass.isPresent()){
            throw new NotFoundException("Không tìm thấy lớp học này");
        }
        Class _class = optClass.get();

        List<LessonGradeDto> grades = evaluationMapper.getAllStudentGrade(lessonId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        CellStyle titleStyle = ExcelUtil.createStyleForTitle(workbook);
        Sheet sheet = workbook.createSheet("Lesson Grade");
        int rowNumber = 0;
        //tạo row lesson
        Row lessonRow = sheet.createRow(rowNumber);
        Cell lessonLabelCell = lessonRow.createCell(0, CellType.STRING);
        lessonLabelCell.setCellValue("Dự án:");
        lessonLabelCell.setCellStyle(titleStyle);
        Cell lessonTitleCell = lessonRow.createCell(1, CellType.STRING);
        lessonTitleCell.setCellValue(lesson.getTitle());
//        rollNumberTitleCell.setCellStyle(titleStyle);
        rowNumber++;
        //row class
        Row classRow = sheet.createRow(rowNumber);
        Cell classLabelCell = classRow.createCell(0, CellType.STRING);
        classLabelCell.setCellValue("Lớp:");
        classLabelCell.setCellStyle(titleStyle);
        Cell classNameCell = classRow.createCell(1, CellType.STRING);
        classNameCell.setCellValue(_class.getName());
        rowNumber++;
        //row note
        Row noteRow = sheet.createRow(rowNumber);
        Cell noteLabelCell = noteRow.createCell(0, CellType.STRING);
        noteLabelCell.setCellValue("Lưu ý:");
        noteLabelCell.setCellStyle(titleStyle);
        Cell noteContentCell = noteRow.createCell(1, CellType.STRING);
        noteContentCell.setCellValue("Giáo viên khi chấm điểm, vui lòng chỉ điền điểm của học sinh vào cột điểm và không chỉnh sửa bất kỳ cột nào khác!");
        rowNumber++;
        //row title
        Row titleRow = sheet.createRow(rowNumber);
        Cell studentIdCell = titleRow.createCell(0, CellType.STRING);
        studentIdCell.setCellValue("Mã số");
        studentIdCell.setCellStyle(titleStyle);
        Cell studentNameCell = titleRow.createCell(1, CellType.STRING);
        studentNameCell.setCellValue("Họ tên");
        studentNameCell.setCellStyle(titleStyle);
        Cell experimentCountCell = titleRow.createCell(2, CellType.STRING);
        experimentCountCell.setCellValue("Số thí nghiệm");
        experimentCountCell.setCellStyle(titleStyle);
        Cell groupNameCell = titleRow.createCell(3, CellType.STRING);
        groupNameCell.setCellValue("Nhóm");
        groupNameCell.setCellStyle(titleStyle);
        Cell hardWorkingCell = titleRow.createCell(4, CellType.STRING);
        hardWorkingCell.setCellValue("Chăm chỉ");
        hardWorkingCell.setCellStyle(titleStyle);
        Cell teamworkCell = titleRow.createCell(5, CellType.STRING);
        teamworkCell.setCellValue("Làm việc nhóm");
        teamworkCell.setCellStyle(titleStyle);
        Cell skillCell = titleRow.createCell(6, CellType.STRING);
        skillCell.setCellValue("Kỹ năng");
        skillCell.setCellStyle(titleStyle);
        Cell preparationCell = titleRow.createCell(7, CellType.STRING);
        preparationCell.setCellValue("Lên kế hoạch");
        preparationCell.setCellStyle(titleStyle);
        Cell implementationCell = titleRow.createCell(8, CellType.STRING);
        implementationCell.setCellValue("Thực hiện");
        implementationCell.setCellStyle(titleStyle);
        Cell presentationCell = titleRow.createCell(9, CellType.STRING);
        presentationCell.setCellValue("Thuyết trình");
        presentationCell.setCellStyle(titleStyle);
        Cell productionCell = titleRow.createCell(10, CellType.STRING);
        productionCell.setCellValue("Sản phẩm");
        productionCell.setCellStyle(titleStyle);
        Cell totalGradeCell = titleRow.createCell(11, CellType.STRING);
        totalGradeCell.setCellValue("Tổng điểm nhóm");
        totalGradeCell.setCellStyle(titleStyle);
        Cell finalGradeCell = titleRow.createCell(12, CellType.STRING);
        finalGradeCell.setCellValue("Điểm");
        finalGradeCell.setCellStyle(titleStyle);


        for(LessonGradeDto grade : grades){
            rowNumber ++;
            Row studentRow = sheet.createRow(rowNumber);
            Cell _studentIdCell = studentRow.createCell(0, CellType.STRING);
            _studentIdCell.setCellValue(grade.getStudentId());
            Cell _studentNumberCell = studentRow.createCell(1, CellType.STRING);
            _studentNumberCell.setCellValue(grade.getStudentName());
            Cell _experimentCountCell = studentRow.createCell(2, CellType.NUMERIC);
            _experimentCountCell.setCellValue(grade.getExperimentCount());
            Cell _groupNameCell = studentRow.createCell(3, CellType.STRING);
            _groupNameCell.setCellValue(grade.getGroupName());
            Cell _hardWorkingCell = studentRow.createCell(4, CellType.NUMERIC);
            _hardWorkingCell.setCellValue((double) Math.round(grade.getHardWorking() * 100) / 100);
            Cell _teamworkCell = studentRow.createCell(5, CellType.NUMERIC);
            _teamworkCell.setCellValue((double) Math.round(grade.getTeamwork() * 100) / 100);
            Cell _skillCell = studentRow.createCell(6, CellType.NUMERIC);
            _skillCell.setCellValue((double) Math.round(grade.getSkill() * 100) / 100);
            Cell _preparationCell = studentRow.createCell(7, CellType.NUMERIC);
            _preparationCell.setCellValue((double) Math.round(grade.getPreparation() * 100) / 100);
            Cell _implementationCell = studentRow.createCell(8, CellType.NUMERIC);
            _implementationCell.setCellValue((double) Math.round(grade.getImplementation() * 100) / 100);
            Cell _presentationCell = studentRow.createCell(9, CellType.NUMERIC);
            _presentationCell.setCellValue((double) Math.round(grade.getPresentation() * 100) / 100);
            Cell _productionCell = studentRow.createCell(10, CellType.NUMERIC);
            _productionCell.setCellValue((double) Math.round(grade.getProduction() * 100) / 100);
            Cell _totalGradeCell = studentRow.createCell(11, CellType.NUMERIC);
            _totalGradeCell.setCellValue((double) Math.round(grade.getTotal() * 100) / 100);
            if(isResult){
                Cell _finalGradeCell = studentRow.createCell(12, CellType.NUMERIC);
                _finalGradeCell.setCellStyle(titleStyle);
                _finalGradeCell.setCellValue(grade.getGrade() != null ? (double) Math.round(grade.getGrade() * 100) / 100 : 0);
            }
        }
        sheet.autoSizeColumn(0);
//        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);

        String fileName = "Grade_" + lesson.getTitle().replaceAll("\\s+", "_")
                + "_" + _class.getName().replaceAll("\\s+", "_") + ".xlsx";
        File file = new File(fileName);
        FileOutputStream os = null;
        String s3Result;
        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            s3Result = s3Service.saveFile(file, "grades", fileName);
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
    public String downloadClassGrade(String classId, String subjectId) {

        List<SemesterGradeDto> result = getClassGrade(classId, subjectId);

        if(result.isEmpty()){
            throw new RuntimeException("Không có dữ liệu cho lớp học này");
        }

        Optional<Class> optionalClass = classRepository.findById(classId);

        Optional<Semester> optionalSemester = semesterRepository.findById(optionalClass.get().getSemesterId());
        if(!optionalSemester.isPresent()){
            throw new RuntimeException("Không tìm thấy học kỳ này");
        }

        Optional<Subject> optionalSubject = subjectRepository.findById(Integer.parseInt(subjectId));
        if(!optionalSubject.isPresent()){
            throw new RuntimeException("Không tìm thấy môn học này");
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        CellStyle titleStyle = ExcelUtil.createStyleForTitle(workbook);
        Sheet sheet = workbook.createSheet(optionalSemester.get().getName() + " - " + optionalClass.get().getName());
        int rowNumber = 0;
        //row semester
        Row semesterRow = sheet.createRow(rowNumber);
        Cell semesterLabelCell = semesterRow.createCell(0, CellType.STRING);
        semesterLabelCell.setCellValue("Học kỳ:");
        semesterLabelCell.setCellStyle(titleStyle);
        Cell semesterNameCell = semesterRow.createCell(1, CellType.STRING);
        semesterNameCell.setCellValue(optionalSemester.get().getName());
        Cell yearLabelCell = semesterRow.createCell(2, CellType.STRING);
        yearLabelCell.setCellValue("Năm học:");
        yearLabelCell.setCellStyle(titleStyle);
        Cell yearNameCell = semesterRow.createCell(3, CellType.STRING);
        yearNameCell.setCellValue(optionalSemester.get().getYear());
        rowNumber++;
        //row class
        Row classRow = sheet.createRow(rowNumber);
        Cell classLabelCell = classRow.createCell(0, CellType.STRING);
        classLabelCell.setCellValue("Lớp:");
        classLabelCell.setCellStyle(titleStyle);
        Cell classNameCell = classRow.createCell(1, CellType.STRING);
        classNameCell.setCellValue(optionalClass.get().getName());
        Cell subjectLabelCell = classRow.createCell(2, CellType.STRING);
        subjectLabelCell.setCellValue("Môn:");
        subjectLabelCell.setCellStyle(titleStyle);
        Cell subjectNameCell = classRow.createCell(3, CellType.STRING);
        subjectNameCell.setCellValue(optionalSubject.get().getName());
        rowNumber++;
        //row title
        Row titleRow = sheet.createRow(rowNumber);
        Cell rollNumberTitleCell = titleRow.createCell(0, CellType.STRING);
        rollNumberTitleCell.setCellValue("Mã số");
        rollNumberTitleCell.setCellStyle(titleStyle);
        Cell fullNameTitleCell = titleRow.createCell(1, CellType.STRING);
        fullNameTitleCell.setCellValue("Họ tên");
        fullNameTitleCell.setCellStyle(titleStyle);
        Cell experimentTitleCell = titleRow.createCell(2, CellType.STRING);
        experimentTitleCell.setCellValue("Số thí nghiệm");
        experimentTitleCell.setCellStyle(titleStyle);
        Cell gradeTitleCell = titleRow.createCell(3, CellType.STRING);
        gradeTitleCell.setCellValue("Điểm");
        gradeTitleCell.setCellStyle(titleStyle);
        Cell statusTitleCell = titleRow.createCell(4, CellType.STRING);
        statusTitleCell.setCellValue("Trạng thái");
        statusTitleCell.setCellStyle(titleStyle);

        //fill data
        for(SemesterGradeDto grade : result){
            rowNumber ++;
            Row studentRow = sheet.createRow(rowNumber);
            Cell _rollNumberCell = studentRow.createCell(0, CellType.STRING);
            _rollNumberCell.setCellValue(grade.getRollNumber());
            Cell _fullNameCell = studentRow.createCell(1, CellType.STRING);
            _fullNameCell.setCellValue(grade.getFullName());
            Cell _experimentCell = studentRow.createCell(2, CellType.NUMERIC);
            _experimentCell.setCellValue(grade.getExperimentCount() != null ? grade.getExperimentCount() : 0);
            Cell _GradeCell = studentRow.createCell(3, CellType.NUMERIC);
            _GradeCell.setCellValue(grade.getGrade() != null ? (double)Math.round(grade.getGrade() * 10) / 10 : 0);
            Cell _statusCell = studentRow.createCell(4, CellType.STRING);
            String status = "Chưa đạt";
            if(grade.getGrade() != null){
                if (grade.getGrade() >= 9){
                    status = "Xuất xắc";
                } else if(grade.getGrade() >= 8 && grade.getGrade() < 9){
                    status = "Giỏi";
                } else if(grade.getGrade() >= 5 && grade.getGrade() < 8){
                    status = "Đạt";
                } else {
                    status = "Chưa đạt";
                }
            }
            _statusCell.setCellValue(status);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);

        String fileName = "Grade_" + optionalClass.get().getName().replaceAll("\\s+", "_")
                + "_" + optionalSemester.get().getName().replaceAll("\\s+", "_")
                + (optionalSubject.isPresent() ? "_" + optionalSubject.get().getName().replaceAll("\\s+", "_") : "")
                 + ".xlsx";
        File file = new File(fileName);
        FileOutputStream os = null;
        String s3Result;
        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            s3Result = s3Service.saveFile(file, "grades", fileName);
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
    public List<SemesterGradeDto> getClassGrade(String classId, String subjectId) {
        Optional<Class> optionalClass = classRepository.findById(classId);
        if(!optionalClass.isPresent()){
            throw new BadRequestException("Không tìm thấy dữ liệu về lớp này");
        }
        List<SemesterGradeDto> result = new ArrayList<>();
        List<ClassStudent> classStudents = classStudentRepository.findAllByClassId(classId);
        for (ClassStudent student : classStudents){
            OwnerGradeStatisticDto gradeStatistics = evaluationMapper
                    .getFinalGrades(student.getStudentId(), optionalClass.get().getId(), subjectId);
                Optional<User> optionalUser = userRepository.findById(student.getStudentId());
                if(optionalUser.isPresent()){
                    SemesterGradeDto grade = new SemesterGradeDto()
                            .setRollNumber(optionalUser.get().getId())
                            .setFullName(optionalUser.get().getFirstName() + " " + optionalUser.get().getLastName())
                            .setEmail(optionalUser.get().getEmail())
                            .setGender(optionalUser.get().getGender())
                            .setExperimentCount(gradeStatistics != null
                                    ? gradeStatistics.getExperimentCount()
                                    : 0)
                            .setGrade(gradeStatistics != null
                                    ? (float)Math.round(Float.parseFloat(gradeStatistics.getY()) * 10) / 10
                                    : 0);
                    result.add(grade);
                }
            }
        result.sort(Comparator.comparing(SemesterGradeDto::getGrade)
                .thenComparing(SemesterGradeDto::getExperimentCount)
                .thenComparing(SemesterGradeDto::getRollNumber).reversed());
        return result;
    }
}
