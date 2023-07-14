package com.small.group.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@RequestMapping("/image")
@Controller
public class ImageController {
	
	@PostMapping("/saveImage")
	public String saveImage(@ModelAttribute ImageItem imageItem, HttpSession session) {
		String base64Data = imageItem.getImageData();
		String fileName = imageItem.getFileName();
		Integer groupNo = imageItem.getGroupNo();
		String[] allowedExtension = {"jpg", "jpeg", "png", "gif"};
		try {
			String base64Image = base64Data.split(",")[1];
			byte[] imageBytes = Base64Utils.decodeFromString(base64Image);
			/*
			 * imagePath의 마지막 .위치까지만 복사해서 해당 문자열로 순회하여
			 * 이미지가 존재하면 삭제하기 그리고 아래 코드를 실행해서 다시 저장
			 */
			String imagePath = "src/main/resources/static/image/groupMain/" + fileName;
			// 경로에 기존 이미지 파일이 존재하면 삭제
			String deleteFilePath = imagePath.substring(0, imagePath.lastIndexOf(".") + 1);
			for(String extension : allowedExtension) {
				File file = new File(deleteFilePath + extension);
				if(file.isFile()) {
					System.out.println("파일이 해당 이름으로 존재합니다.");
					System.out.println(file.getCanonicalPath());
					file.delete();
					System.out.println("파일이 삭제되었습니다.");
				}
			}
			
			// 이미지를 경로에 저장
			FileOutputStream outputStream = new FileOutputStream(imagePath);
			outputStream.write(imageBytes);
			outputStream.close();
			System.out.println("이미지가 경로에 저장되었습니다.");
			return "redirect:/group/groupMain?groupNo=" + groupNo;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("이미지 저장에 실패하였습니다.");
			return "fail";
		} 
		
		
	}
	
	
	// 파일의 존재여부에 따른 결과 값을 반환하는 함수
	@PostMapping("/fileExists")
	@ResponseBody
	public String fileExists(@RequestBody String filePath) {
		String[] allowedExtension = {"jpg", "jpeg", "png", "gif"};
		/*
		 *  해당 확장자들을 전부 순회하며 경로에 파일명으로 된 이미지가 있는지
		 *  검색한 후 일치하는 파일명을 결과 값으로 반환함.
		 */
		for(String extension : allowedExtension) {
			File file = new File(filePath + extension);
			if(file.isFile()) {
				
				// 파일명 부분만 가져오기
				int indexOf = filePath.lastIndexOf("/") + 1;
				String fileName = filePath.substring(indexOf, filePath.length());
				return fileName + extension;
			}
		}
		
		// 일치하지 않으면 notFound 반환
		return "notFound";
	}
	
	
	@GetMapping("/loadImage/groupMain")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@RequestParam Integer groupNo, HttpSession session, Model model) {
		String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
		String fileName = "main_image_group_" + groupNo + ".";
		String path = session.getServletContext().getRealPath("/");
		
		Integer indexOfMain = path.indexOf("\\src\\main") + "\\src\\main".length();
		String realPath = path.substring(0, indexOfMain).replace("\\", "/") + "/resources/static/image/groupMain/";
		String fileAbPath = realPath + fileName;
		
		File imageFile = new File(fileAbPath);
		MediaType mediaType = null;
		for(String extension : allowedExtensions) {
			imageFile = new File(fileAbPath + extension);
			if(imageFile.isFile()) {
				System.out.println("해당 파일이 검색되었습니다.");
				System.out.println("filePath: " + fileAbPath + extension);
				
				if(extension.equals("jpg") || extension.equals("jpeg")) {
					mediaType = MediaType.IMAGE_JPEG;
				} else if(extension.equals("gif")) {
					mediaType = MediaType.IMAGE_GIF;
				} else if(extension.equals("png")) {
					mediaType = MediaType.IMAGE_PNG;
				}
				break;
			}
		}
		// 파일이 존재하지 않을 경우 NOT FOUND 반환
		if(!imageFile.exists()) {
			System.out.println("파일이 검색되지 않았습니다.");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		// 여기서부터는 파일이 존재할 경우에 실행되는 코드
		try(FileInputStream fis = new FileInputStream(imageFile)) {
			// 헤더의 정의
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(mediaType);
			
			// 데이터의 변환
			byte[] rawData = IOUtils.toByteArray(fis);
			return new ResponseEntity<>(rawData, headers, HttpStatus.OK);
			
		} catch(Exception e) {
			System.err.println("파일 입출력 스트림 에러가 발생하였습니다.");
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}

@Data
class ImageItem {
	private String imageData;
	private String fileName;
	private Integer groupNo;
}
