package com.small.group.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.http.MediaType;

import org.apache.commons.io.IOUtils;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.small.group.dto.GroupDTO;
import com.small.group.dto.PhotoCommentDTO;
import com.small.group.dto.PhotoDTO;
import com.small.group.entity.Photo;
import com.small.group.entity.PhotoComment;
import com.small.group.entity.User;
import com.small.group.service.GroupMemberService;
import com.small.group.service.GroupService;
import com.small.group.service.PhotoCommentService;
import com.small.group.service.PhotoService;
import com.small.group.service.UserService;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.privilege.GetMethodAction;
import net.coobird.thumbnailator.Thumbnailator;


@RequestMapping("/photo")
@Controller
@RequiredArgsConstructor
@Slf4j
public class PhotoController {
	
	/*
	 * @Value
	 *  - application.properties에 설정한 값 읽어오기
	 *  - ${com.javalab.upload.path}
	 *    - com.javalab.upload.path=C://filetest
	 */
    private String uploadPath;
	
	private final GroupService groupService;
	private final UserService userService;
	private final PhotoService photoService;
	private final PhotoCommentService photoCommentService;
	private final GroupMemberService groupMemberService;
	
	/*
     *	[ 사진첩 관련 컨트롤러 맵핑 ]
     *	작성자 : 유성태 - 07/10 
     */
	
	/*
	 * 그룹 사진첩 앨범 이동
	 */
//	@GetMapping("/photoList")
//	public String getPhotoList(Model model,
//							  @RequestParam("groupNo") Integer groupNo,
//							  @ModelAttribute("photoDTO") PhotoDTO photoDTO,
//							  HttpSession session) {
//		
//		User user = (User) session.getAttribute("user");
//	    if (user == null) {
//	    	return "redirect:/user/login";
//	    }
//		
//	    // 그룹 번호에 해당하는 사진 목록 가져오기
//	    List<PhotoDTO> photoList = photoService.getPhotoListByGroupNo(groupNo);
//	    photoList.sort(Comparator.comparing(PhotoDTO::getPhotoNo).reversed());	//역순(최신순)으로
//	    
//	    GroupDTO groupDto = groupService.readGroup(groupNo);
//	    
//		// 로그인 된 회원 정보가 모임의 회원 정보가 같을 경우에 'upload' 버튼의 유무를 전달함.
//		Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);
//		if (isMemberIncludedGroup > 0) {
//			model.addAttribute("uploadButton", true);
//		}else {
//			model.addAttribute("uploadButton", false);
//		}
//		
//		model.addAttribute("group", groupDto);
//	    model.addAttribute("photoList", photoList);
//	    model.addAttribute("groupNo", groupNo); // 그룹 번호도 모델에 추가
//	    
//	    // 확인을 위해 모델에 추가한 사진 목록을 로그로 출력
//	    for (PhotoDTO photo : photoList) {
//	        System.out.println(photo);
//	    }
//	    
//	    return "photo/photoList";
//	}

	/*
     * 사진 업로드 페이지로 이동
     */
    @GetMapping("/uploadPhoto/{groupNo}")
    public String uploadPhoto(Model model,
                              @PathVariable("groupNo") Integer groupNo,
                              HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        GroupDTO groupDto = groupService.readGroup(groupNo);

        // 현재 로그인한 사용자가 그룹의 회원인지 확인
        Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);
        if (isMemberIncludedGroup > 0) {
            model.addAttribute("uploadButton", true);
        } else {
            model.addAttribute("uploadButton", false);
        }
        model.addAttribute("group", groupDto);
        model.addAttribute("groupNo", groupNo);

        return "photo/uploadPhoto";
    }

    /*
     *  Ajax로 요청된 파일 업로드 처리 메소드
     * 반환타입 : ResponseEntity<List<UploadResultDTO>>
     *  - 첨부파일 갯수 만큼 처리 결과가 섬네일 이미지까지 포함해서 만들어짐.
     */
    @PostMapping("/uploadPhoto/{groupNo}")
    public ResponseEntity<List<PhotoDTO>> uploadPhoto(@ModelAttribute("photoDTO") @Validated PhotoDTO photoDTO,
    												  @PathVariable Integer groupNo,
    												  @PathVariable Integer userNo,
						                              BindingResult bindingResult, Model model,
						                              HttpServletRequest request,
						                              HttpSession session,
						                              @RequestParam("photoFiles") MultipartFile[] uploadFiles) {
	    
    	// 업로드 결과를 담을 리스트
    	List<PhotoDTO> photoDTOList = new ArrayList<>();
    	
    	User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(photoDTOList);
        }
        
        GroupDTO groupDto = groupService.readGroup(groupNo);
        
        // 현재 로그인한 사용자가 그룹의 회원인지 확인
        Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);
        if (isMemberIncludedGroup <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(photoDTOList);
        }
    	
    	System.out.println("@@@@@photoDTOList" + photoDTOList);
    	
    	
    	// 유효성 검사 오류가 있는 경우
    	if (bindingResult.hasErrors()) {
	    	// 오류 정보 로깅 - Log field errors 
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			
			for (FieldError error : fieldErrors) {
				log.error(error.getField() + " " + error.getDefaultMessage());
			}
			
			// 오류가 있을 경우 업로드 페이지로 다시 이동  07-12 12:02 다시 원복
//	        return new ResponseEntity<>(photoDTOList, HttpStatus.OK);
			
			// 07-12 13:22 오류가 있을 경우 업로드 페이지로 다시 이동
//			return ResponseEntity.badRequest().body(photoDTOList);
			// 07-12 16:40 수정 오류가 있을 경우 업로드 페이지로 다시 이동 
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(photoDTOList);
	    }
		model.addAttribute("photo", photoDTO);
		model.addAttribute("groupNo", photoDTO.getGroupNo());
		log.info("@@@Post: uploadPhoto");
    	
	    /*
         * 첨부 파일 갯수만큼 반복
         */
        for (MultipartFile uploadFile: uploadFiles) {
        	// 첨부파일이 이미지 인지 체크
            if(uploadFile.getContentType().startsWith("image") == false) {
                log.warn("이미지 파일만 첨부할 수 있습니다.");
//                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                //07-12 16:40 수정 오류가 있을 경우 업로드 페이지로 다시 이동
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(photoDTOList);
            }
	    
		    //실제 파일 이름만 추출하기 위해서 경로 맨뒤의 "/" 뒤의 문자열 추출
	        String originalName = uploadFile.getOriginalFilename();
	        String fileName = originalName.substring(originalName.lastIndexOf("//") + 1);
	
	        log.info("fileName: " + fileName);
	        System.out.println("@@@@fileName: " + fileName);
	        
	        //날짜 폴더 생성, 파일을 년/월/일 폴더에 나눠서 보관하게 됨.
	        String folderPath = makeFolder();
	        log.info("folderPath: " + folderPath);
	        System.out.println("@@@@folderPath: " + folderPath);
	        
	        /*
	         * UUID : 중복되지 않는 번호 생성 
	         *  - 중복되지 않는 고유한 식별자(UUID)를 생성합니다. 이것은 파일 이름에 추가되어, 
	         *    같은 이름의 파일을 업로드할 때 파일이 덮어씌워지는 것을 방지함.
	         */
	        String uuid = UUID.randomUUID().toString();
	        System.out.println("@@@@uuid : " + uuid);
		    
	        /*
	         * savePath
	         *  - 저장될 파일의 전체 경로(폴더)와 파일명
	         *  - File.separator : 윈도우즈("/"), 리눅스,맥("/") 
	         *  - UUID와 저장할 파일 이름 중간에 "_"를 이용해서 구분
	         *  - 절대 중복되지 않는 파일 이름이 만들어짐.
	         */
	        String saveName = uploadPath + File.separator + folderPath + File.separator + uuid +"_" + fileName;
	        Path savePath = Paths.get(saveName);
	        
	        System.out.println("@@@@savePath" + savePath);
	
	        try {
	            //위에서 만들어진 경로+파일명으로 서버에 업로드(원본 파일 저장)
	            uploadFile.transferTo(savePath);
	
	            //섬네일 파일명 생성(중간에 s_로 시작하도록)
	            String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator
	                    +"s_" + uuid +"_" + fileName;
	            
	            System.out.println("@@@@thumbnailSaveName" + thumbnailSaveName);
	            
	            //섬네일 파일 객체 생성
	            File thumbnailFile = new File(thumbnailSaveName);
	            
	            System.out.println("@@@@thumbnailFile" + thumbnailFile);
	            
	            //섬네일 생성 - 원본 파일과 섬네일 파일 객체를 입력으로 받아, 지정된 크기의 섬네일을 생성
	            Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile,100,100 );
	            
	            /*
	             * 결과 정보 추가: PhotoDTO 객체를 생성하여 파일 이름, UUID,
	             * 폴더 경로를 저장하고, 이를 결과 목록에 추가.
	             * 이걸 화면으로 보내서 섬네일로 보여줘야 함.
	             */
//	            PhotoDTO photoData = new PhotoDTO(fileName, uuid, folderPath);
//	            photoDTOList.add(photoData);
	         // 데이터베이스에 사진 정보 저장
//	            photoService.insertPhoto(photoData);
	            userNo = photoDTO.getUserNo();
	            
	            PhotoDTO photoData = new PhotoDTO();
	            photoData.setFileName(fileName); 	 // 파일 이름 설정
	            photoData.setUuid(uuid); 			 // UUID 설정
	            photoData.setFolderPath(folderPath); // 폴더 경로 설정
	            photoData.setGroupNo(groupNo);		 // 그룹 번호 설정
	            photoData.setUserNo(userNo);		 //사용자 번호 설정

	            System.out.println("@@@@photoData" + photoData);
	            // 데이터베이스에 사진 정보 저장
	            photoService.insertPhoto(photoData);
	            photoDTOList.add(photoData);
	
	        } catch (IOException e) {
	        	// 파일 업로드 중에 오류가 발생한 경우  16:51
	            log.error("Error uploading photo: " + e.getMessage());
	            return ResponseEntity.badRequest().body(photoDTOList);
	        }

        }//end for
//    return new ResponseEntity<>(photoDTOList, HttpStatus.OK);
     
        // 업로드된 사진 정보를 응답으로 반환 16:51
        return ResponseEntity.ok(photoDTOList);
    }// end uploadPhoto
    
	/**
	 * 파일 저장 경로를 년/월/일 단위로 만들어주는 메소드
	 */
	private String makeFolder() {
	
	    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	
	    String folderPath =  str.replace("//", File.separator);
	
	    // make folder --------
	    File uploadPathFolder = new File(uploadPath, folderPath);
	
	    if (uploadPathFolder.exists() == false) {
	        uploadPathFolder.mkdirs();
	    }
	    return folderPath;
	}//end makeFolder
	
	@GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName) {

        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName =  URLDecoder.decode(fileName,"UTF-8");

            log.info("fileName: " + srcFileName);

            File file = new File(uploadPath +File.separator+ srcFileName);

            log.info("file: " + file);

            HttpHeaders header = new HttpHeaders();

            //MIME타입 처리
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            //파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    
    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> removeFile(String fileName){

        String srcFileName = null;
        try {
            srcFileName = URLDecoder.decode(fileName,"UTF-8");
            File file = new File(uploadPath +File.separator+ srcFileName);
            boolean result = file.delete();

            File thumbnail = new File(file.getParent(), "s_" + file.getName());

            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
        
        

    /*
     * 사진 상세 페이지로 이동
     */
    @GetMapping("/photoRead/{photoNo}")
    public String readPhoto(@PathVariable("photoNo") Integer photoNo, Model model) {
        // 사진 번호에 해당하는 사진 정보 가져오기
        PhotoDTO photoDTO = photoService.readPhoto(photoNo);
        model.addAttribute("photoDTO", photoDTO);
        
        // 사진 번호에 해당하는 댓글 목록 가져오기
        List<PhotoCommentDTO> commentList = photoCommentService.getPhotoCommentList(photoNo);
        model.addAttribute("commentList", commentList);
        
        // 댓글 입력을 위한 PhotoCommentDTO 객체 생성
        PhotoCommentDTO photoCommentDTO = new PhotoCommentDTO();
        photoCommentDTO.setPhotoCommentNo(photoNo);
        model.addAttribute("photoCommentDTO", photoCommentDTO);
        
        return "photo/photoRead";
    }
    
    /*
     * 사진 삭제 처리
     */
    @GetMapping("/deletePhoto/{photoNo}")
    public String deletePhoto(@PathVariable("photoNo") Integer photoNo) {
        // 사진 삭제
        photoService.deletePhoto(photoNo);
        return "redirect:/group/photoList";
    }

    /*
	 * 댓글 삭제
	 */
	@PostMapping("/photoCommentDelete")
	@ResponseBody
	public String deletePhotoComment(Model model,
								@RequestBody Integer photoCommentNo,
								HttpSession session) {
		
		PhotoCommentDTO photoCommentDTO = PhotoCommentDTO.builder()
								.photoCommentNo(photoCommentNo)
								.build();
		
		boolean deleted = photoCommentService.deletePhotoComment(photoCommentNo);
		
		String result = "";
		
		if (deleted) {
			result = "success";
		} else {
			result = "fail";
		}
		
		return result;
	}
	
	/*
	 * 댓글 저장  
	 */
	@PostMapping("/phototCommentInsert")
	@ResponseBody
	public String insertPhotoComment (
								@RequestBody PhotoCommentData formData,
								HttpSession session,
								Model model) {
		
		Integer groupNo = formData.getGroupNo();
    	Integer userNo = formData.getUserNo();
    	String photoCommentContent = formData.getPhotoCommentContent(); 
		
    	
    	PhotoCommentDTO photoCommentDTO = PhotoCommentDTO.builder()
    							.photoCommentContent(photoCommentContent)
    							.groupNo(groupNo)
    							.userNo(userNo)
    							.build();
		
		PhotoComment photoComment = photoCommentService.insertPhotoComment(photoCommentDTO);
		
		String result = "";
		
		if(photoComment != null) {
			result = "success";
		} else {
			result = "fail";
		}
		
		return result;
	}
	
	/** ------------------------------------------------------
	 *	작성자: 이민혁
	 *	------------------------------------------------------
	 */
	
	
	@GetMapping("/photoList")
	public String getPhotoList(Model model,
			  @RequestParam("groupNo") Integer groupNo,
			  HttpSession session) {
		// 세션에 회원이 존재하지 않을 경우 로그인 화면으로 이동
		User user = (User) session.getAttribute("user");
	    if (user == null) {
	    	return "redirect:/user/login";
	    }
		GroupDTO groupDTO = groupService.readGroup(groupNo);
	    
	    model.addAttribute("group", groupDTO);
	    model.addAttribute("groupNo", groupNo);
		return "photo/photoList";
	}
	
	/*
	 *	사진 파일 1개를 경로에 업로드 하는 함수
	 */
	@PostMapping("/uploadFile")
	public String uploadFile(Model model, 
							 HttpSession session,
							 @RequestParam("groupNo") Integer groupNo,
							 @RequestParam("multipartFile") MultipartFile multipartFile,
							 @ModelAttribute PhotoDTO photoDTO) {
		
		System.out.println("******************");
		// 컨텐츠 타입이 이미지 파일이 아닌 경우
		if(!multipartFile.getContentType().startsWith("image")) {
			System.out.println("선택하신 파일은 이미지 파일이 아닙니다.");
			return "redirect:/photo/photoList?groupNo=" + groupNo;
		}
		
		// 현재 접속한 회원이 있는지 확인
		User user = (User)session.getAttribute("user");
		if(user == null) {
			System.out.println("세션에 회원 정보가 존재하지 않습니다.");
			System.out.println("로그인 폼으로 이동합니다.");
			return "redirect:/user/login";
		}
		
		// 이미지 파일인 경우 아래의 과정 진행
		String originalFileName = multipartFile.getOriginalFilename();
		
		String contextPath = session.getServletContext().getRealPath("/").replace("\\", "/");
		System.out.println("contextPath: " + contextPath);
		Integer indexOfMain = contextPath.lastIndexOf("/src/main") + "/src/main".length();
		System.out.println("indexOf: " + indexOfMain);
		String uploadPath = contextPath.substring(0, indexOfMain).replace("//", "/") + "/resources/static/image/photo/";
		System.out.println("업로드 경로가 설정되었습니다.");
		
		String uuid = UUID.randomUUID().toString();
		System.out.println("UUID가 랜덤으로 생성되었습니다.");
		String savePathString = uploadPath + uuid + originalFileName; // 데이터베이스에 저장될 문자열
		String SaveThumbnailPathString =  uploadPath + uuid + "s_" +originalFileName; // 's_' 썸네일 약자를 붙임, 데이터베이스에 저장될 문자열
		
		Path savePath = Paths.get(savePathString); // Path 객체 생성
		System.out.println("Path 객체가 생성되었습니다.");
		try {
			multipartFile.transferTo(savePath);
			System.out.println("이미지 파일이 경로에 저장되었습니다.");
			System.out.println("썸네일 객체를 생성합니다.");
			
			File thumbnailFile = new File(SaveThumbnailPathString);
			System.out.println("썸네일 파일 객체를 생성했습니다.");
			
			Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);
			System.out.println("썸네일 이미지가 경로에 저장되었습니다.");
			
			System.out.println("PhotoDTO 객체에 imageURL 문자열을 세팅합니다.");
			photoDTO.setImageUrl(savePathString);
			photoDTO.setThumbnailUrl(SaveThumbnailPathString);
			
			photoService.insertPhoto(photoDTO);
			System.out.println("데이터베이스에 사진 정보가 저장되었습니다.");
			
		} catch (IOException ioe) {
			System.out.println("파일 입출력 에러 발생");
			System.out.println("message: " + ioe.getMessage());
		} catch (Exception e) {
			System.out.println("알 수 없는 에러 발생");
			System.out.println("message: " + e.getMessage());
		}
		
		
		return "redirect:/photo/photoList?groupNo=" + groupNo;
	}
	
	/*
	 *	groupNo에 해당하는 photoList를 가져와서
	 *	rawData(byte[]) 타입으로 페이지에 전달하는 함수
	 */
	@GetMapping("/getPhotoListByGroupNo")
	@ResponseBody
	public ResponseEntity<List<byte[]>> getPhotoListByGroupNo(@RequestParam Integer groupNo) {
		
		List<byte[]> photoDataList = new ArrayList<byte[]>();
		
		List<PhotoDTO> photoDTOList = photoService.getPhotoListByGroupNo(groupNo);
		if(photoDTOList.isEmpty()) {
			System.out.println("데이터베이스에 사진 데이터가 존재하지 않습니다.");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			return new ResponseEntity<>(photoDataList, headers, HttpStatus.OK);
		}
		
		// 데이터가 존재하는 경우 실행될 코드
		System.out.println("데이터베이스에 해당 모임의 사진 정보가 존재합니다.");
		for(PhotoDTO dto : photoDTOList) {
			String imageUrl = dto.getImageUrl();
			File imagePath = new File(imageUrl);
			
			// imageUrl 경로에 해당 파일이 존재하면 결과 리스트에 추가
			if(imagePath.isFile()) {
				System.out.println("path:" + imagePath +"경로에서 파일을 검색했습니다.");
				
				try(FileInputStream fis = new FileInputStream(imagePath)) {
					byte[] rawData = IOUtils.toByteArray(fis); // 파일 객체를 rawData로 변환한다.
					photoDataList.add(rawData); // 변환된 rawData를 리스트에 담는다.
					
				} catch (Exception e) {
					System.out.println("알 수 없는 오류 발생: " + e.getMessage());
				}
				
			} else {
				continue;
			}
			
		} // for end
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return new ResponseEntity<>(photoDataList, headers, HttpStatus.OK);
	}
   
    
}//end PhotoController

@Getter
class PhotoCommentData {
	
	private Integer groupNo;
	private Integer userNo;
	private String photoCommentContent;

}