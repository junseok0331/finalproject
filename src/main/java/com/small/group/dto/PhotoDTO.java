package com.small.group.dto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Serializable 
 *  - 자바 직렬화란 자바 시스템 내부에서 사용되는 객체 
 *   또는 데이터를 외부의 자바 시스템에서도 사용할 수 있도록 바이트(byte) 형태로
 *   데이터 변환하는 기술과 바이트로 변환된 데이터를 다시 객체로 변환하는 기술(역직렬화)
 *  - 시스템적으로 이야기하자면 JVM(Java Virtual Machine 이하 JVM)의
 *   메모리에 상주(힙 또는 스택)되어 있는 객체 데이터를 바이트 형태로 변환하는 기술과 직렬화된 바이트 형태의 데이터를 객체로 변환해서 JVM으로 상주시키는 형태
 */

@SuppressWarnings("serial")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO implements Serializable {

	private Integer photoNo;
	
	@NotBlank(message = "제목을 입력하세요")
	@Size(min = 1, max = 50, message = "내용은 최대 50글자수만 작성이 가능합니다.")
	private String photoTitle;
	
	private String photoContent;
	//  파일의 저장 경로나 URL을 저장하기 위해서입니다.
	// 이를 통해 엔티티에서는 파일의 실제 저장 경로나 URL을 관리하고, 
	//PhotoDTO 객체를 통해 해당 파일에 대한 참조 정보를 전달
	// 썸네일 이미지 등을 표시해야 할 때 사용될 수 있습니다.
	private String imageUrl;
	
	private String thumbnailUrl;
	
	@NotNull
	private Integer groupNo;
	
	private Integer userNo;
	@NotNull
	private String userName;
	
	/*
	 * 파일 이름은 주로 업로드된 파일을 저장하거나 참조하는 용도로 사용되며,
	 * 데이터베이스에는 파일의 실제 저장 경로 또는 URL을 저장하는 것이 일반적입니다.
	 * 따라서 엔티티에서는 imageUrl 필드를 사용하여 파일의 저장 경로나 URL을 저장하면 됩니다.
	 * 파일 자체는 디스크 또는 클라우드 스토리지 등에 저장하고,
	 * 엔티티는 해당 파일에 대한 참조 정보만 갖도록 설계하는 것이 일반적입니다.
	 */
	private String fileName; // 파일 이름을 저장하는 필드
	private String uuid;
	private String folderPath; // 파일 업로드를 위한 필드(파일 업로드를 위한 데이터를 임시로 전달하기 위한 용도)
	
	/* 수정 07-12 10:55
    public String getImageURL(){
        try {
            return URLEncoder.encode(folderPath+"/"+uuid+"_"+fileName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
	*/
//	public String getImgUrl(){
//	        if(folderPath != null && uuid != null && fileName != null) {
//				try {
//		            return URLEncoder.encode(imageUrl,"UTF-8");
//		        } catch (UnsupportedEncodingException e) {
//		            e.printStackTrace();
//		        }
//			}
//		        return "";
//	    }
	
//    public String getThumbnailURL(){
//        try {
//            return URLEncoder.encode(folderPath+"/s_"+uuid+"_"+fileName,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
	
    // PhotoController 썸네일로 보여줘야 할 때 생성자 사용
//	public PhotoDTO(String fileName, String uuid, String folderPath) {
//		super();
//		this.fileName = fileName;
//		this.uuid = uuid;
//		this.folderPath = folderPath;
//	}

	private LocalDateTime regDate;
	private LocalDateTime modDate;
}
