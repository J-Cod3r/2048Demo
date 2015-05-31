package com.jcod3r.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

@Controller
public class IndexController {

	private static final String extName = ".rar";

	@RequestMapping("/index.html")
	public String index(Model model, HttpServletRequest request)
			throws IOException {
		// 由于使用了Nginx，所以不能简单使用request.getRemoteAddr();获得客户端IP
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		String queryUrl = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
		Document doc = Jsoup.connect(queryUrl).get();
		JSONObject json = JSONObject.parseObject(
				doc.getElementsByTag("body").get(0).text()).getJSONObject(
				"data");
		model.addAttribute("country", json.getString("country"));
		model.addAttribute("city", json.getString("city"));
		return "index";
	}

	@RequestMapping("/download/{fileName}")
	public ResponseEntity<byte[]> downLoad(@PathVariable String fileName,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		File downloadFile = new File(this.getClass().getClassLoader()
				.getResource("../../source/2048.rar").getPath());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment",
				new String(fileName.getBytes("UTF-8"), "iso-8859-1") + extName);
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(downloadFile), headers,
				HttpStatus.CREATED);
	}

}
