package aiwa.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
	@Override public void init(FilterConfig config) { }
	@Override public void destroy() { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String contextPath = httpRequest.getContextPath();
		String path = httpRequest.getRequestURI().substring(contextPath.length());
		if (isPublic(path)) { chain.doFilter(request, response); return; }

		HttpSession session = httpRequest.getSession(false);
		if (session != null && session.getAttribute("userId") instanceof Number) {
			chain.doFilter(request, response);
			return;
		}
		String next = path;
		if (httpRequest.getQueryString() != null) next += "?" + httpRequest.getQueryString();
		String loginUrl = contextPath + "/LoginController?next=" + URLEncoder.encode(next, StandardCharsets.UTF_8);
		httpResponse.sendRedirect(httpResponse.encodeRedirectURL(loginUrl));
	}

	private boolean isPublic(String path) {
		return path.equals("/") || path.equals("/index.jsp") || path.equals("/StudyController") || path.equals("/home")
				|| path.equals("/LoginController") || path.equals("/RegisterController")
				|| path.startsWith("/assets/") || path.equals("/favicon.ico");
	}
}
