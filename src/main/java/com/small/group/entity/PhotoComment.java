package com.small.group.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "tbl_photoComment")
public class PhotoComment extends BaseEntityWithTimeStamps {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer photoCommentNo;
	
	@Column(nullable = false)
	private String photoCommentContent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_no")
	private Group group;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_no")
	private User user;

	
}
