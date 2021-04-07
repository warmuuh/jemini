package com.github.warmuuh.jemini.spring.autoconfigure;

import com.github.warmuuh.jemini.GeminiStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

public class GeminiSessionIdManager extends DefaultSessionIdManager {
	public GeminiSessionIdManager(Server server) {
		super(server);
	}

	@Override
	public String newSessionId(HttpServletRequest request, long created) {
		if (request.getRequestedSessionId() == null) {
			throw new ResponseStatusException(GeminiStatus.CLIENT_CERT_REQUIRED.getStatus(), "Client Certificate Needed", null);
		}
		return request.getRequestedSessionId();
	}
}
