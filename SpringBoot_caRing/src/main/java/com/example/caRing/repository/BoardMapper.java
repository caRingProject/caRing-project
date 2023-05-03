package com.example.caRing.repository;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.car.AttachedFile;
import com.example.caRing.model.board.car.Brand;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.board.car.CarType;
import com.example.caRing.model.board.car.Feature;
import com.example.caRing.model.board.car.Fuel;
import com.example.caRing.model.board.car.OptionList;

@Mapper
public interface BoardMapper {
	
	List<Brand> findBrand();
	List<CarType> findCarType();
	List<Fuel> findFuel();
	List<Feature> findFeature();
	
	void saveCar(Car car);
	void saveOption(OptionList optionList);
	void saveFile(AttachedFile file);
	
    void saveBoard(Board board);
    String setTitle(Long carInfo_id);	
    
    Car findCarInfoByEmail(String host_email);
	
}
