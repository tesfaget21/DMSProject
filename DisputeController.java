package com.tz.productproject.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tz.productproject.Entity.Dispute;
import com.tz.productproject.service.BranchService;
import com.tz.productproject.service.DisputeReasonsService;
import com.tz.productproject.service.DisputeService;
import com.tz.productproject.service.DisputeTypeService;
import com.tz.productproject.service.DistrictService;
import com.tz.productproject.supportives.FileUploadUtil;

@Controller
public class DisputeController {
	@Autowired
	DistrictService districtService;
	
	@Autowired
	BranchService branchService;
	
	@Autowired
	DisputeTypeService disputeTypesService;
	
	@Autowired
	DisputeReasonsService reasonsTypesService;
	
	@Autowired
	DisputeService disputeService;
	
	int fetchedId;

	@RequestMapping("/dispute")
	public String dispute(ModelMap map) {
		map.addAttribute("districts", districtService.getDistrictService());
		map.addAttribute("branches", branchService.getAllBranchService());
		map.addAttribute("disputeTypes", disputeTypesService.getAllDisputeTypeService());
		map.addAttribute("disputeReasons", reasonsTypesService.getAllDisputeReasonsService());

		return "dispute";

	}

	@RequestMapping("/saveDispute")
	public String saveDispute(@ModelAttribute("dispute") Dispute dispute, ModelMap map,
			@RequestParam("filledFormImage") MultipartFile multipartFile) throws IOException {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

		dispute.setScannedFormImage(fileName);

		Dispute savedDispute = disputeService.saveDisputeService(dispute);
		String msg = "Sucessfully saved with id " + savedDispute.getDispute_Id();
		String uploadDir = "./Disputes_Filled_By_Customers/" + savedDispute.getDispute_Id();
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try {
			InputStream inputStream = multipartFile.getInputStream();
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException("Could not save uploaded file " + fileName);
		}

		map.addAttribute("msg", msg);
		System.out.println("Result: " + msg);
		return "dispute";
	}

	@RequestMapping("/disputeList")
	public String showListDisputes(ModelMap map) {
		map.addAttribute("disputes", disputeService.getAllDisputeService());
		return "disputeList";
	}

	@RequestMapping("/EditDisp")
	public String EditDispute(@RequestParam("id") int id, ModelMap map) {

		fetchedId = id;
		Dispute dispute = disputeService.getDisputeService(id);
		map.addAttribute("dispute", dispute);
		map.addAttribute("districts", districtService.getDistrictService());
		map.addAttribute("branches", branchService.getAllBranchService());
		map.addAttribute("disputeTypes", disputeTypesService.getAllDisputeTypeService());
		map.addAttribute("disputeReasons", reasonsTypesService.getAllDisputeReasonsService());

		return "disputeEdit";
	}

	@RequestMapping("/editDispute")
	public String updateDispute(Dispute disp, ModelMap map,
			@RequestParam("filledFormImage") MultipartFile multipartFile) throws IOException {
		String scannedFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		Dispute dispute = disputeService.getDisputeService(fetchedId);

		dispute.setDisputeType(disp.getDisputeType());
		dispute.setDisputeReeason(disp.getDisputeReeason());
		dispute.setAcquireBranchDistrictName(disp.getAcquireBranchDistrictName());
		dispute.setAcquireBranchName(disp.getAcquireBranchName());
		dispute.setIssuerBranchDistrictName(disp.getIssuerBranchDistrictName());
		dispute.setIssuerBank(disp.getIssuerBank());
		dispute.setCustomerAccountNumber(disp.getCustomerAccountNumber());
		dispute.setCustomerFullName(disp.getCustomerFullName());
		dispute.setCustomerCardNumber(disp.getCustomerCardNumber());
		dispute.setCustomerPhoneNumber(disp.getCustomerPhoneNumber());
		dispute.setTransactionAmount(disp.getTransactionAmount());
		dispute.setTrnasactionTimes(disp.getTrnasactionTimes());
		dispute.setTransactionDate(disp.getTransactionDate());
		dispute.setDescription(disp.getDescription());
		dispute.setScannedFormImage(scannedFileName);

		Dispute updateDisputeService = disputeService.updateDisputeService(dispute);
		map.addAttribute("diputes", disputeService.getAllDisputeService());
		String uploadDir = "./Disputes_Filled_By_Customers/" + updateDisputeService.getDispute_Id();
		
		FileUploadUtil.saveFile(uploadDir, scannedFileName, multipartFile);

		Path uploadPath = Paths.get(uploadDir);
		File f=uploadPath.toFile();
		if (f.exists()) {
		deleteDirectoryStream(uploadPath);
		}
		
		Files.createDirectories(uploadPath);

		try {
			InputStream inputStream = multipartFile.getInputStream();
			Path filePath = uploadPath.resolve(scannedFileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException("Could not save uploaded file " + scannedFileName);
		}
		return "dispute";
	}

	@RequestMapping("/deleteDispute")
	public String deleteDispute(@RequestParam("id") int id, ModelMap map) throws IOException  {

		/*
		 * String uploadDir = "./Disputes_Filled_By_Customers/" + id; Path uploadPath =
		 * Paths.get(uploadDir); File f=uploadPath.toFile();
		 * System.out.println(f.exists()); if(f.exists()) {
		 * deleteDirectoryStream(uploadPath); }
		 */
		 System.out.println("Step 1");
		  disputeService.deleteDisputeService(id);
		  System.out.println("Step 2");
		 map.addAttribute("disputes", disputeService.getAllDisputeService());
		  System.out.println("Step 3");
		  
			String uploadDir = "./Disputes_Filled_By_Customers/" + id;
			Path uploadPath = Paths.get(uploadDir);
			File f = uploadPath.toFile();
			System.out.println(f.exists());
			if (f.exists()) {
				deleteDirectoryStream(uploadPath);
			}
			  System.out.println("Step 4");
		return "disputeList";
	}

	@RequestMapping("/viewCustomerForm")
	public String viewCustomerForm(@RequestParam("id") int id, ModelMap map) {
		String uploadDir = "./Disputes_Filled_By_Customers/" + id;
		Path uploadPath = Paths.get(uploadDir);
		File f=uploadPath.toFile();
	if(f.exists()) {
		Dispute dispute = disputeService.getDisputeService(id);
		// map.addAttribute("disputeId", dispute.getDispute_id());
		map.addAttribute("scannedFilledForm", dispute.getScannedFiledFormPath());
	}
	

		return "customersFilledForm";
	}

	public void deleteDirectoryStream(Path path) throws IOException {
		
			Files.walk(path).sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
			
	}

	

}
