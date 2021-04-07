package com.github.warmuuh.jemini;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Getter
public enum GeminiStatus {

	INPUT(10),
	SENSITIVE_INPUT(11),
	SUCCESS(20),
	REDIRECT_TEMPORARY(30),
	REDIRECT_PERMANENT(31),
	TEMPORARY_FAILURE(40),
	SERVER_UNAVAILABLE(41),
	CGI_ERROR(42),
	PROXY_ERROR(43),
	SLOW_DOWN(44),
	PERMANENT_FAILURE(50),
	NOT_FOUND(51),
	GONE(52),
	PROXY_REQUEST_REFUSED(53),
	BAD_REQUEST(59),
	CLIENT_CERT_REQUIRED(60),
	CERT_NOT_AUTHORIZED(61),
	CERT_NOT_VALID(62)
	;

	private final int status;

	public boolean is1xInput() {
		return 10 <= status && status < 20;
	}


	public boolean is2xSuccess() {
		return 20 <= status && status < 30;
	}


	public boolean is3xRedirect() {
		return 30 <= status && status < 40;
	}


	public boolean is4xTemporaryFailure() {
		return 40 <= status && status < 50;
	}


	public boolean is5xPermamentFailure() {
		return 50 <= status && status < 60;
	}


	public boolean is6xClientCertReq() {
		return 60 <= status && status < 70;
	}

	public static GeminiStatus tryFromStatus(int status){
		for (GeminiStatus value : GeminiStatus.values()) {
			if (value.getStatus() == status){
				return value;
			}
		}
		return null;
	}

}
