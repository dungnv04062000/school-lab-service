package com.schoollab.modelTest;

import com.schoollab.controller.request.*;
import com.schoollab.dto.*;
import com.schoollab.model.*;
import com.schoollab.model.Class;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SetUpModelTest {
    public static ExperimentDetailDto setUpExperimentDetailDto(){
        ExperimentDetailDto experimentDetailDto = new ExperimentDetailDto();
        BeanUtils.copyProperties(setUpExperimentDetail(), experimentDetailDto);
        return experimentDetailDto;
    }
    public static ExperimentDetail setUpExperimentDetail(){
        ExperimentDetail experimentDetail = new ExperimentDetail()
                .setId("23456")
                .setExperimentId("12")
                .setResult(9.0F)
                .setFirstInput(9.0F)
                .setSecondInput(9.0F)
                .setOrderNumber(123)
                .setCreateAt(Instant.now());
        return experimentDetail;
    }
    public static ExperimentDto setUpExperimentDto(){
        ExperimentDto experimentDto = new ExperimentDto();
        BeanUtils.copyProperties(setUpExperiment(), experimentDto);
        return experimentDto;
    }
    public static Experiment setUpExperiment(){
        Experiment experiment = new Experiment()
                .setId("12345")
                .setTitle("Test")
                .setCreateBy("HE140705")
                .setFirstLabel("a")
                .setSecondLabel("b")
                .setFirstMeasure("cm")
                .setSecondMeasure("cm")
                .setResultLabel("c")
                .setResultMeasure("cm")
                .setNoteId("123");
        return experiment;
    }
    public static OwnerGradeStatisticDto setUpOwnerGradeStatisticDto(){
        OwnerGradeStatisticDto ownerGradeStatisticDto = new OwnerGradeStatisticDto()
                .setX("50")
                .setY("50");
        return ownerGradeStatisticDto;
    }
    public static LessonGradeDto setUpLessonGradeDto(){
        LessonGradeDto lessonGradeDto = new LessonGradeDto()
                .setStudentId("HE140705")
                .setGrade(9.0F)
                .setGroupName("1")
                .setImplementation(9.0F)
                .setPreparation(9.0F)
                .setPresentation(9.0F)
                .setExperimentCount(5)
                .setHardWorking(9.0F)
                .setSkill(9.0F)
                .setTeamwork(9.0F)
                .setTotal(9.0F)
                .setGroupId("1");
        return lessonGradeDto;
    }
    public static SemesterDto setUpSemesterDto(){
        SemesterDto semesterDto = new SemesterDto();
        BeanUtils.copyProperties(setUpSemester(), semesterDto);
        return semesterDto;
    }
    public static Semester setUpSemester(){
        Semester semester = new Semester()
                .setId("1")
                .setName("Fall2022")
                .setCampusId("FPT")
                .setYear(2022)
                .setStartAt(Instant.now())
                .setCreateAt(Instant.now());
        return semester;
    }
    public static GradeExcelRowDto setUpGradeExcelRowDto(){
        GradeExcelRowDto gradeExcelRowDto = new GradeExcelRowDto()
                .setGrade(9.0F)
                .setRollNumber("HE140705");
        return gradeExcelRowDto;
    }
    public static EvaluationDto setUpEvaluationDto(){
        EvaluationDto evaluationDto = new EvaluationDto();
        BeanUtils.copyProperties(setUpEvaluation(), evaluationDto);
        return evaluationDto;
    }
    public static Evaluation setUpEvaluation(){
        Evaluation evaluation = new Evaluation()
                .setId("123456")
                .setGrade(9.0F)
                .setToId("HE140705")
                .setFromId("HE140704")
                .setLessonId("12345")
                .setCreateAt(Instant.now());
        return evaluation;
    }
    public static EvaluationGroupDto setUpEvaluationGroupDto(){
        EvaluationGroupDto evaluationGroupDto = new EvaluationGroupDto();
        BeanUtils.copyProperties(setUpEvaluationGroup(), evaluationGroupDto);
        return evaluationGroupDto;
    }
    public static EvaluationGroup setUpEvaluationGroup(){
        EvaluationGroup evaluationGroup = new EvaluationGroup()
                .setId("123456")
                .setGroupId("1")
                .setProduction(25)
                .setPreparation(25)
                .setPresentation(25)
                .setImplementation(25)
                .setFromId("HE140705")
                .setLessonId("TEST")
                .setCreateAt(Instant.now());
        return evaluationGroup;
    }
    public static LessonDto setUpLessonDto(){
        LessonDto lessonDto = new LessonDto();
        BeanUtils.copyProperties(setUpLesson(), lessonDto);
        return lessonDto;
    }
    public static Lesson setUpLesson(){
        Lesson lesson = new Lesson()
                .setId("abcd1234")
                .setSubjectId(1)
                .setGoal("test")
                .setClassId("10A1")
                .setStatus("Pending")
                .setTitle("Test")
                .setCreateBy("HE140705")
                .setContext("Context")
                .setAttachmentUrl("/url/test")
                .setStemKnowledgeId("STEM")
                .setEvaluationCriteriaId("Test")
                .setLevelId(1)
                .setCreateAt(Instant.now());
        return lesson;
    }
    public static CommentDto setUpCommentDto(){
        CommentDto commentDto = new CommentDto();
        BeanUtils.copyProperties(setUpComment(), commentDto);
        return commentDto;
    }
    public static Comment setUpComment(){
        Comment comment = new Comment()
                .setId("abc123")
                .setCommentId("1")
                .setFromId("HE140705")
                .setContent("New comment")
                .setLessonId("123")
                .setCreateAt(Instant.now());
        return comment;
    }
    public static CommentUpdateRequestBody commentUpdateReq(){
        CommentUpdateRequestBody req = new CommentUpdateRequestBody();
        req.setContent("UPDATE");
        return req;
    }
    public static CommentCreateRequestBody commentReq(){
        CommentCreateRequestBody req = new CommentCreateRequestBody();
        req.setCommentId("1");
        req.setContent("ABC");
        req.setLessonId("123");
        return req;
    }
    public static GroupMemberDto setUpGroupMemberDto(){
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        BeanUtils.copyProperties(setUpGroupMember(), groupMemberDto);
        return groupMemberDto;
    }
    public static GroupMember setUpGroupMember(){
        GroupMember groupMember = new GroupMember()
                .setGroupId("1")
                .setMemberId("HE140705")
                .setId("abc111");
        return groupMember;
    }
    public static TutorialUpdateRequestBody tutorialUpdateReq(){
        TutorialUpdateRequestBody req = new TutorialUpdateRequestBody();
        req.setDescription("UPDATE");
        req.setTitle("TEST");
        return req;
    }
    public static TutorialCreateRequestBody tutorialCreateReq(){
        TutorialCreateRequestBody req = new TutorialCreateRequestBody();
        req.setTitle("Khoa hoc");
        req.setDescription("Test");
        return req;
    }
    public static TutorialDto setUpTutorialDto(){
        TutorialDto tutorialDto = new TutorialDto();
        BeanUtils.copyProperties(setUpTutorial(), tutorialDto);
        return tutorialDto;
    }
    public static Tutorial setUpTutorial(){
        Tutorial tutorial = new Tutorial()
                .setId("123456")
                .setTitle("Khoa hojc")
                .setDescription("Test")
                .setCreateBy("GV002")
                .setAttachmentUrl("https/tutorials")
                .setCreateAt(Instant.now());
        return tutorial;
    }
    public static ClassGroupDto setUpClassGroupDto(){
        ClassGroupDto classGroupDto = new ClassGroupDto();
        BeanUtils.copyProperties(setUpClassGroup(), classGroupDto);
        return classGroupDto;
    }
    public static ClassGroup setUpClassGroup(){
        ClassGroup classGroup = new ClassGroup()
                .setId("1234")
                .setClassId("10")
                .setName("10A6")
                .setLessonId("123456")
                .setCreateAt(Instant.now());
        return classGroup;
    }
    public static SubmissionDto setUpSubmissionDto(){
        SubmissionDto submissionDto = new SubmissionDto();
        BeanUtils.copyProperties(setUpSubmission(), submissionDto);
        return submissionDto;
    }
    public static Submission setUpSubmission(){
        Submission submission = new Submission()
                .setId("123456789")
                .setFromId("He140705")
                .setLessonId("1234")
                .setAttachmentUrl("this/url")
                .setContent("Nop bai hoa hoc")
                .setCreateAt(Instant.now());
        return submission;
    }
    public static SubjectDto setUpSubjectDto(){
        SubjectDto subjectDto = new SubjectDto();
        BeanUtils.copyProperties(setUpSubject(), subjectDto);
        return subjectDto;
    }
    public static Subject setUpSubject(){
        Subject subject = new Subject()
                .setId(1)
                .setName("Khoa học");
        return subject;
    }
    public static SubjectAddRequest subjectReq(){
        SubjectAddRequest req = new SubjectAddRequest();
        req.setName("Khoa học");
        return req;
    }
    public static ResetPasswordRequestBody resetReq(){
        ResetPasswordRequestBody req = new ResetPasswordRequestBody();
        req.setPassword("abc");
        return req;
    }
    public static ForgotPasswordRequestBody forgotPassReq(){
        ForgotPasswordRequestBody req = new ForgotPasswordRequestBody();
        req.setEmail("dungnvhe140705@fpt.edu.vn");
        return req;
    }
    public static UserUpdateRequestBody userUpdateReq(){
        UserUpdateRequestBody req = new UserUpdateRequestBody();
        BeanUtils.copyProperties(userRegisterReq(), req);
        return req;
    }
    public static UserRegisterRequestBody userRegisterReq(){
        UserRegisterRequestBody req = new UserRegisterRequestBody();
        req.setEmail("dungnvh@fpt.edu.vn");
        BeanUtils.copyProperties(emailRegisterRq(), req);
        return req;
    }
    public static EmailRegisterRequestBody emailRegisterRq(){
        EmailRegisterRequestBody req = new EmailRegisterRequestBody();
        req.setIdToken("ADBDBDBAHHFASASDASDASD");
        req.setUserId("HE140705");
        req.setGender("MALE");
        req.setCampusId("123456789");
        req.setBirthDate(new Date());
        req.setCity("Ha Noi");
        req.setDistrict("Ha Noi");
        req.setStreet("Ha Noi");
        req.setWard("Ha Noi");
        req.setCityCode("1");
        req.setWardCode("1");
        req.setDistrictCode("1");
        req.setFirstName("Nguyen");
        req.setLastName("Dung");
        req.setPassword("1234512345");
        req.setPhoneNumber("0989898989");
        req.setRole("1");
        return req;
    }
    public static Role setUpRole(int id, String name){
        Role role = new Role()
                .setId(id)
                .setName(name);
        return role;
    }
    public static UserRole setUpUserRole(Role role){
        UserRole userRole = new UserRole()
                .setId("1234")
                .setUserId("HE140705")
                .setRoleId(role.getId());
        return userRole;
    }
    public static TeacherInClassDto setUpTeacherInClassDto(){
        TeacherInClassDto teacherInClassDto = new TeacherInClassDto()
                .setId("HE140705")
                .setGender("MALE")
                .setCampusName("FPT")
                .setFullName("Nguyen Viet Dung")
                .setBirthDate(Instant.now())
                .setPhoneNumber("098989898")
                .setImageUrl("this/image")
                .setEmail("dungnvhe@fpt.edu.vn");
        return teacherInClassDto;
    }
    public static ClassTeacherDto setUpClassTeacherDto(){
        ClassTeacherDto classTeacherDto = new ClassTeacherDto();
        BeanUtils.copyProperties(setUpClassTeacher(), classTeacherDto);
        return classTeacherDto;
    }
    public static ClassTeacher setUpClassTeacher(){
        ClassTeacher classTeacher = new ClassTeacher()
                .setId("123456789")
                .setClassId("123456")
                .setTeacherId("HE140705")
                .setCreateAt(Instant.now());
        return classTeacher;
    }
    public static ClassTeacherRequest classTeacherReq(){
        ClassTeacherRequest req = new ClassTeacherRequest();
        req.setClassId("123456");
        req.setTeacherId("HE140705");
        return req;
    }

    public static ClassStudentAddRequestBody singleStudentReq(){
        ClassStudentAddRequestBody req = new ClassStudentAddRequestBody();
        req.setStudentId("HE140706");
        req.setClassId("123456");
        return req;
    }
    public static ClassStudentDto setUpClassStudentDto(){
        ClassStudentDto classStudentDto = new ClassStudentDto();
        BeanUtils.copyProperties(setUpClassStudent(), classStudentDto);
        return classStudentDto;
    }
    public static List<StudentInClassDto> setUpListStudentInClass(){
        List<StudentInClassDto> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StudentInClassDto st = setUpStudentInClassDto();
            st.setId(st.getId()+i);
            st.setEmail(st.getEmail()+i+"@fpt.com.vn");
            list.add(st);
        }
        return list;
    }
    public static StudentInClassDto setUpStudentInClassDto(){
        StudentInClassDto studentInClassDto = new StudentInClassDto()
                .setId("123456")
                .setFullName("Nguyen Viet Dung")
                .setBirthDate(Instant.now())
                .setGender("MALE")
                .setImageUrl("this/image")
                .setPhoneNumber("098989898")
                .setEmail("dungnvhe4070");
        return studentInClassDto;
    }
    public static ClassStudent setUpClassStudent(){
        ClassStudent classStudent = new ClassStudent()
                .setId("123456")
                .setStudentId("HE140705")
                .setClassId("123456")
                .setCreateAt(Instant.now());
        return classStudent;
    }
    public static UserDto setUpUserDto(){
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(setUpUser(), userDto);
        return userDto;
    }

    public static User setUpUser(){
        User user = new User()
                .setId("HE140705")
                .setFirstName("Nguyen")
                .setLastName("Dung")
                .setEmail("dungnvhe140705@fpt.edu.vn")
                .setGender("MALE")
                .setAddressId("1234")
                .setBirthDate(new Date())
                .setPhoneNumber("09828282828")
                .setImageUrl("https://www.google.com/intl/vi/gmail/about/")
                .setCampusId("FPT")
                .setRegisterAt(Instant.now());
        return user;
    }
    public static ClassDto setUpClassDto(){
        ClassDto classDto = new ClassDto();
        BeanUtils.copyProperties(setUpClass(), classDto);
        return classDto;
    }
    public static Class setUpClass(){
        Class model = new Class()
                .setId("15646")
                .setGradeId(10)
                .setName("10A1")
                .setCampusId("123456789")
                .setFormTeacherId("HE140705")
                .setCreateAt(Instant.now());
        return model;
    }
    public static ClassUpdateRequestBody clasUpdateReq(){
        ClassUpdateRequestBody requestBody = new ClassUpdateRequestBody();
        requestBody.setFormTeacherId("HE140705");
        requestBody.setName("10A1");
        requestBody.setGradeId(1);
        return requestBody;
    }
    public static ClassCreateRequestBody clasReq(){
        ClassCreateRequestBody requestBody = new ClassCreateRequestBody();
        requestBody.setCampusId("123456789");
        requestBody.setGradeId(10);
        requestBody.setFormTeacherId("HE140705");
        requestBody.setName("10A1");
        return requestBody;
    }

    public static Account setUpAccount(){
        Account account = new Account()
                .setUserId("HE140705")
                .setUsername("dungnv")
                .setPassword("dung1234")
                .setSalt("private")
                .setIsVerify(true)
                .setUpdateAt(Instant.now());
        return account;
    }
    public static NoteUpdateRequestBody noteUpdateReq(){
        NoteUpdateRequestBody req = new NoteUpdateRequestBody();
        req.setContent("Bài học quan trọng");
        return req;
    }

    public static AccountDto setUpAccountDto(){
        AccountDto accountDto = new AccountDto();
        BeanUtils.copyProperties(setUpAccount(), accountDto);
        return accountDto;
    }
    public static NoteAddRequest noteReq(){
        NoteAddRequest noteAddRequest = new NoteAddRequest();
        noteAddRequest.setLessonId("1234");
        noteAddRequest.setContent("Test abc");
        return noteAddRequest;
    }

    public static Note setUpNote(){
        Note note = new Note()
                .setId(UUID.randomUUID().toString())
                .setUserId("HE140705")
                .setContent("Bài học này cần sự chú ý cao")
                .setLessonId("1234")
                .setCreateAt(Instant.now());
        return note;
    }

    public static NoteDto setUpNoteDto(){
        NoteDto noteDto = new NoteDto();
        noteDto.setId(UUID.randomUUID().toString());
        noteDto.setUserId("HE140705");
        noteDto.setContent("Bài học này cần sự chú ý cao");
        noteDto.setLessonId("1234");
        noteDto.setCreateAt(Instant.now());
        return noteDto;
    }
    public static CampusCreateRequestBody campusReq(){
        CampusCreateRequestBody requestBody = new CampusCreateRequestBody();
        requestBody.setName("FPT Hòa Lạc Campus");
        requestBody.setStreet("Liên Hà");
        requestBody.setWardCode("1");
        requestBody.setWard("Xã Vân Hà");
        requestBody.setDistrictCode("2");
        requestBody.setDistrict("Huyện Đông Anh");
        requestBody.setCityCode("1");
        requestBody.setCity("Tỉnh Hà Nội");
        return requestBody;
    }

    public static CampusDto setUpCampusDto(){
        CampusDto campusDto = new CampusDto();
        BeanUtils.copyProperties(campusReq(), campusDto);
        campusDto.setId("123456789");
        campusDto.setCreateAt(Instant.now());
        return campusDto;
    }

    public static Campus setUpCampus(){
        Campus campus = new Campus()
                .setId("123456789")
                .setName("FPT Hòa Lạc Campus")
                .setAddressId("123456")
                .setCreateAt(Instant.now());
        return campus;
    }

    public static Address setUpAddress(){
        Address address = new Address()
                .setId("123456")
                .setStreet("Liên Hà")
                .setWardId("123")
                .setCreateAt(Instant.now());
        return address;
    }

    public static Ward setUpWard(){
        Ward ward = new Ward()
                .setId("123")
                .setCode("1")
                .setName("Xã Vân Hà")
                .setDistrictId("1234");
        return ward;
    }

    public static District setUpDistrict(){
        District district = new District()
                .setId("1234")
                .setCode("2")
                .setName("Huyện Đông Anh")
                .setCityId("133");
        return district;
    }

    public static City setUpCity(){
        City city = new City()
                .setId("133")
                .setCode("1")
                .setName("Tỉnh Hà Nội");
        return city;
    }
}
