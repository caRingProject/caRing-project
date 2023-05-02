package com.example.caRing.repository;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.board.AttachedFile;
import com.example.caRing.model.board.Brand;
import com.example.caRing.model.board.Car;

@Mapper
public interface BoardMapper {
	
	
	List<Brand> findBrand();

}
