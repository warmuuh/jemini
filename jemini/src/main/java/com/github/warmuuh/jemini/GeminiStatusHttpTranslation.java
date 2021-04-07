package com.github.warmuuh.jemini;

import org.eclipse.jetty.http.HttpStatus;

public class GeminiStatusHttpTranslation {

	public static GeminiStatus mapFromHttp(int httpStatus) {
		if (httpStatus < 100){
			return GeminiStatus.tryFromStatus(httpStatus);
		}

		if (httpStatus >= 200 && httpStatus < 299) {
			return GeminiStatus.SUCCESS;
		}
		if (httpStatus >= 300 && httpStatus < 399){
			switch (httpStatus) {
				case HttpStatus.FOUND_302:
				case HttpStatus.TEMPORARY_REDIRECT_307:
					return GeminiStatus.REDIRECT_TEMPORARY;
				case HttpStatus.MOVED_PERMANENTLY_301:
				case HttpStatus.PERMANENT_REDIRECT_308:
					return GeminiStatus.REDIRECT_PERMANENT;
				default:
					return GeminiStatus.REDIRECT_TEMPORARY;
			}
		}
		if (httpStatus >= 400 && httpStatus < 499) {
			switch (httpStatus) {
				case HttpStatus.TOO_MANY_REQUESTS_429:
					return GeminiStatus.SLOW_DOWN;
				case HttpStatus.NOT_FOUND_404:
					return GeminiStatus.NOT_FOUND;
				case HttpStatus.GONE_410:
					return GeminiStatus.GONE;
				case HttpStatus.BAD_REQUEST_400:
					return GeminiStatus.BAD_REQUEST;
				default:
					return GeminiStatus.TEMPORARY_FAILURE;
			}
		}

		if (httpStatus >= 500 && httpStatus < 599) {
			switch (httpStatus) {
				case HttpStatus.BAD_GATEWAY_502:
				case HttpStatus.GATEWAY_TIMEOUT_504:
					return GeminiStatus.PROXY_ERROR;
				case HttpStatus.SERVICE_UNAVAILABLE_503:
					return GeminiStatus.SERVER_UNAVAILABLE;
				default:
					return GeminiStatus.PERMANENT_FAILURE;
			}
		}

		return GeminiStatus.SUCCESS;
	}

}
