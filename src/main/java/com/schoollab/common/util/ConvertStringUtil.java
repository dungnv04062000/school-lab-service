package com.schoollab.common.util;

public class ConvertStringUtil {

    public static String replaceHtmlBreakLine(String content){
        String _content = content
                .replaceAll("\r\n\r\n", "<br/>")
                .replaceAll("\r\n", "")
                .replaceAll("<p>&nbsp;</p>", "<br/>")
                .replaceAll("<p>", "<div>")
                .replaceAll("</p>", "</div>");
        return _content;
    }

    public static String replaceHtmlBreakLineTextArea(String content){
        String _content = content
                .replaceAll("\r\n\r\n", "<br/>")
                .replaceAll("\r\n", "<br/>");
        return _content;
    }
}
