package com.schoollab.common.constants;

public class Constants {

    public static final int ONE_HOUR = 1;
    public static final int DAYS_IN_ONE_WEEK = 7;
    public static final int MINUTES_IN_ONE_HOUR = 60;
    public static final int HOURS_IN_ONE_DAY = 24;
    public static final int SECONDS_IN_ONE_MINUTES = 60;
    public static final int MILLISECONDS_IN_ONE_SECOND = 1000;
    public static final int JWT_EXPIRE_ACCESS = 30 * SECONDS_IN_ONE_MINUTES * MILLISECONDS_IN_ONE_SECOND; //30 minutes
    public static final int JWT_EXPIRE_REFRESH =  ONE_HOUR * MINUTES_IN_ONE_HOUR
            * SECONDS_IN_ONE_MINUTES * MILLISECONDS_IN_ONE_SECOND; //1 hour
    public static final String JWT_SECRET_KEY = "schoollab";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_CLAIMS_ROLE = "roles";

    //ROLE
    public static final String ROLE_ROOT_ADMIN = "ROOT_ADMIN";
    public static final String ROLE_SCHOOL_ADMIN = "SCHOOL_ADMIN";
    public static final String ROLE_FORM_TEACHER= "FORM_TEACHER";
    public static final String ROLE_TEACHER= "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final int ROLE_ROOT_ADMIN_ID = 5;
    public static final int ROLE_SCHOOL_ADMIN_ID = 1;
    public static final int ROLE_FORM_TEACHER_ID = 2;
    public static final int ROLE_TEACHER_ID = 3;
    public static final int ROLE_STUDENT_ID = 4;

    //URL Path
    public static final String URL_PATH_LOGIN = "/v1/login";
    public static final String URL_PATH_REGISTER = "/v1/register";
    public static final String URL_PATH_REGISTER_BY_EMAIL = "/v1/register-by-email";
    public static final String URL_PATH_REFRESH_TOKEN = "/v1/auth/refresh-token";
    public static final String URL_PATH_USERS = "/v1/users/**";

    public static final Float MARK_RATE = 2f;
    public static final Float MIN_MARK = 0f;
    public static final Float MAX_MARK = 10f;

    //Submission status
    public static final String SUBMISSION_STATUS_ONTIME = "ONTIME";
    public static final String SUBMISSION_STATUS_LATE = "LATE";

    //Submission status
    public static final String SUPPORT_STATUS_WAITTING = "WAITTING";
    public static final String SUPPORT_STATUS_DONE = "DONE";


}
