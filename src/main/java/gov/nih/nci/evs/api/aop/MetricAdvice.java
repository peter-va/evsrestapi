package gov.nih.nci.evs.api.aop;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nih.nci.evs.api.model.evs.Metric;
import gov.nih.nci.evs.api.support.FilterCriteriaElasticFields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Aspect
public class MetricAdvice {

	/** The logger. */
	private static final Logger log = LoggerFactory.getLogger(MetricAdvice.class);

	private static final String EVSRESTAPI_APPLICATION = "evsrestapi";

	@Around("execution(* gov.nih.nci.evs.api.controller.*.*(..)) && @annotation(recordMetricDB)")
	private Object recordMetricDB(ProceedingJoinPoint pjp, RecordMetricDB recordMetricDB) throws Throwable {
		log.debug("log method having db as parameter");

		// get the request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String filterParams = request.getParameter("db");

		return recordMetric(pjp, request, filterParams);

	}

	@Around("execution(* gov.nih.nci.evs.api.controller.*.*(..)) && @annotation(recordMetricDBFormat)")
	private Object recordMetricDBFormat(ProceedingJoinPoint pjp, RecordMetricDBFormat recordMetricDBFormat)
			throws Throwable {
		log.debug("log method having db and format as parameter");

		// get the request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String filterParams = "{\"db\":\"" + request.getParameter("db") + "\",\"fmt\":\"" + request.getParameter("fmt")
				+ "\"}";

		return recordMetric(pjp, request, filterParams);

	}

	@Around("execution(* gov.nih.nci.evs.api.controller.*.*(..)) && args(filterCriteriaElasticFields,..) && @annotation(recordMetricSearch)")
	private Object recordMetricForSearch(ProceedingJoinPoint pjp,
			FilterCriteriaElasticFields filterCriteriaElasticFields, RecordMetricSearch recordMetricSearch)
			throws Throwable {
		log.debug("log search");
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		// get the parameters
		ObjectMapper mapper = new ObjectMapper();
		String filterParams = mapper.writeValueAsString(filterCriteriaElasticFields);
		log.debug("params - " + filterParams);

		return recordMetric(pjp, request, filterParams);

	}

	public Object recordMetric(ProceedingJoinPoint pjp, HttpServletRequest request, String params) throws Throwable {

		// get the start time
		long startTime = System.currentTimeMillis();
		Date startDate = new Date();
		Object retVal = pjp.proceed();
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		Date endDate = new Date();
		log.debug("durtaion = " + String.valueOf(duration));

		// get the ip address of the remote user
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String userIpAddress = attr.getRequest().getRemoteAddr();
		log.debug("userIpAddress" + userIpAddress);

		String applicationName = EVSRESTAPI_APPLICATION;
		log.debug("applicationName - " + applicationName);
		Metric metric = new Metric();
		metric.setApplicationName(applicationName);
		metric.setEndPoint(request.getRequestURL().toString());
		log.debug("url -" + request.getRequestURL().toString());
		metric.setQueryParams(params);
		log.debug("params - " + params);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		metric.setStartTime(dateFormat.format(startDate));
		metric.setEndTime(dateFormat.format(endDate));
		metric.setUsername("anonymousUser");
		log.debug("username -" + metric.getUsername());
		metric.setDuration(duration);
		metric.setRemoteIpAddress(userIpAddress);

		// get the parameters
		ObjectMapper mapper = new ObjectMapper();
		String metricStr = mapper.writeValueAsString(metric);
		log.debug("metric -" + metricStr);

		return retVal;
	}

}
