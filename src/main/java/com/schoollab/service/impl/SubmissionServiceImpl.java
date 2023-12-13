package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.util.ConvertStringUtil;
import com.schoollab.dto.SubmissionDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.SubmissionMapper;
import com.schoollab.model.Lesson;
import com.schoollab.model.Submission;
import com.schoollab.repository.LessonRepository;
import com.schoollab.repository.SubmissionRepository;
import com.schoollab.service.S3Service;
import com.schoollab.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    GenericMapper genericMapper;

    @Autowired
    SubmissionMapper submissionMapper;

    @Autowired
    S3Service s3Service;

    @Override
    public SubmissionDto createSubmission(String fromId, String lessonId, String content, MultipartFile multipartFile) {
        Optional<Lesson> optLesson = lessonRepository.findById(lessonId);
        if(!optLesson.isPresent()){
            throw new BadRequestException("Không tìm thấy dự án này");
        }

        Lesson lesson = optLesson.get();

        Submission alreadySubmission = submissionRepository.findAllByFromIdAndLessonId(fromId, lessonId);
        if(alreadySubmission != null){
            return editSubmission(alreadySubmission.getId(), content, multipartFile);
        }
        Submission submission = new Submission()
                .setFromId(fromId)
                .setLessonId(lessonId)
                .setContent(ConvertStringUtil.replaceHtmlBreakLine(content))
                .setCreateAt(Instant.now());

        if(lesson.getEndAt() != null && lesson.getEndAt().isBefore(Instant.now())){
            submission.setStatus(Constants.SUBMISSION_STATUS_LATE);
            submission.setLateTime( (int) (Instant.now().getEpochSecond() - (lesson.getEndAt().getEpochSecond())));
        } else {
            submission.setStatus(Constants.SUBMISSION_STATUS_ONTIME);
        }

        Submission savedSubmission = submissionRepository.save(submission);
        String downloadUrl = s3Service.saveFile(multipartFile, "submission/" + savedSubmission.getId(), null);
        savedSubmission.setAttachmentUrl(downloadUrl);
        submissionRepository.save(savedSubmission);

        return genericMapper.mapToTypeNotNullProperty(savedSubmission, SubmissionDto.class);
    }

    @Override
    public SubmissionDto editSubmission(String submissionId, String content, MultipartFile multipartFile) {
        Optional<Submission> optSubmission = submissionRepository.findById(submissionId);
        if(!optSubmission.isPresent()){
            throw new BadRequestException("Không tìm thấy bài nộp này");
        }

        Submission submission = optSubmission.get();
        Optional<Lesson> optLesson = lessonRepository.findById(submission.getLessonId());
        if(!optLesson.isPresent()){
            throw new BadRequestException("Không tìm thấy dự án này");
        }
        Lesson lesson = optLesson.get();

        submission.setContent(ConvertStringUtil.replaceHtmlBreakLine(content));
        if(multipartFile != null){
            //nếu nộp file mới
            if(submission.getAttachmentUrl() != null){
                //xóa file cũ nếu có
                s3Service.deleteFile(submission.getAttachmentUrl());
            }
            //lưu lại file mới
            String downloadUrl = s3Service.saveFile(multipartFile, "submission/" + submission.getId(), null);
            submission.setAttachmentUrl(downloadUrl);
        }
        submission.setUpdateAt(Instant.now());
        if(lesson.getEndAt() != null && lesson.getEndAt().isBefore(Instant.now())){
            submission.setStatus(Constants.SUBMISSION_STATUS_LATE);
            submission.setLateTime( (int) (Instant.now().getEpochSecond() - (lesson.getEndAt().getEpochSecond())));
        } else {
            submission.setStatus(Constants.SUBMISSION_STATUS_ONTIME);
            submission.setLateTime(null);
        }
        Submission savedSubmission = submissionRepository.save(submission);

        return genericMapper.mapToTypeNotNullProperty(savedSubmission, SubmissionDto.class);
    }

    @Override
    public SubmissionDto getSubmissionByFromIdAndLessonId(String userId, String lessonId) {
        Submission submission = submissionRepository.findAllByFromIdAndLessonId(userId, lessonId);
        return genericMapper.mapToTypeNotNullProperty(submission, SubmissionDto.class);
    }

    @Override
    public SubmissionDto getOne(String submissionId) {
        return submissionMapper.getOne(submissionId);
    }

    @Override
    public List<SubmissionDto> getAll(String userId, String classId, String semesterId, String lessonTitle, String student, Instant createAtFrom, Instant createAtTo, String orderBy, Integer page, Integer rowNumber) {
        return submissionMapper.getAll(userId, classId, semesterId, lessonTitle, student, createAtFrom, createAtTo, orderBy, page, rowNumber);
    }

    @Override
    public int countAll(String userId, String classId, String semesterId, String lessonTitle, String student, Instant createAtFrom, Instant createAtTo) {
        return submissionMapper.countAll(userId, classId, semesterId, lessonTitle, student, createAtFrom, createAtTo);
    }

    @Override
    public List<SubmissionDto> getOwnerSubmissions(String userId, String semesterId, String lessonTitle, Instant createAtFrom, Instant createAtTo, String orderBy, Integer page, Integer rowNumber) {
        return submissionMapper.getOwnerSubmissions(userId, semesterId, lessonTitle, createAtFrom, createAtTo, orderBy, page, rowNumber);
    }

    @Override
    public int countOwnerSubmissions(String userId, String semesterId, String lessonTitle, Instant createAtFrom, Instant createAtTo) {
        return submissionMapper.countOwnerSubmissions(userId, semesterId, lessonTitle, createAtFrom, createAtTo);
    }
}
