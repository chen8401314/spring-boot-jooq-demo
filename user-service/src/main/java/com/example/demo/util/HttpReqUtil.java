package com.example.demo.util;

import com.example.demo.common.Consts;
import com.example.demo.context.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * http请求获取token工具类
 *
 * @author 10302
 * @date 2017/8/2
 */
@Slf4j
@Component
public class HttpReqUtil {

    private HttpReqUtil() {
    }

    static List<String> domains;

    public static final String GET_METHOD = "GET";

    public static final String POST_METHOD = "POST";

    private static final int DEFAULT_CONNTIME = 5000;

    private static final int DEFAULT_READTIME = 5000;

    private static final String CHARATER_ENCODING = "UTF-8";

    static {
        String authCookieDomain = System.getenv("AUTH_COOKIE_DOMAIN");
        if (StringUtils.isEmpty(authCookieDomain)) {
            authCookieDomain = "localhost";
        }
        domains = Arrays.asList(authCookieDomain.split(Consts.COMMA));
    }

    /**
     * 设置带token的cookie
     *
     * @param jwt
     * @param domain
     * @param response
     */
    private static void setTokenCookie(String jwt, String domain, HttpServletResponse response) {
        Cookie cookie = new Cookie(ServiceContext.TOKEN_HEADER, jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(domain);
        response.setHeader(ServiceContext.TOKEN_HEADER, jwt);
        //设置编码格式为UTF-8
        response.setCharacterEncoding(CHARATER_ENCODING);
        //通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.addCookie(cookie);
    }

    public static void setTokenCookies(String jwt, HttpServletResponse response) {
        domains.forEach(domain -> setTokenCookie(jwt, domain, response));
    }


    /**
     * http请求
     *
     * @param method          请求方法GET/POST
     * @param path            请求路径
     * @param timeout         连接超时时间 默认为5000
     * @param readTimeout     读取超时时间 默认为5000
     * @param data            数据
     * @param requestProperty 请求头部信息
     * @return
     */
    public static String defaultConnection(String method, String path, int timeout, int readTimeout, String data, Map<String, String> requestProperty) throws IOException {
        String result = "";
        if (StringUtils.isBlank(path)) {
            return result;
        }
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        //设置外部传入的http头部设置
        if (requestProperty != null) {
            requestProperty.forEach(con::setRequestProperty);
        }
        con.setConnectTimeout(timeout == 0 ? DEFAULT_CONNTIME : timeout);
        con.setReadTimeout(readTimeout == 0 ? DEFAULT_READTIME : readTimeout);
        if (data != null && !"".equals(data)) {
            //设置post请求的链接信息
            if (POST_METHOD.equals(method)) {
                con.setDoOutput(true);
            }
            OutputStream output = con.getOutputStream();
            //输出文件编码
            output.write(data.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();
        }
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream input = con.getInputStream();
            result = inputStreamToStrFromByte(input, null);
            con.disconnect();
        }
        return result;
    }


    /**
     * 设置参数
     *
     * @param map     参数map
     * @param path    需要赋值的path
     * @param charset 编码格式 默认编码为utf-8(取消默认)
     * @return 已经赋值好的url 只需要访问即可
     */
    public static String setParams(Map<String, String> map, String path, String charset) throws MalformedURLException {
        String result = "";
        boolean hasParams = false;
        if (StringUtils.isBlank(path)) {
            return null;
        }
        if (map != null && map.size() > 0) {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, String>> params = map.entrySet();
            for (Map.Entry<String, String> entry : params) {
                String key = entry.getKey().trim();
                String value = entry.getValue().trim();
                if (hasParams) {
                    builder.append("&");
                } else {
                    hasParams = true;
                }
                if (StringUtils.isBlank(charset)) {
                    builder.append(key).append("=").append(value);
                } else {
                    builder.append(key).append("=").append(urlEncode(value, charset));
                }
            }
            result = builder.toString();
        }
        return doUrlPath(path, result).toString();
    }

    /**
     * 默认的https执行方法,返回
     *
     * @param method 请求的方法 POST/GET
     * @param path   请求path 路径
     * @param map    请求参数集合
     * @param data   输入的数据 允许为空
     * @return
     */
    public static String httpDefaultExecute(String method, String path, Map<String, String> map, String data) {
        String result = "";
        try {
            String url = setParams((TreeMap<String, String>) map, path, "");
            result = defaultConnection(method, url, DEFAULT_CONNTIME, DEFAULT_READTIME, data, null);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 设置连接参数
     *
     * @param path 路径
     * @return
     */
    private static URL doUrlPath(String path, String query) throws MalformedURLException {
        URL url = new URL(path);
        //path上的路径解析
        if (StringUtils.isEmpty(url.getQuery())) {
            if (path.endsWith("?")) {
                path += query;
            } else {
                path = path + "?" + query;
            }
        } else {
            if (path.endsWith("&")) {
                path += query;
            } else {
                path = path + "&" + query;
            }
        }
        return new URL(path);
    }


    /**
     * 编码
     *
     * @param source
     * @param encode
     * @return
     */
    public static String urlEncode(String source, String encode) {
        String result = source;
        try {
            result = URLEncoder.encode(source, encode);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 将输入流转换为字符串(通过byte数组)
     *
     * @param input   输入流
     * @param charset
     * @return
     */
    public static String inputStreamToStrFromByte(InputStream input, String charset) {
        String result = "";
        int len = 0;
        byte[] arr = new byte[1024];
        StringBuilder sb = new StringBuilder();
        if (input != null) {
            if (charset == null || "".equals(charset)) {
                charset = CHARATER_ENCODING;
            }
            try {
                while ((len = input.read(arr)) != -1) {
                    sb.append(new String(arr, 0, len, charset));
                }
                result = sb.toString();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }
}
