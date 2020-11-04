package com.tgfcodes.beer.controller.handler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TypeErrorController implements ErrorController {

	@Override
	public String getErrorPath() {
		return "redirect:/error";
	}

	@GetMapping(value = "/error")
	public ModelAndView handleError(HttpServletRequest httpServletRequest) {
		Object status = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				return new ModelAndView("/erro/404");
			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				return new ModelAndView("/erro/403");
			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return new ModelAndView("/erro/500");
			}
		}
		return null;
	}

}
