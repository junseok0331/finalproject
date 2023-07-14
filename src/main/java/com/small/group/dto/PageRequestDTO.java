package com.small.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 요청 페이지에 대한 모든 정보를 담고 있는 클래스
 * @Builder : 클래스를 생성할 때 좀더 편리한 방법을 생성할 수 있음.
 * 	특히 .(쩜)으로 계속 연결해서 멤버 변수에 값을 할당하면서 객체 생성.
 */
@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {

    private int page;
    private int size;
    private String type;
    private String keyword;
    private Integer groupCategoryNo;
    private String groupName;

    public void setGroupCategoryNo(Integer groupCategoryNo) {
        this.groupCategoryNo = groupCategoryNo;
    }
    
    public void setGroupName(String groupName) {
    	this.groupName = groupName;
    }

    public PageRequestDTO(){
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort){

        return PageRequest.of(page -1, size, sort);

    }
}
