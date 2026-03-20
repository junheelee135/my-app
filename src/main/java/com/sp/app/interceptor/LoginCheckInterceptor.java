package com.sp.app.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sp.app.domain.dto.SessionInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/*
  - HandlerInterceptor 인터페이스
    : 컨트롤러가 요청 전과 후에 반복적인 기능을 수행할 수 있도록 하기 위한 인터페이스
    : 로그인 검사, 응답시간 계산, 이벤트기간 만료등에서 이용 가능
*/
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
	/*
	   - 클라이언트 요청이 컨트롤러에 도착하기 전에 호출
	   - false 리턴하면 다른 HandlerInterceptor 또는 컨트롤러를 실행하지 않고
	     요청 종료
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		boolean result = true;

		try {
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			String cp = req.getContextPath();
			String uri = req.getRequestURI();
			String queryString = req.getQueryString();

			if (info == null) {
				result = false;

				if (isAjaxRequest(req)) {
					resp.sendError(401);
				} else {
					if (uri.indexOf(cp) == 0) {
						uri = uri.substring(cp.length());
					}
					
					if (queryString != null) {
						uri += "?" + queryString;
					}

					session.setAttribute("preLoginURI", uri);
					resp.sendRedirect(cp + "/member/login");
				}
			} else {
				if(uri.indexOf("admin") != -1 && info.getUserLevel() < 51) {
					result = false;
					resp.sendRedirect(cp + "/member/noAuthorized");
				}
			}
		} catch (Exception e) {
			log.info("pre: " + e.toString());
		}

		return result;
	}

	/*
		- 컨트롤러가 요청을 처리한 후에 호출
		- 컨트롤러 실행 중 예외가 발생하면 실행하지 않는다.
	 */
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object handler, ModelAndView modelAndView)
			throws Exception {
	}

	/*
		- 컨트롤러가 요청을 처리한 후 뷰를 통해 클라이언트에게 응답을 전송한 후에 실행
		- 컨트롤러 처리 중 또는 뷰를 생성하는 과정에서 예외가 발생해도 실행
	 */
	@Override
	public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex)
			throws Exception {
	}

	/*
	   - AJAX 요청인지를 확인하기 위한 메소드
	   - ajax 요청을 할때 로그인이 필요한 경우 헤더에 AJAX:true 로 전송하도록 구현
	*/
	private boolean isAjaxRequest(HttpServletRequest req) {
		String header = req.getHeader("AJAX");
		return header != null && header.equals("true");
	}
}
