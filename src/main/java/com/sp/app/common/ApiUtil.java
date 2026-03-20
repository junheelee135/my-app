package com.sp.app.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiUtil {
	/**
	 * 공공 API 등의 XML, JSON 데이터를 받아 String 으로 반환하는 메소드
	 * @param spec		API를 제공하는 url
	 * @return
	 * @throws Exception
	 */
	public String fetchData(String spec) throws Exception {
		String result = null;
		
		BufferedReader nbr = null;
		try {
			/*
			HttpURLConnection conn = null;
			conn = (HttpURLConnection)new URL(spec).openConnection(); // JDK 20 deprecated
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = br.readLine()) != null) {
				sb.append(s);
			}
			result = sb.toString();
			
			conn.disconnect();
			*/
			
			URL url = URI.create(spec).toURL();
			nbr = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = nbr.readLine()) != null) {
				sb.append(s);
			}
			
			result = sb.toString();
		} catch (Exception e) {
			log.info("receiveToString : ", e);
			throw e;
		} finally {
			if(nbr != null) {
				try {
					nbr.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}
}
