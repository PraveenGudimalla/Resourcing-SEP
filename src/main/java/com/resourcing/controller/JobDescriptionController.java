/**
 * @author Nikhil Gundla
 * @author Praveeen Gudimalla
 *
 */
package com.resourcing.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resourcing.beans.Candidate;
import com.resourcing.beans.Client;
import com.resourcing.beans.JobDescription;
import com.resourcing.service.CandidateService;
import com.resourcing.service.ClientService;
import com.resourcing.service.JobDescriptionService;

@Controller
@RequestMapping("/job")
public class JobDescriptionController {

	static Logger LOGGER = Logger.getLogger(ClientController.class);

	@Autowired
	JobDescriptionService jobDescriptionService;

	@Autowired
	private CandidateService candidateService;

	@Autowired
	ClientService clientService;

	@GetMapping("/InactivateJd/{id}")
	public String inactivateJd(Model model, @PathVariable("id") int jdId, JobDescription objJobDescription,
			HttpSession session) {
		LOGGER.debug("inside inactivateJd id:::" + jdId);
		JobDescription jd = jobDescriptionService.getJobDescriptionById(jdId);
		LOGGER.debug("inside inactivateJd id:::" + jdId);
		if (jd.getIsActive() == 'N') {
			jd.setIsActive('Y');
			jobDescriptionService.updateJobDescription(jd);
		} else if (jd.getIsActive() == 'Y') {
			jd.setIsActive('N');
			jobDescriptionService.updateJobDescription(jd);
		}
		List<JobDescription> jdList = jobDescriptionService.findAllJobDescriptionByClient(jd.getClient());
		Comparator<JobDescription> isActive = Comparator.comparing(JobDescription::getIsActive);
		List<JobDescription> sortedList = jdList.stream().sorted(isActive.reversed()).collect(Collectors.toList());
		Client client = clientService.getClientById(jd.getClient().getClientId());
		model.addAttribute("clientDetails", client);
		model.addAttribute("editClientDetails", client);
		model.addAttribute("objJobDescription", sortedList);
		model.addAttribute("TableName", jdList.size()+" Jobs Posted By " + client.getClientName().toUpperCase() + " from " + client.getClientCompany().toUpperCase());
		return "client_dashboard_jobList";
	}


	// To Get the JobDetails for an Logged-out Person
	@GetMapping("/jdListForUnknown")
	public String getJdListForUnknown(Model model,JobDescription objJobDescription,RedirectAttributes redirectAttributes,
			@RequestParam(required = false) String message) {
		List<JobDescription> list = jobDescriptionService.getAllJobDescriptions();
		List<JobDescription> filter = list.stream().filter(obj -> obj.getSkills().contains(objJobDescription.getSkills().toUpperCase().replaceAll("\s+", "")) && obj.getExp() <= objJobDescription.getExp()).collect(Collectors.toList());
		LOGGER.debug("count:::"+filter.size());
		if(!StringUtils.isEmpty(message)) {
			model.addAttribute("message", message);
		}
		if (filter.size() != 0) {
			LOGGER.debug("size::::"+ objJobDescription.getExp());
			System.out.println("if condition:::");
			model.addAttribute("jdList", filter);
			return "candidate_jobs_list_layout_full_page_map";
		} else {
//			LOGGER.debug("else condition:::::::");
//			List<JobDescription> jobs = jobDescriptionService.getAllJobDescriptions();
//			List<Candidate> candidateList = candidateService.getAllCandidates();
//			List<Client> clientList = clientService.getAllClients();
//			model.addAttribute("jdList", jobs.size());
//			model.addAttribute("candidateList", candidateList.size());
//			model.addAttribute("clientList", clientList.size());
//			model.addAttribute("objJd", objJobDescription);
			redirectAttributes.addFlashAttribute("message", "No jobs found for skills " + objJobDescription.getSkills() + " with " + objJobDescription.getExp() +" of Experience");
			return "redirect:/resourcing";
		}
	}

	@PostMapping("/updatedJdList")
	public String getJdList(Model model, @Validated @ModelAttribute("objJd") JobDescription objJobDescription) {
		List<JobDescription> jdList = jobDescriptionService.getAllJobDescriptions();
		LOGGER.debug("entered into getJdListForUnknown::::" + objJobDescription.getLocation());
		LOGGER.debug("entered into getJdListForUnknown::::" + objJobDescription.getPosition());
		model.addAttribute("jdList", jdList);
		return "jobs-grid-layout-full-page";
	}
	
	@GetMapping("/DetailedJd/{id}")
	public String getDetailedJd(Model model, @PathVariable("id") int jdId, JobDescription objJobDescription) {
		JobDescription jd = jobDescriptionService.getJobDescriptionById(jdId);
		LOGGER.debug("entered into getHomePage::::Detailed JdPage");
		model.addAttribute("objJd", jd);
		return "candidate_single_job_page";
	}

}
