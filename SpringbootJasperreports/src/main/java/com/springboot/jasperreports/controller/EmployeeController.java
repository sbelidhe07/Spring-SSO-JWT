package com.springboot.jasperreports.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.jasperreports.model.Employee;
import com.springboot.jasperreports.service.EmployeeService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.servlet.http.HttpServletResponse;

import com.springboot.jasperreports.CookieUtil;

@Controller
public class EmployeeController {

	final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String jwtTokenCookieName = "JWT-TOKEN";

	// @Autowired annotation provides the automatic dependency injection.
	@Autowired
	EmployeeService eservice;

  @RequestMapping("/")
    public String home() {
        return "redirect:/welcome";
    }

   @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
	

	// Method to create the pdf report via jasper framework.
	@RequestMapping(value = "/view")
	public String view () {
		log.info("Preparing the pdf report via jasper.");
		try {
			createPdfReport(eservice.findAll());
			log.info("File successfully saved at the given path.");
		} catch (final Exception e) {
			log.error("Some error has occured while preparing the employee pdf report.");
			e.printStackTrace();
		}
		// Returning the view name as the index page for ease.
	  return "welcome";
	}

	// Method to create the pdf file using the employee list datasource.
	private void createPdfReport(final List<Employee> employees) throws JRException {
		// Fetching the .jrxml file from the resources folder.
		final InputStream stream = this.getClass().getResourceAsStream("/report.jrxml");

		// Compile the Jasper report from .jrxml to .japser
		final JasperReport report = JasperCompileManager.compileReport(stream);

		// Fetching the employees from the data source.
		final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(employees);

		// Adding the additional parameters to the pdf.
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", "javacodegeek.com");

		// Filling the report with the employee data and additional parameters information.
		final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);

		// Users can change as per their project requrirements or can take it as request input requirement.
		// For simplicity, this tutorial will automatically place the file under the "c:" drive.
		// If users want to download the pdf file on the browser, then they need to use the "Content-Disposition" technique.
		final String filePath = "/Users/srinivas/";
		// Export the report to a PDF file.
		JasperExportManager.exportReportToPdfFile(print, filePath + "Employee_report.pdf");
	}

	@RequestMapping("/logout")
    public String logout(HttpServletResponse httpServletResponse) {
        CookieUtil.clear(httpServletResponse, jwtTokenCookieName);
        return "redirect:/";
    }
}
