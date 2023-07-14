package com.small.group.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 데이터베이스 처리 결과 페이지를 담고 있는 객체
 *  - 객체 생성시 제네릭 타입으로 두개의 객체를 전달받음
 *  - DTO : 화면에서 전달되는 값을 받아서 보관하는 객체
 *  - EN : 데이터베이스 처리 결과를 보관하고 있는 객체
 */
@Data
public class PageResultDTO<DTO, EN> {

    // 데이터베이스 처리 결과 페이지, 카테고리/상품 등 리스트
    private List<DTO> dtoList;

    // 총 페이지 번호
    private int totalPage;

    // 현재 페이지 번호
    private int page;
    // 목록 사이즈
    private int size;

    // 시작 페이지 번호, 끝 페이지 번호
    private int start, end;

    // 이전, 다음
    private boolean prev, next;

    // 페이지 번호  목록
    private List<Integer> pageList;

    /*
     * [생성자]
     *  - 두 개의 파라미터를 전달받는다.
     *  - 하나는 데이터베이스 처리 결과 저장 객체
     *    다른 하나는 함수형 인터페이스인 Function을 사용하여
     *    DTO와 엔티티를 전환하는 행위를 할 파라미터이다.
     */
    public PageResultDTO(Page<EN> result, Function<EN,DTO> fn ){

    	// 데이터베이스 조회 결과를 스트림의 map함수를 사용해서 ArrayList에
    	// 담아주는 역할. map함수는 인자로 전달된 fn 함수인 fn을 
        dtoList = result.stream().map(fn).collect(Collectors.toList());

        totalPage = result.getTotalPages();

        makePageList(result.getPageable());
    }


    private void makePageList(Pageable pageable){

        this.page = pageable.getPageNumber() + 1; // 0부터 시작하므로 1을 더함
        this.size = pageable.getPageSize();

        //temp end page
        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;

        start = tempEnd - 9;

        prev = start > 1;

        end = totalPage > tempEnd ? tempEnd: totalPage;

        next = totalPage > tempEnd;

        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

    }

}
