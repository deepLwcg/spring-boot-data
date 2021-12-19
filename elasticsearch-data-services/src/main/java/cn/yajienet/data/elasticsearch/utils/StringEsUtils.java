package cn.yajienet.data.elasticsearch.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/20
 * @Version 1.0.0
 * @Description
 */
public class StringEsUtils {

    private static final Pattern HUMP_PATTERN = Pattern.compile( "[A-Z]" );
    private static final Pattern LINE_PATTERN = Pattern.compile( "_(\\w)" );


    private StringEsUtils() {
    }

    /**
     * 驼峰转下划线
     *
     * @param str {@link String}
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/20
     */
    public static String humpToLine(String str) {
        Matcher matcher = HUMP_PATTERN.matcher( str );
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement( sb, "_" + matcher.group( 0 ).toLowerCase() );
        }
        matcher.appendTail( sb );
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param str {@link String}
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/20
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher( str );
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement( sb, matcher.group( 1 ).toUpperCase() );
        }
        matcher.appendTail( sb );
        return sb.toString();
    }

}
