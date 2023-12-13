package com.schoollab.service.impl;

import com.schoollab.common.enums.LessonStatusEnum;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.util.ConvertStringUtil;
import com.schoollab.common.util.TimeUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.LessonCreateRequestBody;
import com.schoollab.controller.request.LessonUpdateRequestBody;
import com.schoollab.dto.EvaluationCriteriaDto;
import com.schoollab.dto.LessonDto;
import com.schoollab.dto.STEMKnowledgeDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.CommentMapper;
import com.schoollab.mapper.LessonMapper;
import com.schoollab.model.*;
import com.schoollab.model.Class;
import com.schoollab.repository.*;
import com.schoollab.service.LessonService;
import com.schoollab.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    ClassStudentRepository classStudentRepository;

    @Autowired
    LessonHistoryRepository lessonHistoryRepository;

    @Autowired
    STEMKnowledgeRepository stemKnowledgeRepository;

    @Autowired
    EvaluationCriteriaRepository evaluationCriteriaRepository;

    @Autowired
    GenericMapper genericMapper;

    @Autowired
    LessonMapper lessonMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    S3Service s3Service;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public LessonDto createLesson(String createBy, LessonCreateRequestBody requestBody, MultipartFile multipartFile) {
        if(lessonRepository.findByTitle(requestBody.getTitle()) != null){
            throw new BadRequestException("Tên dự án này đã tồn tại");
        }

        //lưu kiến thức stem
        STEMKnowledge stemKnowledge = new STEMKnowledge()
                .setScience(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getScience()))
                .setTechnology(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getTechnology()))
                .setEngineering(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getEngineering()))
                .setMathematics(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getMathematics()))
                .setCreateAt(Instant.now());
        STEMKnowledge savedStemKnowledge = stemKnowledgeRepository.save(stemKnowledge);

        //lưu tiêu chí đánh giá điểm
        EvaluationCriteria evaluationCriteria = new EvaluationCriteria()
                .setPreparation(requestBody.getPreparation())
                .setImplementation(requestBody.getImplementation())
                .setPresentation(requestBody.getPresentation())
                .setProduction(requestBody.getProduction())
                .setCreateAt(Instant.now());
        EvaluationCriteria savedEvaluationCriteria = evaluationCriteriaRepository.save(evaluationCriteria);

        Lesson lesson = new Lesson()
                .setTitle(requestBody.getTitle().trim())
                .setClassId(requestBody.getClassId())
                .setSubjectId(requestBody.getSubjectId())
                .setContext(ConvertStringUtil.replaceHtmlBreakLine(requestBody.getContext().trim()))
                .setGoal(ConvertStringUtil.replaceHtmlBreakLine(requestBody.getGoal().trim()))
                .setStemKnowledgeId(savedStemKnowledge.getId())
                .setEvaluationCriteriaId(savedEvaluationCriteria.getId())
                .setLevelId(requestBody.getLevelId())
                .setCreateBy(createBy)
                .setCreateAt(Instant.now())
                .setStatus(LessonStatusEnum.ONGOING.name());
        if(requestBody.getEndAt() != null && !requestBody.getEndAt().equals("null")){
            lesson.setEndAt(Instant.ofEpochSecond(Long.parseLong(requestBody.getEndAt())));
        }
        Lesson savedLesson = lessonRepository.save(lesson);
        if(multipartFile != null && !multipartFile.isEmpty()) {
            String attachmentUrl = s3Service.saveFile(multipartFile, "lessons/" + savedLesson.getId(), null);
            savedLesson.setAttachmentUrl(attachmentUrl);
        }
        lessonRepository.save(savedLesson);

        LessonDto lessonDto = genericMapper.mapToTypeNotNullProperty(savedLesson, LessonDto.class);

        lessonDto.setScience(savedStemKnowledge.getScience().trim());
        lessonDto.setTechnology(savedStemKnowledge.getTechnology().trim());
        lessonDto.setEngineering(savedStemKnowledge.getEngineering().trim());
        lessonDto.setMathematics(savedStemKnowledge.getMathematics().trim());

        lessonDto.setPreparation(savedEvaluationCriteria.getPreparation());
        lessonDto.setImplementation(savedEvaluationCriteria.getImplementation());
        lessonDto.setPresentation(savedEvaluationCriteria.getPresentation());
        lessonDto.setProduction(savedEvaluationCriteria.getProduction());
        return lessonDto;
    }

    @Override
    public LessonDto copyLessonToOtherClass(String lessonId, String classId) {
        Optional<Class> optClass = classRepository.findById(classId);
        if(!optClass.isPresent()){
            throw new NotFoundException("Không tìm thấy lớp này");
        }
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }

        Lesson lesson = optLesson.get();
        Lesson copiedLesson = new Lesson();
        genericMapper.copyPropertiesIgnoreNull(lesson, copiedLesson);
        copiedLesson.setId(null);
        copiedLesson.setClassId(optClass.get().getId());
        copiedLesson.setCreateAt(Instant.now());
        copiedLesson.setUpdateAt(null);
        Lesson savedLesson = lessonRepository.save(copiedLesson);
        return genericMapper.mapToTypeNotNullProperty(savedLesson, LessonDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public LessonDto updateLesson(String lessonId, LessonUpdateRequestBody requestBody, MultipartFile multipartFile) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(lessonId);
        if(!optionalLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy bài học này");
        }

        Lesson lesson = optionalLesson.get();
        LessonHistory lessonHistory = new LessonHistory()
                .setLessonId(lesson.getId())
                .setContext(lesson.getContext())
                .setGoal(lesson.getGoal())
                .setAttachmentUrl(lesson.getAttachmentUrl())
                .setCreateAt(Instant.now());



        lesson.setTitle(requestBody.getTitle());
        lesson.setContext(ConvertStringUtil.replaceHtmlBreakLine(requestBody.getContext().trim()));
        lesson.setGoal(ConvertStringUtil.replaceHtmlBreakLine(requestBody.getGoal().trim()));
        lesson.setLevelId(requestBody.getLevelId());
        lesson.setUpdateAt(Instant.now());
        if(requestBody.getEndAt() != null && !requestBody.getEndAt().equals("null")){
            lesson.setEndAt(Instant.ofEpochSecond(Long.parseLong(requestBody.getEndAt())));
        }
        if(multipartFile != null && !multipartFile.isEmpty()){
            if(lesson.getAttachmentUrl() != null){
                s3Service.deleteFile(lesson.getAttachmentUrl());
            }
            String attachmentUrl = s3Service.saveFile(multipartFile,"lessons/" + lesson.getId(), null);
            lesson.setAttachmentUrl(attachmentUrl);
        }

        Lesson savedLesson = lessonRepository.save(lesson);
        lessonHistoryRepository.save(lessonHistory);

        //update STEM knowledge
        Optional<STEMKnowledge> optStemKnowledge = stemKnowledgeRepository.findById(lesson.getStemKnowledgeId());
        if(optStemKnowledge.isPresent()) {
            STEMKnowledge stemKnowledge = optStemKnowledge.get();
            stemKnowledge.setScience(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getScience().trim()));
            stemKnowledge.setTechnology(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getTechnology().trim()));
            stemKnowledge.setEngineering(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getEngineering().trim()));
            stemKnowledge.setMathematics(ConvertStringUtil.replaceHtmlBreakLineTextArea(requestBody.getMathematics().trim()));
            stemKnowledge.setUpdateAt(Instant.now());
            stemKnowledgeRepository.save(stemKnowledge);
        }

        //update Evaluation criteria
        Optional<EvaluationCriteria> optionalEvaluationCriteria =
                evaluationCriteriaRepository.findById(lesson.getEvaluationCriteriaId());
        if(optionalEvaluationCriteria.isPresent()) {
            EvaluationCriteria evaluationCriteria = optionalEvaluationCriteria.get();
            evaluationCriteria.setPreparation(requestBody.getPreparation());
            evaluationCriteria.setImplementation(requestBody.getImplementation());
            evaluationCriteria.setPresentation(requestBody.getPresentation());
            evaluationCriteria.setProduction(requestBody.getProduction());
            evaluationCriteria.setUpdateAt(Instant.now());
            evaluationCriteriaRepository.save(evaluationCriteria);
        }
        return genericMapper.mapToTypeNotNullProperty(savedLesson, LessonDto.class);
    }

    @Override
    public LessonDto finishingLesson(String lessonId) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }

        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.FINISHED.name())){
            throw new RuntimeException("Dự án này đã kết thúc từ trước");
        }
        lesson.setEndAt(Instant.now());
        lesson.setStatus(LessonStatusEnum.FINISHED.name());
        lessonRepository.save(lesson);
        return genericMapper.mapToTypeNotNullProperty(lesson, LessonDto.class);
    }

    @Override
    public LessonDto reOpenningLesson(String lessonId, Instant endAt) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }

        Lesson lesson = optLesson.get();
        if(lesson.getStatus().equals(LessonStatusEnum.ONGOING.name())){
            throw new RuntimeException("Dự án này vẫn đang được diễn ra");
        }
        lesson.setEndAt(endAt);
        lesson.setStatus(LessonStatusEnum.ONGOING.name());
        lessonRepository.save(lesson);
        return genericMapper.mapToTypeNotNullProperty(lesson, LessonDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public LessonDto deleteLesson(String lessonId) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(lessonId);
        if(!optionalLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy bài học này");
        }

        Lesson lesson = optionalLesson.get();
        if(lesson.getAttachmentUrl() != null) {
            s3Service.deleteFile(lesson.getAttachmentUrl());
        }

        lessonHistoryRepository.deleteAllByLessonId(lessonId);
        lessonRepository.delete(lesson);

        lessonHistoryRepository.deleteAllByLessonId(lessonId);
        stemKnowledgeRepository.deleteById(lesson.getStemKnowledgeId());
        evaluationCriteriaRepository.deleteById(lesson.getEvaluationCriteriaId());

        return genericMapper.mapToTypeNotNullProperty(lesson, LessonDto.class);
    }

    @Override
    public List<LessonDto> getAllLessons(String classId, String userId, Integer levelId, String subjectId, String title, Instant createAtFrom, Instant createAtTo, Integer page, Integer rowNumber) {
        return lessonMapper.getAllLessons(classId, userId, levelId, subjectId, title, createAtFrom, createAtTo, page, rowNumber);
    }

    @Override
    public int countAllLessons(String classId, String userId, Integer levelId, String subjectId, String title, Instant createAtFrom, Instant createAtTo) {
        return lessonMapper.countAllLessons(classId, userId, levelId, subjectId, title, createAtFrom, createAtTo);
    }

    @Override
    public List<LessonDto> getAllLessonsForStudent(String semesterId, String subjectId, String title, Integer levelId, Instant createAtFrom, Instant createAtTo, Integer page, Integer rowNumber) {
        List<Class> classes = classRepository.findAllBySemesterId(semesterId);
        String classId = null;
        for (Class _class : classes) {
            ClassStudent classStudent = classStudentRepository.findByClassIdAndStudentId(_class.getId(), UserInfoUtil.getUserID());
            if(classStudent != null){
                classId = classStudent.getClassId();
                break;
            }
        }
        return lessonMapper.getAllLessonsForStudent(classId, subjectId, title, levelId, createAtFrom, createAtTo, page, rowNumber);
    }

    @Override
    public int countAllLessonsForStudent(String semesterId, String subjectId, String title, Integer levelId, Instant createAtFrom, Instant createAtTo) {
        List<Class> classes = classRepository.findAllBySemesterId(semesterId);
        String classId = null;
        for (Class _class : classes) {
            ClassStudent classStudent = classStudentRepository.findByClassIdAndStudentId(_class.getId(), UserInfoUtil.getUserID());
            if(classStudent != null){
                classId = classStudent.getClassId();
                break;
            }
        }
        return lessonMapper.countAllLessonsForStudent(classId, subjectId, title, levelId, createAtFrom, createAtTo);
    }

    @Override
    public LessonDto getOne(String id) {
        LessonDto lessonDto = lessonMapper.getOne(id);
        int countComment = commentMapper.countComments(lessonDto.getId());
        lessonDto.setCountComment(countComment);
        return lessonDto;
    }
}
